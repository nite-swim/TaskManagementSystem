package com.itcast.service;

import com.itcast.pojo.Task;

import java.util.List;

public interface TaskService {
    List<String> getTaskByUserId(int userId);

    void finishTask(Integer userId);

    Task getTaskInfoById(Integer taskId);
}
