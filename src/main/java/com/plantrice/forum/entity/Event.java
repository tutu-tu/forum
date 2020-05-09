package com.plantrice.forum.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 对事件触发时相关的信息进行封装
 * 封装事件的实体
 */
public class Event {

    private String topic;           //事件的类型，Kafka的主题
    private int userId;          //事件触发的人
    private int entityType;      //实体类型，是点赞，关注，还是评论
    private int entityId;
    private int entityUserId;   //实体作者
    private Map<String,Object> data = new HashMap<>();      //剩余的数据存到map中

    public String getTopic() {
        return topic;
    }

    /**
     * 这样做的好处就是，当我们在调用的时候，set到topic，肯定还要set其他的属性，
     * set完topic，又返回到当前对象，再由当前对象调用其他的set方法。
     * @param topic
     * @return
     */
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}
