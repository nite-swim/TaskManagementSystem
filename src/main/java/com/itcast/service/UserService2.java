package com.itcast.service;

import java.util.List;

public interface UserService2 {
    List<String> getTaskInstanceByUserId(Integer userId);

    void insertTaskInstances();
}
