package com.plantrice.forum.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 生成随机字符串和加密
 */
public class ForumUtil {

    //生成随机字符串
    public static String generateUUID(){
        //生成随机字符串用的工具uuid，其实可以直接生成，但是要对他稍微进行封装
        //这样就可以随机生成字符串，这个字符串由字母和横线所构成
        //将“-”字符串，替换为空的字符串
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密    有两个体点  1.只能加密，不能解密
    //isBlank 判断是否为空白
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        //spring自带的工具，帮你传入的结果(byte形式的）加密成一个十六进制的字符串返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //json格式转换成字符串
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    //对不同的需求实现方法的重载
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    /*public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }*/
}
