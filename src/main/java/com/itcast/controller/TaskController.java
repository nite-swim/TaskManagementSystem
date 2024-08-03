package com.itcast.controller;

import com.itcast.pojo.Task;
import com.itcast.pojo.TaskUserLevel;
import com.itcast.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 用户完成任务并获得奖励、进行升级
     * @param userId
     * @return
     */
    @PutMapping
    public String finishTask(@RequestParam Integer userId) {
        taskService.finishTask(userId);
        return "OK";
    }

    /**
     * 根据任务id查询任务信息
     * @param taskId
     * @return
     */
    @GetMapping("/taskId")
    public TaskUserLevel getTaskInfoById(@RequestParam Integer taskId) {
        TaskUserLevel result = taskService.getTaskInfoById(taskId);
        return result;
    }


    /**
     * 新增不同类型的任务
     * @param task
     * @return "OK"
     */
    @PostMapping("/newTask")
    public String addTask(@ModelAttribute Task task){
        return taskService.addTask(task);
    }
}
