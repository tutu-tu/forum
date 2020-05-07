package com.plantrice.forum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 *邮件的客户端
 */
@Component
public class MailClient {

    //声明一个logger，在关键的地方记录日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    //spring管理的核心组件
    @Autowired
    private JavaMailSender mailSender;

    //通过spring.mail.username 这个key注入到spring中 这个方法是发件员
    @Value("${spring.mail.username}")
    private String from;

    //封装共有的方法，供外届调用，不需要返回值，只要不报错，就算成功
    //to为发送的目标，subject标题，content内容
    public void sendMail(String to, String subject, String content){
        try {
        //构建mimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        //帮助你去构建message里面的内容
        MimeMessageHelper helper = new MimeMessageHelper(message);
        //告诉他你的发件人是谁就可以了,设置发件人
        helper.setFrom(from);
        //设置收件人
        helper.setTo(to);
        //设置主题
        helper.setSubject(subject);
        //设置文本，true表示可以传送HTML类型的内容，不加默认不同文本
        helper.setText(content,true);
        mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }
    }

}
