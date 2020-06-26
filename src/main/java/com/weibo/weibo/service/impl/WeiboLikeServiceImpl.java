package com.weibo.weibo.service.impl;

import com.weibo.weibo.common.Constant;
import com.weibo.weibo.common.RedisService;
import com.weibo.weibo.entity.UserLikeStatus;
import com.weibo.weibo.mapper.UserLikeStatusMapper;
import com.weibo.weibo.service.WeiboLikeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 数据格式一
 * key(String): statusId
 * value(Set<Integer>): set of uid
 * 空间占用小，但无法判断是否已入库。
 * <p>
 * 数据格式二
 * key(String):  (n/y):statusId:uid
 * n 未入库 y 已入库
 * ex. n:123:90   y:123:33
 * value(null)
 */
@Service
public class WeiboLikeServiceImpl implements WeiboLikeService {

    @Resource
    private RedisService redisService;

    @Resource
    private UserLikeStatusMapper userLikeStatusMapper;

    @Override
    public boolean like(Integer uid, Integer statusId) {
        String nKey = redisService.genLikeKey(Constant.PREFIX_NOT_IN_MYSQL, statusId, uid);
        String yKey = redisService.genLikeKey(Constant.PREFIX_IN_MYSQL, statusId, uid);
        //同一条微博同一个用户只能点赞一次
        return !redisService.hasKey(yKey) && redisService.setIfAbsent(nKey, null, 30L, TimeUnit.DAYS);
    }

    @Override
    public boolean isLiked(Integer uid, Integer statusId) {
        String nKey = redisService.genLikeKey(Constant.PREFIX_NOT_IN_MYSQL, statusId, uid);
        String yKey = redisService.genLikeKey(Constant.PREFIX_IN_MYSQL, statusId, uid);

        if (redisService.hasKey(nKey) || redisService.hasKey(yKey)) {
            return true;
        }

        //阻挡无效参数反复查询MySQL  key格式：a:statusId:uid
        String aKey = redisService.genLikeKey(Constant.PREFIX_ATTACK, statusId, uid);
        if (redisService.hasKey(aKey)) {
            return false;
        }

        UserLikeStatus userLikeStatus = new UserLikeStatus(uid, statusId);
        int count = userLikeStatusMapper.select(userLikeStatus);
        if (count > 0) {
            //MySQL查询到数据，放到Redis，相同参数下次查询命中缓存
            like(uid, statusId);
            return true;
        }

        //未命中Redis和MySQL，缓存一秒。一秒内再次查询不查MySQL直接返回false
        redisService.set(aKey, null, 1L, TimeUnit.SECONDS);
        return false;
    }
}
