package com.plantrice.forum.dao;

import com.plantrice.forum.entity.User;
import org.apache.ibatis.annotations.Mapper;

//数据访问层
//写@Mapper和@Repository都可以
@Mapper
public interface UserMapper {

    //通过id查找user信息
    User selectById(int id);
    //通过用户名查找user信息
    User selectByName(String username);
    //通过邮箱找到user信息
    User selectByEmail(String email);
    //增加一个用户，返回增加的行数，多少条
    int insertUser(User user);
    //修改状态，返回修改的行数，多少条
    int updateStatus(int id, int status);
    //跟新头像的路径
    int updateHeader(int id, String headerUrl);
    //更新密码
    int updatePassword(int id, String password);

}
