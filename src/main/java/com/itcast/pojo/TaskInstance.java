package com.itcast.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInstance {
    private int id;
    private int userId;
    private int taskId;
    private String taskName;
    private int progress;
}
