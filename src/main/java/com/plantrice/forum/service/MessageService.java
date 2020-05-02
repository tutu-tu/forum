package com.plantrice.forum.service;

import com.plantrice.forum.dao.MessageMapper;
import com.plantrice.forum.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//私信的业务处理层
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    //查询私信列表
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    //查询私信个数
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    //查询会话列表
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    //查询会话个数
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询未读会话个数
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

}
