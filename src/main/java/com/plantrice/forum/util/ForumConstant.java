package com.plantrice.forum.util;

//激活码的一些状态(常量）
public interface ForumConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证超时 12小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下的登录凭证超时时间，5天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 5;

    /**
     * 实体类型：评论帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
