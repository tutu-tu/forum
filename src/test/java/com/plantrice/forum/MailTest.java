package com.plantrice.forum;

import com.plantrice.forum.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

//发送邮件的测试类
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    //thymeleaf核心的类，这个类也是被spring管理的，直接注入就行了 ，模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //测试发送一个普通文本的
    @Test
    public void testMail(){
        //mailClient来发邮件
        mailClient.sendMail("1670183304@qq.com","hello","welcome to guangzhou");
    }

    //测试发送HTML文本的邮件
    @Test
    public void testHtmlMail() {
        //访问模板，需要给模板传参数，这个参数需要context对象来构造
        Context context = new Context();
        context.setVariable("username","tom");

        //告诉模板文件放在那，和数据（传输人的名字）
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1670183304@qq.com", "HTML", content);
    }
}
