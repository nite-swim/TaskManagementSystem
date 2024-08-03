package com.itcast.mapper;

import com.itcast.pojo.TaskInstance;
import com.itcast.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where name = #{name}")
    User getUserByName(String name);

/*    @Select("select user.level from user where name = #{name}")
    Integer getUserLevelByName(String name);*/

    @Select("select task_instance.progress from task_instance where user_id = #{userId} and task_id = #{taskId}")
    TaskInstance getTaskInstanceByUserIdAndTaskId(@Param("userId") int userId, @Param("taskId") int taskId);

    @Insert("insert into task_instance (user_id, task_id, task_name, progress) values (#{userId}, #{taskId}, #{taskName}, #{progress})")
    void insertTaskInstance(TaskInstance taskInstance);

    @Update("update task_instance set progress = progress + 1 where user_id = #{userId} and task_id = #{taskId}")
    void updateTaskInstance(@Param("userId") int userId, @Param("taskId") int taskId);

    @Select("select task_instance.progress from task_instance where user_id = #{userId} and task_id = #{taskId}")
    Integer getTaskInstanceProgress(@Param("userId") int userId, @Param("taskId") int taskId);

    @Update("update user set gold = gold + #{rewardGold}, red_pocket = red_pocket + #{rewardRedPocket} where id = #{userId}")
    void userGainReward(@Param("rewardGold") int rewardGold, @Param("rewardRedPocket") int rewardRedPocket, @Param("userId") int userId);

    @Select("select task_name from task_instance_test where user_id = #{userId} and progress = 2;")
    List<String> getTaskNameByUserId(@Param("userId") int userId);

    @Select("select task_name, progress from task_instance where user_id = #{userId}")
    List<TaskInstance> getTaskInstanceByUserId(@Param("userId") int userId);

    @Select("select time from task where task_name = #{taskName}")
    int getTimeByTaskName(@Param("taskName") String taskName);
}
