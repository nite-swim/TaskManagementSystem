package com.itcast.service;

import com.itcast.pojo.TaskUserLevel;

import java.util.List;

public interface TaskService {
    List<String> getTaskByUserId(int userId);

    void finishTask(Integer userId);

    TaskUserLevel getTaskInfoById(Integer taskId);
}
