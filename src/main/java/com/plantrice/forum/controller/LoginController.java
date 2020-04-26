package com.plantrice.forum.controller;

import com.google.code.kaptcha.Producer;
import com.plantrice.forum.entity.User;
import com.plantrice.forum.service.LoginTicketService;
import com.plantrice.forum.service.UserService;
import com.plantrice.forum.util.ForumConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
public class LoginController implements ForumConstant {

    //创建日志，以当前类名命明
    private static final Logger logger = LoggerFactory.getLogger(ForumConstant.class);

    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private Producer kaptchaProducer;

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

    //生成验证码的方法,返回的是图片，不能用返回参数类型来返回，要用respond来响应
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response,
                           HttpSession session){
        //生成验证码,
        //生成四位的随机字符串
        String text = kaptchaProducer.createText();
        //用text字符串，去画一个图
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("kaptcha",text);
        //将图片输出给浏览器
        //给浏览器声明返回什么样的数据
        response.setContentType("image/png");
        try {
            //获取输出流，输出内容，字节流
            OutputStream os = response.getOutputStream();
            //输出
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

    //表单提交数据，相当于增加数据，把数据增加到数据库里
    //rememberme 前端页面的记住密码，点击就记录时间久一点，反之少一点
    //页面写的验证码，要和生成的验证码去对比，页面的验证码放到session里了
    //如果登录成功，要把ticket发放给客户端好让他保存，需要用cookie保存
    //如果参数是实体，mvc会把他自动存入到model里，在页面上就可以从model里得到这个user的数据
    // 但是username这种普通的，字符串，基本类型啊，spring不会把他存入model里
    //有两种方法得到这个数据，1.手动存入model，2.这几个参数存在于request对象里，
    // request.getParameter(username);因为是请求中携带过来的
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,
                        String password,
                        String code,
                        boolean rememberme,
                        Model model,
                        HttpSession session,
                        HttpServletResponse response){

        //判断验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 检查账号,密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = loginTicketService.login(username, password, expiredSeconds);
        //成功
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            //整个项目都有效
            cookie.setPath(contextPath);
            //设置cookie最大有效时间
            cookie.setMaxAge(expiredSeconds);
            //将cookie在响应是发送给浏览器
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出登录
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        loginTicketService.logout(ticket);
        return "redirect:/login";
    }
}
