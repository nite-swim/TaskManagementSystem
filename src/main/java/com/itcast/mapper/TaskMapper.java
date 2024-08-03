package com.itcast.mapper;

import com.itcast.pojo.Task;
import com.itcast.pojo.TaskInstance;
import com.itcast.pojo.TaskUserLevel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {
    @Select("select task_userlevel.task_name, task_userlevel.task_reward from task_userlevel where user_level = (select user.level from user where user.id = #{userId})")
    List<TaskUserLevel> getTaskByUserId(int userId);

    @Select("select user.level from user where id = #{userId}")
    Integer getUserLevel(int userId);

    @Update("update user set gold = gold +1 where id = #{userId}")
    void updateGoldPrimary(int userId);

    @Update("update user set gold = gold +2 where id = #{userId}")
    void updateGoldIntermediate(int userId);

//    @Update("update user set red_pocket = red_pocket + 1 where id = #{userId}")
//    void updateRedPocketIntermediate(int userId);

    @Update("update user set gold = gold +3 where id = #{userId}")
    void updateGoldAdvanced(int userId);

//    @Update("update user set red_pocket = red_pocket + 5 where id = #{userId}")
//    void updateRedPocketAdvanced(int userId);

    @Update("update user set level = level +1 where id = #{userId}")
    void updateLevel(int userId);

    @Select("select task_userlevel.task_name, task_userlevel.task_type, task_userlevel.task_reward from task_userlevel where id = #{taskId}")
    TaskUserLevel getTaskInfoById(int taskId);

    @Update("update user set red_pocket = red_pocket + #{money} where id = #{userId}")
    void updateRedPocket(@Param("money") int money,@Param("userId") int userId);

    @Select("select task.task_name from task where user_level = #{userLevel};")
    List<String> getTaskNameByUserLevel(int userLevel);

    @Select("select * from task where task_name = #{taskName}")
    Task getTaskByTaskName(String taskName);

    //@Insert("insert into task_instance_test (user_id, task_name, progress) values (#{userId}, #{taskName}, #{progress});")
    //void insertTaskInstances(@Param("userId") int userId, @Param("taskName") String taskName, @Param("progress") int progress);
    void insertTaskInstances(List<TaskInstance> taskInstances);


    @Insert("insert into task (task_name, task_type, task_reward, user_level, task_goal, time) values (#{taskName}, #{taskType}, #{taskReward}, #{userLevel}, #{taskGoal}, #{time})")
    void addTask(@Param("taskName") String taskName, @Param("taskType") String taskType, @Param("taskReward") String taskReward,
                 @Param("userLevel") int userLevel, @Param("taskGoal") String taskGoal, @Param("time") int time);

    @Insert("insert into reward (task_name, reward_gold, reward_red_pocket) values (#{taskName}, #{rewardGold}, #{rewardRedPocket})")
    void addReward(@Param("taskName") String taskName, @Param("rewardGold") int rewardGold, @Param("rewardRedPocket") int rewardRedPocket);
}
