package com.plantrice.forum.dao;

import com.plantrice.forum.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //通过实体类查询评论总数,分页
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询数据的条目数
    int selectCountByEntity(int entityType, int entityId);

    //添加一条评论
    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
