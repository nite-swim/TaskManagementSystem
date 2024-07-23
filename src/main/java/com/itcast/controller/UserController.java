package com.itcast.controller;

import com.itcast.pojo.User;
import com.itcast.service.TaskService;
import com.itcast.service.UserService;
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

    @PostMapping("/login")
    public List<String> login(@ModelAttribute User user) {
        User userLogin = userService.getUserByName(user.getName());
        List<String> list = new ArrayList<>();
        if (userLogin == null){
            list.add("该用户不存在。");
        }else {
            if (userLogin.getPassword().equals(user.getPassword())){
                list = taskService.getTaskByUserId(userLogin.getId());
            }else {
                list.add("密码错误。");
            }
        }
        return list;
    }
}
