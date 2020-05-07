package com.plantrice.forum.controller;

import com.plantrice.forum.entity.Comment;
import com.plantrice.forum.entity.DiscussPost;
import com.plantrice.forum.entity.Page;
import com.plantrice.forum.entity.User;
import com.plantrice.forum.service.CommentService;
import com.plantrice.forum.service.DiscussPostService;
import com.plantrice.forum.service.LikeService;
import com.plantrice.forum.service.UserService;
import com.plantrice.forum.util.ForumConstant;
import com.plantrice.forum.util.ForumUtil;
import com.plantrice.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

//帖子的控制层
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements ForumConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    //发布帖子的接口，
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return ForumUtil.getJSONString(403,"您还没有登录哦！请去完成登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.insertDiscussPost(post);
        return ForumUtil.getJSONString(1,"发布成功！");
    }

    //进入帖子详情页面
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId,
                                 Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user =  userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeCount",likeCount);
        //如果没有用户信息，没有登录，但是还是要显示赞，状态返回0，
        int likeStatus = hostHolder.getUser() == null ? 0 :
            likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeStatus",likeStatus);
        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        //得到当前帖子的所有评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());
        //显示的对象 vule Object  ，用来封装数据
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList) {
                //一个评论的显示对象
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //如果没有用户信息，没有登录，但是还是要显示赞，状态返回0，
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //评论的评论（回复，二级评论）从零开始 Integer.MAX_VALUE 有多少查多少
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList) {
                        //一个回复显示的对象
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //回复的作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标  处理target == 0 说明没有回复目标
                       User target =  reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                       replyVo.put("target",target);
                        //点赞
                       likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //如果没有用户信息，没有登录，但是还是要显示赞，状态返回0，
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                       //把最终的数据装到集合里
                       replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        //得到最终的结果，放到model里，传给最终的模板,comments 多条评论（评论帖子，评论评论）
        model.addAttribute("comments",commentVoList);
        return "site/discuss-detail";

    }

}
