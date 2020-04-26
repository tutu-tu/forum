package com.plantrice.forum.util;

import com.plantrice.forum.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.
 * 起到容器的作用
 * ThreadLocal线程隔离，以线程为key存储值得
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    //存值
    public void setUser(User user) {
        users.set(user);
    }

    //取值
    public User getUser() {
        return users.get();
    }

    //请求结束，将threadLocal中的user清理掉
    public void clear() {
        users.remove();
    }

}
