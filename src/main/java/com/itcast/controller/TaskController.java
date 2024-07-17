package com.itcast.controller;

import com.itcast.pojo.Task;
import com.itcast.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/task")
@Validated
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户查询任务列表
     * @param userId
     * @return 该用户等级对应的任务列表
     */
    @GetMapping("/userId")
    public List<String> getTaskByUserId(@RequestParam Integer userId) {
        List<String> result = taskService.getTaskByUserId(userId);
        return result;
    }

    @PutMapping
    public String finishTask(@RequestParam Integer userId) {
        taskService.finishTask(userId);
        return "OK";
    }

    @GetMapping("/taskId")
    public Task getTaskInfoById(@RequestParam Integer taskId) {
        Task result = taskService.getTaskInfoById(taskId);
        return result;
    }
}
