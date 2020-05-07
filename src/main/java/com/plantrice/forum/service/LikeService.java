package com.plantrice.forum.service;
import com.plantrice.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 点赞的业务层
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityKeyLike(entityType,entityId);
        //判断该用户是否点过赞，只需要判断set中是否有userId
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        //如果返回true，证明userId存在，该用户已经点过赞了
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            //没点过赞，要添加
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
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
}
