package com.itcast.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private int id;
    private String taskName;
    private int rewardGold;
    private int rewardRedPocket;
}
