package com.itcast.service;

import com.itcast.pojo.User;

import java.util.List;

public interface UserService {
    User getUserByName(String username);

    void multiUserFinishTask();

    List<String> getFinishedTasksByUserName(String username);
}
