package com.plantrice.forum.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Cookie判断的工具类
 */
public class CookieUtil {

    //从cookie中取值，首先传入request对象，告诉你要取得值的name是什么
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }

        //得到所有的cookie对象，是一个数组
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                //cookie的值是否等于传入的参数
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        //没有你要的数据，return null
        return null;
    }

}
