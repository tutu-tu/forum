package com.plantrice.forum.service;

import com.plantrice.forum.dao.UserMapper;
import com.plantrice.forum.entity.User;
import com.plantrice.forum.util.ForumConstant;
import com.plantrice.forum.util.ForumUtil;
import com.plantrice.forum.util.MailClient;
import com.plantrice.forum.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//业务逻辑层（用户的）
@Service
public class UserService implements ForumConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${forum.path.domain}")
    private String domain;
    @Autowired
    private RedisTemplate redisTemplate;

    //根据id查询用户信息
    public User findUserById(int id) {
        //return userMapper.selectById(id);
        //先从缓存中查
        User user = getCache(id);
        //如果查不到，初始化缓存
        if (user == null){
            user = initCache(id);
        }
        //如果查到了返回user信息
        return user;
    }

    //返回的类型很多，用map接收
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            //抛出的异常表明向方法传递了一个不合法或不正确的参数。
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号，把传入的username去库里面查，看是否存在
       User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //验证密码，把传入的Email去库里面查，看是否占用
        u = userMapper.selectByName(user.getEmail());
        if (u != null){
            map.put("emailMsg","该邮箱已被占用");
            return map;
        }

        //注册用户，对密码加密  new Random().nextInt(100) 随机生成 1到100的整数
        //String.format 方法
        user.setSalt(ForumUtil.generateUUID().substring(0,5));
        user.setPassword(ForumUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(ForumUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(100)));
        user.setCreateTime(new Date());
        //把user加入的内容，通过usermapper的insertUser方法加入到库中
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());

        //http://localhost:8686/forum/activation(功能）/101（用户id）/code（激活码）
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    //激活的方法，返回激活的状态,把用户查到，判断激活码，对比对不对
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        //已经激活了
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        //如果你的激活码是对的，可以激活成功,要把用户的状态改为1
        else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            //修改了用户状态，删除缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    //更改头像
    public int updateHeader(int userId, String headerUrl) {
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }

    //通过用户名寻找用户信息
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    //1.优先从缓存中取数据
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到，初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,7200, TimeUnit.SECONDS);
        return user;
    }

    //3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
