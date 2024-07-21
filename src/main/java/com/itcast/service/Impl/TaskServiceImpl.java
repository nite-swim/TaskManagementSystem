package com.itcast.service.Impl;

import com.itcast.mapper.TaskMapper;
import com.itcast.pojo.Task;
import com.itcast.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Task> redisTemplate;

    public Integer getUserLevelById(Integer id) {
        if (stringRedisTemplate.opsForValue().get("user:id:" + id)!=null){
            return Integer.parseInt(stringRedisTemplate.opsForValue().get("user:id:" + id));
        }else{
            int level = taskMapper.getUserLevel(id);
            stringRedisTemplate.opsForValue().set("user:id:" + id, String.valueOf(level));
            return level;
        }
    }

    public void updateUserLevelById(Integer id) {
        if (stringRedisTemplate.opsForValue().get("user:id:" + id)!=null){
            System.out.println("Redis更新用户等级");
            stringRedisTemplate.opsForValue().increment("user:id:" + id);
        }else {
            getUserLevelById(id);
        }
        return;
    }
    @Override
    public List<String> getTaskByUserId(int userId) {
        List<String> resultList = new ArrayList<>();
        String userLevel = String.valueOf(getUserLevelById(userId));
        System.out.println(userLevel);
        if (stringRedisTemplate.opsForHash().hasKey("user:level:" + userLevel, userLevel)) {
            resultList.add((String) stringRedisTemplate.opsForHash().get("user:level:" + userLevel, userLevel));
            System.out.println("From Redis");
            return resultList;
        }else {
            List<Task> taskList = taskMapper.getTaskByUserId(userId);
            for (Task task : taskList) {
                resultList.add("任务名："+ task.getTaskName() + "，任务奖励：" + task.getTaskReward());
            }
            stringRedisTemplate.opsForHash().put("user:level:" + userLevel, userLevel, resultList.toString());
            return resultList;
        }
    }

    @Override
    public void finishTask(Integer userId) {
        System.out.println("等待输入指令（GO+数字序号）：");
        Scanner sc = new Scanner(System.in);
        int userLevel = getUserLevelById(userId);
        if (sc.nextLine().matches("^GO\\d+$")){
            System.out.println("请选择任务（Read Action/Write Action）：");
            levelUpAndGainReward(sc, userLevel, userId);
        }else {
            System.out.println("输入有误，请重新输入。");
        }
    }



    private void levelUpAndGainReward(Scanner sc, int userLevel, int userId) {
        String command = sc.nextLine();
        if (command.equalsIgnoreCase("Read Action")){
            taskMapper.updateLevel(userId);
            updateUserLevelById(userId);
            if (userLevel < 3){
                //gold+1
                taskMapper.updateGoldPrimary(userId);
            } else if (2 < userLevel && userLevel < 5) {
                //gold+2, redPocket+1
                taskMapper.updateGoldIntermediate(userId);
                taskMapper.updateRedPocket(1,userId);
            }else {
                //gold+3, redPocket+5
                taskMapper.updateGoldAdvanced(userId);
                taskMapper.updateRedPocket(5,userId);
            }
            System.out.println("任务已完成！");
        }else if (command.equalsIgnoreCase("Write Action")){
            taskMapper.updateLevel(userId);
            updateUserLevelById(userId);
            if (userLevel < 3){
                //gold+1
                taskMapper.updateGoldPrimary(userId);
            }else if (2 < userLevel && userLevel < 5) {
                //gold+2
                taskMapper.updateGoldIntermediate(userId);
            }else {
                //gold+3
                taskMapper.updateGoldAdvanced(userId);
            }
            System.out.println("任务已完成！");
        }else {
            System.out.println("输入有误，请重新输入。");
        }
    }

    @Override
    public Task getTaskInfoById(Integer taskId) {
        Task task = new Task();
        if (redisTemplate.opsForValue().get("task:id:" + taskId) != null){
            task = redisTemplate.opsForValue().get("task:id:" + taskId);
            return task;
        }else {
            task = taskMapper.getTaskInfoById(taskId);
            redisTemplate.opsForValue().set("task:id:" + taskId, task);
            return task;
        }
    }
}
