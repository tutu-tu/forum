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
}
