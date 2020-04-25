package com.plantrice.forum.controller;

import com.plantrice.forum.entity.User;
import com.plantrice.forum.service.UserService;
import com.plantrice.forum.util.ForumConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements ForumConstant {

    @Autowired
    private UserService userService;

    //获取注册路径，返回模板页面，访问注册页面
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getReqisterPage(){
        return "/site/register";
    }

    //访问登录页面
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //提交注册
    //页面的值只要和user的属性相互匹配，springmvc就会自动的把这个值注入给user对象里面的属性
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        //注册成功的情况
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            //跳转目标
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    //http://localhost:8686/forum/activation(功能）/101（用户id）/code（激活码）
    //@PathVariable("userId")从请求路径中取值
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId")int userId,
                             @PathVariable("code")String code){
        int result = userService.activation(userId,code);
        //激活成功
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活，您的账号可以正常使用了");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT ){
            model.addAttribute("msg","无效操作，该账号已经激活过了");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
