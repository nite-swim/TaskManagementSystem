package com.itcast.service.Impl;

import com.itcast.mapper.TaskMapper;
import com.itcast.pojo.Task;
import com.itcast.pojo.TaskUserLevel;
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
    private RedisTemplate<String, TaskUserLevel> redisTemplate;

    /**
     * 根据任务id查询对应的用户等级
     * @param id
     * @return userLevel
     */
    public Integer getUserLevelById(Integer id) {
        if (stringRedisTemplate.opsForValue().get("user:id:" + id)!=null){
            return Integer.parseInt(stringRedisTemplate.opsForValue().get("user:id:" + id));
        }else{
            int level = taskMapper.getUserLevel(id);
            stringRedisTemplate.opsForValue().set("user:id:" + id, String.valueOf(level));
            return level;
        }
    }

    /**
     * 根据用户id给用户升级
     * @param id
     */
    public void updateUserLevelById(Integer id) {
        if (stringRedisTemplate.opsForValue().get("user:id:" + id)!=null){
            System.out.println("Redis更新用户等级");
            stringRedisTemplate.opsForValue().increment("user:id:" + id);
        }else {
            int level = taskMapper.getUserLevel(id);
            stringRedisTemplate.opsForValue().set("user:id:" + id, String.valueOf(level));
        }
    }

    /**
     * 根据用户id查询他可以完成的任务列表
     * @param userId
     * @return
     */
    @Override
    public List<String> getTaskByUserId(int userId) {
        List<String> resultList = new ArrayList<>();
        int level = getUserLevelById(userId);
        if (level>5){
            resultList.add((String) stringRedisTemplate.opsForHash().get("user:level:" + "5", "5"));
            return resultList;
        }else {
            String userLevel = String.valueOf(level);
            System.out.println(userLevel);
            if (stringRedisTemplate.opsForHash().hasKey("user:level:" + userLevel, userLevel)) {
                resultList.add((String) stringRedisTemplate.opsForHash().get("user:level:" + userLevel, userLevel));
                System.out.println("From Redis");
                return resultList;
            }else {
                List<TaskUserLevel> taskUserLevelList = taskMapper.getTaskByUserId(userId);
                for (TaskUserLevel taskUserLevel : taskUserLevelList) {
                    resultList.add("任务名："+ taskUserLevel.getTaskName() + "，任务奖励：" + taskUserLevel.getTaskReward());
                }
                stringRedisTemplate.opsForHash().put("user:level:" + userLevel, userLevel, resultList.toString());
                return resultList;
            }
        }
    }

    /**
     * 模拟用户完成任务的整个过程：用户输入指令->判断用户选择的任务->用户输入任务需要的指令->用户升级并获得奖励
     * 此方法硬编码程度较高，如果新增了不同类型的用户可选择的任务，该方法也只会展示原来的两个任务
     * UserServiceImpl中的progressRecord()方法基本上解决了此问题
     * @param userId
     */
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


    /**
     * 用户完成任务后升级并获得奖励，此方法获得奖励的代码也存在和上一个方法相似的硬编码问题，
     * UserServiceImpl中的userGainReward()方法基本上解决了这个问题
     * @param sc
     * @param userLevel
     * @param userId
     */
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

    /**
     * 根据任务id获取任务信息
     * @param taskId
     * @return
     */
    @Override
    public TaskUserLevel getTaskInfoById(Integer taskId) {
        TaskUserLevel taskUserLevel = new TaskUserLevel();
        if (redisTemplate.opsForValue().get("task:id:" + taskId) != null){
            taskUserLevel = redisTemplate.opsForValue().get("task:id:" + taskId);
            return taskUserLevel;
        }else {
            taskUserLevel = taskMapper.getTaskInfoById(taskId);
            redisTemplate.opsForValue().set("task:id:" + taskId, taskUserLevel);
            return taskUserLevel;
        }
    }

    /**
     * 新增：添加不同类型的任务
     * 另外创建了一个Reward类实现了任务和奖励的解耦，不过把Reward类中的
     * rewardGold和rewardRedPocket两列直接加到原来的task表中好像也可以，
     * 考虑到那样可能会改变表结构和之前方法的一些输出结果，还是另外新建了一个表比较稳妥
     * @param task
     * @return
     */
    @Override
    public String addTask(Task task) {
        taskMapper.addTask(task.getTaskName(), task.getTaskType(), task.getTaskReward(),task.getUserLevel(), task.getTaskGoal(), task.getTime());
        System.out.println("请输入金币奖励数目：");
        Scanner sc = new Scanner(System.in);
        int rewardGold = sc.nextInt();
        System.out.println("请输入红包奖励钱数：");
        int rewardRedPocket = sc.nextInt();
        taskMapper.addReward(task.getTaskName(), rewardGold, rewardRedPocket);
        return "OK";
    }
}
