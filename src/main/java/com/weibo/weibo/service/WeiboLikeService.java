package com.weibo.weibo.service;

public interface WeiboLikeService {

    boolean like(Integer uid, Integer statusId);

    boolean isLiked(Integer uid, Integer statusId);
}
