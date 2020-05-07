package com.plantrice.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 定义第三方得Bean，把那个对象装配到spring容器中，就把那个对象返回
     * 利用template访问数据库，得创建连接，连接需要连接工厂创建，需要把连接工厂注入进来
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        //把工厂设置为Template
        template.setConnectionFactory(factory);
        //template 有了工厂以后，就具备访问数据库得能力。因为redis是key,value 的存储方式，
        // 我们得到的Java类型的数据，最终要存到redis中，所以指定序列化的方式，或者数据转换的方式
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置普通的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式，因为value本身就是一个hash,而hash里面有是一个hash
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //设置完了要触发,设置才能生效
        template.afterPropertiesSet();
        return template;

    }
}
