package com.plantrice.forum.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class ForumUtil {

    //生成随机字符串
    public static String generateUUID(){
        //生成随机字符串用的工具uuid，其实可以直接生成，但是要对他稍微进行封装
        //这样就可以随机生成字符串，这个字符串由字母和横线所构成
        //将“-”字符串，替换为空的字符串
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密    有两个体点  1.只能加密，不能解密
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        //spring自带的工具，帮你传入的结果(byte形式的）加密成一个十六进制的字符串返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
