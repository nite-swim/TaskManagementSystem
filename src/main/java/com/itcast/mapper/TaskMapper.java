package com.itcast.mapper;

import com.itcast.pojo.TaskUserLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}
