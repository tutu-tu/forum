package com.plantrice.forum.controller;

import com.plantrice.forum.entity.DiscussPost;
import com.plantrice.forum.entity.Page;
import com.plantrice.forum.entity.User;
import com.plantrice.forum.service.DiscussPostService;
import com.plantrice.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//视图层，有controller，数据模型和页面
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    //requestMapper 访问路径    method 请求方式
    //方法响应的是网页，不能写@RespondBody
    //返回类型 String 返回的是视图的名字
    //model携带数据给模板
    //主页数据和数据分页
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.不需要再加model.addAttribute("page", page);
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        //分页，把discussPost组装一下，放到新建的list<map<>>中，这个集合是能够封装discussPost的以及user对象的那个一个集合对象
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                //调用userService把user查出来
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        //返回模板的路径
        return "/index";
    }

}
