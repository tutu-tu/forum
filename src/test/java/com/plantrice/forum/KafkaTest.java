package com.plantrice.forum;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    /**
     * 测试用生产者发送消息，消费者能不能自动收到这个消息，消费者打印出来能看到
     */
    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","hello word!");
        kafkaProducer.sendMessage("test","how are you");
        kafkaProducer.sendMessage("test","how old are you");

        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 生产者
 */
@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}
/**
 * 消费者
 */
@Component
class KafkaConsumer{

    @KafkaListener(topics = "test")
    public void hadlerMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}