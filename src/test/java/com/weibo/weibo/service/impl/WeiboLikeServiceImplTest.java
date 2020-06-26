package com.weibo.weibo.service.impl;

import com.weibo.weibo.service.WeiboLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WeiboLikeServiceImplTest {

    @Resource
    private WeiboLikeService weiboLikeService;

    @Test
    void like() {
        weiboLikeService.like(1, 1);
        weiboLikeService.like(2, 1);
        weiboLikeService.like(3, 1);
        weiboLikeService.like(4, 2);
    }

    @Test
    void isLiked() {
        assertTrue(weiboLikeService.isLiked(1, 1));
        assertFalse(weiboLikeService.isLiked(5, 1));
    }
}