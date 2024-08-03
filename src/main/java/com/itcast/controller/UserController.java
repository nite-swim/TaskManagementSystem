package com.itcast.controller;

import com.itcast.pojo.User;
import com.itcast.service.TaskService;
import com.itcast.service.UserService;
import com.itcast.service.UserService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService2 userService2;

    /**
     * 用户登录验证
     *
     * @param user
     * @return list
     */
    @PostMapping("/login")
    public List<String> login(@ModelAttribute User user) {
        User userLogin = userService.getUserByName(user.getName());
        List<String> list = new ArrayList<>();
        if (userLogin == null) {
            list.add("该用户不存在。");
        } else {
            if (userLogin.getPassword().equals(user.getPassword())) {
                list = taskService.getTaskByUserId(userLogin.getId());
            } else {
                list.add("密码错误。");
            }
        }
        return list;
    }

    /**
     * 多个用户轮流完成任务并记录任务的完成进度，用户升级、获得奖励等
     */
    @GetMapping
    public void multiUserFinishTask() {
        userService.multiUserFinishTask();
    }

    /**
     * 通过用户id查询其已经完成的任务（测试）
     *
     * @param userId
     * @return 任务名列表
     */
    @GetMapping("/userId")
    public List<String> getTaskInstanceByUserId(@RequestParam Integer userId) {
        return userService2.getTaskInstanceByUserId(userId);
    }

    /**
     * （测试）
     * 自动生成任务实例，由于给实际用户生成任务实例涉及到用户的升级、奖励等数据的更新
     * 考虑到此功能仅做测试用，所以生成的数据一律存到task_instance_test表中
     */
    @PostMapping("/genRecords")
    public void genRecords() {
        /*long start = System.currentTimeMillis();
        userService2.insertTaskInstances();
        long end = System.currentTimeMillis();
        System.out.println(end - start);*/
        for (int i = 0; i < 1967; i++) {
            userService2.insertTaskInstances();
            System.out.println("还剩"+(1967-i)+"次");
        }
    }

    /**
     * 根据用户名查询已经完成的任务
     *
     * @param userName
     * @return 任务名列表
     */
    @GetMapping("/userName")
    public List<String> getFinishedTasksByUserName(@RequestParam String userName) {
        return userService.getFinishedTasksByUserName(userName);
    }
}
