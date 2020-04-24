package com.plantrice.forum;

import com.plantrice.forum.dao.UserMapper;
import com.plantrice.forum.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class ForumApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder103@sina.com");
        System.out.println(user);

        user = userMapper.selectByName("bbb");
        System.out.println(user);
    }

    //新增用户
    @Test
    public void testInserUser(){
        User user = new User();
        user.setUsername("测试");
        user.setPassword("123123");
        user.setSalt("abc");
        user.setEmail("pdd@163.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    //跟新测试
    @Test
    public void updateUser(){
       int rows = userMapper.updateStatus(150,1);
       System.out.println(rows);

       rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
       System.out.println(rows);

       rows = userMapper.updatePassword(150,"123456");
       System.out.println(rows);

    }
}
