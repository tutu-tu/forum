package com.plantrice.forum.service;

import com.plantrice.forum.dao.UserMapper;
import com.plantrice.forum.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//业务逻辑层
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    //根据id查询用户信息
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

}
