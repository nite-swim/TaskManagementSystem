package com.itcast.service.Impl;

import com.itcast.mapper.RewardMapper;
import com.itcast.mapper.TaskMapper;
import com.itcast.mapper.UserMapper;
import com.itcast.pojo.Reward;
import com.itcast.pojo.Task;
import com.itcast.pojo.TaskInstance;
import com.itcast.pojo.User;
import com.itcast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskServiceImpl taskServiceImpl;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RewardMapper rewardMapper;

    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public void multiUserFinishTask() {
        while (true) {
            System.out.println("请输入用户名：");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.nextLine();
            User user = userMapper.getUserByName(username);
            int level = user.getLevel();
            if (level > 5){
                level = 5;
            }
            List<String> taskList = taskMapper.getTaskNameByUserLevel(level);
            System.out.print("你可以完成的任务有：");
            for (String taskName : taskList) {
                System.out.print((taskList.indexOf(taskName) + 1) + "." + taskName);
            }
            System.out.println();
            System.out.print("请输入序号选择任务：GO");
            int command = scanner.nextInt();
            if (command < 1 || command > taskList.size()) {
                System.out.println("指令有误，请重新输入");
                return;
            } else {
                String taskName = taskList.get(command - 1);
                Task task = taskMapper.getTaskByTaskName(taskName);
                int currentProgress = getCurrentProgress(user, task);
                System.out.print(task.getTaskGoal());
                System.out.println("(当前任务完成进度：" + currentProgress + "/" + task.getTime() + ")");
                if(currentProgress/task.getTime() == 1){
                    System.out.println("当前任务已完成");
                    return;
                }
                progressRecord(task, user);
            }
        }
    }


    /**
     * 根据用户id和任务id定位到该用户选择的任务，获取任务完成进度
     * @param user
     * @param task
     * @return
     */
    private Integer getCurrentProgress(User user, Task task) {
        int userId = user.getId();
        int taskId = task.getId();
        Integer currentProgress = userMapper.getTaskInstanceProgress(userId, taskId);
        if (currentProgress == null){
            return 0;
        }else {
            return currentProgress;
        }
    }

    /**
     * 记录用户完成任务的进度，若表中原先没有该用户完成该任务的记录，就插入新纪录，否则就更新记录，并且更新用户等级和奖励
     * @param task
     * @param user
     */
    private void progressRecord(Task task, User user) {
        int userId = user.getId();
        Scanner scanner = new Scanner(System.in);
        int taskId = task.getId();
        String taskName = task.getTaskName();
        if (scanner.nextLine().equalsIgnoreCase(task.getTaskType())) {
            if (hasRecord(userId, taskId)){
                if (userMapper.getTaskInstanceProgress(userId, taskId) == task.getTime()){
                    System.out.println("该任务已完成");
                }else {
                    //若表里有数据则更新progress
                    userMapper.updateTaskInstance(userId, taskId);
                    if (userMapper.getTaskInstanceProgress(userId, taskId) == task.getTime()){
                        System.out.println("该任务已完成");
                        //升级、奖励
                        taskMapper.updateLevel(userId);
                        taskServiceImpl.updateUserLevelById(userId);
                        userGainReward(user, task);
                    }
                }
            }else {
                //若表里没有数据则插入数据
                TaskInstance taskInstance = new TaskInstance(1, userId, task.getId(), taskName, 1);
                userMapper.insertTaskInstance(taskInstance);
            }
        }
    }

    /**
     * 根据用户id和任务id判断task_instance表中是否有对应的记录
     * @param userId
     * @param taskId
     * @return
     */
    boolean hasRecord(Integer userId, Integer taskId) {
        if (userMapper.getTaskInstanceByUserIdAndTaskId(userId, taskId) != null){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 用户完成任务后获得奖励
     * 为了支持新增任务类型功能，新增了reward类，实现了奖励和任务的解耦
     * @param user
     * @param task
     */
    void userGainReward(User user, Task task) {
        String taskName = task.getTaskName();
        Reward reward = rewardMapper.getRewardByTaskName(taskName);
        int rewardGold = reward.getRewardGold();
        int rewardRedPocket = reward.getRewardRedPocket();
        int userId = user.getId();
        userMapper.userGainReward(rewardGold, rewardRedPocket, userId);
    }

    /**
     * 根据用户名查询已完成的任务
     * 用户id->用户执行过的任务->任务名列表->通过任务名获取完成任务需要执行任务的次数->如果（任务实例进度/次数）==1->任务完成，添加到结果列表中
     * @param username
     * @return
     */
    @Override
    public List<String> getFinishedTasksByUserName(String username) {
        int userId = userMapper.getUserByName(username).getId();
        List<TaskInstance> taskInstances = userMapper.getTaskInstanceByUserId(userId);
        List<String> finishedTaskNames = new ArrayList<>();
        for (TaskInstance taskInstance : taskInstances) {
            String taskName = taskInstance.getTaskName();
            int progress = taskInstance.getProgress();
            int time = userMapper.getTimeByTaskName(taskName);
            if (progress/time == 1){
                finishedTaskNames.add(taskName);
            }
        }
        return finishedTaskNames;
    }

}
