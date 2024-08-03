package com.itcast.service.Impl;

import com.itcast.mapper.TaskMapper;
import com.itcast.mapper.UserMapper;
import com.itcast.pojo.TaskInstance;
import com.itcast.service.UserService2;
import jakarta.annotation.Resource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserService2Impl implements UserService2 {
    @Autowired
    UserMapper userMapper;
    @Autowired
    TaskMapper taskMapper;
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<String> getTaskInstanceByUserId(Integer userId) {
        long start = System.currentTimeMillis();
        List<String> list = userMapper.getTaskNameByUserId(userId);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return list;
    }

    public List<TaskInstance> insertTaskInstancesList() {
        Random random = new Random();
        List<TaskInstance> list = new ArrayList<TaskInstance>();
        for (int i = 0; i < 10000; i++) {
            int userId = random.nextInt(80000) + 1;
            int index = random.nextInt(6);
            String[] taskNames = {"初级阅读任务", "初级评论任务", "中级阅读任务", "中级评论任务", "高级阅读任务", "高级评论任务"};
            String taskName = taskNames[index];
            int progress = random.nextInt(2) + 1;
            TaskInstance taskInstance = new TaskInstance(1, userId,1, taskName, progress);
            list.add(taskInstance);
        }
        return list;
    }

    @Override
    public void insertTaskInstances(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try{
            TaskMapper taskMapper = sqlSession.getMapper(TaskMapper.class);
            List<TaskInstance> list = insertTaskInstancesList();
            System.out.println("1");
/*            list.stream().forEach(taskInstance -> taskMapper.insertTaskInstances(taskInstance.getUserId(),
                    taskInstance.getTaskName(), taskInstance.getProgress()));*/
            taskMapper.insertTaskInstances(list);
            System.out.println("2");
            sqlSession.commit();
        }catch (Exception e){
            sqlSession.rollback();
        }finally{
            sqlSession.close();
        }
    }
}
