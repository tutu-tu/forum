package com.plantrice.forum;

import com.plantrice.forum.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "可以吸毒，可以赌博？可以卖，淫？发发发发发发";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
