package com.itcast.mapper;

import com.itcast.pojo.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TaskMapper {
    @Select("select task.task_name, task.task_reward from task where user_level = (select user.level from user where user.id = #{userId})")
    List<Task> getTaskByUserId(int userId);

    @Select("select user.level from user where id = #{userId}")
    Integer getUserLevel(int userId);

    @Update("update user set gold = gold +1 where id = #{userId}")
    void updateGoldPrimary(int userId);

    @Update("update user set gold = gold +2 where id = #{userId}")
    void updateGoldIntermediate(int userId);

    @Update("update user set red_pocket = red_pocket + 1 where id = #{userId}")
    void updateRedPocketIntermediate(int userId);

    @Update("update user set gold = gold +3 where id = #{userId}")
    void updateGoldAdvanced(int userId);

    @Update("update user set red_pocket = red_pocket + 5 where id = #{userId}")
    void updateRedPocketAdvanced(int userId);

    @Update("update user set level = level +1 where id = #{userId}")
    void updateLevel(int userId);

    @Select("select task.task_name, task.task_type, task.task_reward from task where id = #{taskId}")
    Task getTaskInfoById(int taskId);
}
