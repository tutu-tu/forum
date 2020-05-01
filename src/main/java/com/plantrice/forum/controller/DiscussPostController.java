package com.plantrice.forum.controller;

import com.plantrice.forum.entity.DiscussPost;
import com.plantrice.forum.entity.User;
import com.plantrice.forum.service.DiscussPostService;
import com.plantrice.forum.service.UserService;
import com.plantrice.forum.util.ForumUtil;
import com.plantrice.forum.util.HostHolder;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import java.util.Date;

//帖子的控制层
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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

    @RequestMapping(path = "/ditail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId,
                                 Model model){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        User user =  userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        return "site/discuss-detail";

    }

}
