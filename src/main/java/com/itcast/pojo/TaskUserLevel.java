package com.itcast.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskUserLevel implements Serializable {
    private Integer id;
    private Integer userLevel;
    private String taskName;
    private String taskType;
    private String taskReward;
}
