package com.plantrice.forum.service;
import com.plantrice.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * 点赞的业务层
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId, int entityType, int entityId,int entityUserId){
        /*String entityLikeKey = RedisKeyUtil.getEntityKeyLike(entityType,entityId);
        //判断该用户是否点过赞，只需要判断set中是否有userId
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        //如果返回true，证明userId存在，该用户已经点过赞了
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            //没点过赞，要添加
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }*/

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityKeyLike(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserKeyLike(entityUserId);
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);
                //启动事务
                redisOperations.multi();
                if (isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }


    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityKeyLike(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //统计实体点赞的状态，本来返回Boolean就可以了，
    // 但是如果将来业务扩展了，不只是点赞，还可以点踩，可以返回-1
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityKeyLike(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserKeyLike(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
