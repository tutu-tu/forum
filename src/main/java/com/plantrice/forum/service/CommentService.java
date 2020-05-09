package com.plantrice.forum.service;

import com.plantrice.forum.dao.CommentMapper;
import com.plantrice.forum.entity.Comment;
import com.plantrice.forum.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.plantrice.forum.util.ForumConstant.ENTITY_TYPE_POST;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //查询所有的评论（帖子的，评论的）
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    //评论的条目数
    public int findCommentCountByEntity(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //增加评论（帖子or评论） isolation隔离性，读已提交 propagation传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    //通过id查询整张表的数据
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

}
