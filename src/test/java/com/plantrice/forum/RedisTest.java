package com.plantrice.forum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //value为String类型
    @Test
    public void testString(){
        String redisKey = "test:count";
        //存值
        redisTemplate.opsForValue().set(redisKey,1);
        //取值
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        //追加
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        //减少
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    //Hash
    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","lisi");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));

    }

    //list
    @Test
    public void testList(){
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        //获取当前列表中的数据
        System.out.println(redisTemplate.opsForList().size(redisKey));
        //根据索引查看数据
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        //输出某个范围的数据
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        //从右边开始进行弹出
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        //都弹出了，看看还有多少数据
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }

    //set
    @Test
    public void testSet(){
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey,"aa","bb","cc","dd");
        //个数
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        //随机弹出一个值
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        //查看集合中还有多少元素,返回元素集合
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    //Zset
    @Test
    public void testZset(){
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey,"小明",50);
        redisTemplate.opsForZSet().add(redisKey,"小红",80);
        redisTemplate.opsForZSet().add(redisKey,"小白",70);
        redisTemplate.opsForZSet().add(redisKey,"啊呀",40);
        redisTemplate.opsForZSet().add(redisKey,"gdx",65);

        //统计多少个数据
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        //分数
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"小红"));
        //从小到大的排名,返回索引
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"小红"));
        //从大到小排序
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"小红"));
        //由小到大取范围的集合
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,1));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,3));
    }

    //测试全局
    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        //判断这个hash 的key是否存在，相当于命令的exists
        System.out.println(redisTemplate.hasKey("test:user"));
        //设置过期时间，可以指定 时间为10 秒
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }

    //多次访问同一个key,以绑定的方式存在 BoundOperations 绑定操作
    //将key绑定到一个对象上，所产生的对象，就叫做绑定对象。
    @Test
    public void testBoundOperations(){
        String redisKeys = "test:count";
        //数据类型为String类型
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKeys);
        //不用输入key，直接累加
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        //取出值
        System.out.println(operations.get());

    }

    //编程式事务
    @Test
    public void testCodeTransaction(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:ct";
                //启动事务
                redisOperations.multi();
                //开启事务和提交事务之间，会把数据存储到队列里，不会立刻执行，
                // 当事务提交了，会一块执行，这个时候查询是没有效果的
                redisOperations.opsForSet().add(redisKey,"张飞");
                redisOperations.opsForSet().add(redisKey,"关羽");
                redisOperations.opsForSet().add(redisKey,"黄忠");
                redisOperations.opsForSet().add(redisKey,"马超");
                redisOperations.opsForSet().add(redisKey,"赵云");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                //提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }
}
