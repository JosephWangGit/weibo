package com.weibo.weibo.controller;

import com.weibo.weibo.service.WeiboLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class WeiboLikeController {
    public static void main(String[] args) {
        System.out.println(new Double(Math.pow(2, 32)).intValue());
    }

    private static final Logger logger = LoggerFactory.getLogger(WeiboLikeController.class);

    @Resource
    private WeiboLikeService weiboLikeService;

    @PostMapping("/like")
    @ResponseBody
    public boolean like(Integer uid, Integer statusId) {
        logger.info("/like [uid:{}] [statusId:{}]", uid, statusId);
        if (!validator(uid, statusId)) {
            return false;
        }
        return weiboLikeService.like(uid, statusId);
    }

    @PostMapping("isLiked")
    @ResponseBody
    public boolean isLiked(Integer uid, Integer statusId) {
        logger.info("/isLiked [uid:{}] [statusId:{}]", uid, statusId);
        if (!validator(uid, statusId)) {
            return false;
        }
        return weiboLikeService.isLiked(uid, statusId);
    }

    private boolean validator(Integer uid, Integer statusId) {
        if (uid == null || statusId == null) {
            return false;
        }
        return true;
    }
}
