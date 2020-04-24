package com.plantrice.forum;

import com.plantrice.forum.dao.DiscussPostMapper;
import com.plantrice.forum.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiscussPostMapperTerst {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSeletctPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
}
