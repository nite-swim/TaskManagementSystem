package com.itcast.mapper;

import com.itcast.pojo.Reward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RewardMapper {
    @Select("select reward.reward_gold, reward_red_pocket from reward where task_name = #{taskName}")
    Reward getRewardByTaskName(String taskName);
}
