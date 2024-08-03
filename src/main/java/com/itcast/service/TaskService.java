package com.itcast.service;

import com.itcast.pojo.Task;
import com.itcast.pojo.TaskUserLevel;
import com.itcast.pojo.User;

import java.util.List;

public interface TaskService {
    List<String> getTaskByUserId(int userId);

    void finishTask(Integer userId);

    TaskUserLevel getTaskInfoById(Integer taskId);

    String addTask(Task task);
}
