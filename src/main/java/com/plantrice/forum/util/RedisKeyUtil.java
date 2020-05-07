package com.plantrice.forum.util;

import org.springframework.stereotype.Component;

/**
 * 生成redisKey的工具类
 */
public class RedisKeyUtil {

    //key在拼接的时候以冒号来分隔 split(分裂，分隔）
    private static final String SPLIT = ":";
    //帖子赞和详情赞统称为实体，要存实体的赞
    //前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    //某个实体的赞的key
    //like:entity:entityType:entityId  -->set(userId)  将来可能通过userId去查找谁点的赞
    public static String getEntityKeyLike(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户收到的赞
    //like:user:userId   -->int
    public static String getUserKeyLike(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    //某用户关注的实体（用户，帖子等信息，所以是实体）
    //followee:userId:entityType  -->Zset(entityId,now date())
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    //follower：entityType:entityId  -->Zset(userId,now date())
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //登录验证码
    //获取登录验证码的时候和某一个用户是相关的，要识别这个验证码是输入哪个用户的
    //当用户看到验证码的时候还没登陆，没有用户信息的，不知道userId的，
    //可以通过给用户一个随机的字符串，存到cookie中，以这个字符串来标识这个用户。
    //kaptcha:随机字符串
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //登录凭证
    public static String getTicketKey(String ticiet){
        return PREFIX_TICKET+SPLIT+ticiet;
    }

    //缓存用户数据
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }
}
