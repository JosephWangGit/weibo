package com.weibo.weibo.job;

import com.weibo.weibo.common.Constant;
import com.weibo.weibo.common.RedisService;
import com.weibo.weibo.entity.UserLikeStatus;
import com.weibo.weibo.mapper.UserLikeStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class RedisToMySQLTask {

    private static final Logger logger = LoggerFactory.getLogger(RedisToMySQLTask.class);

    @Resource
    private RedisService redisService;

    @Resource
    private UserLikeStatusMapper userLikeStatusMapper;

    @Async("taskScheduler")
    @Scheduled(fixedRate = Constant.FIXED_RATE)
    public void redisToMySQLTask() {
        redisService.scan(Constant.N_KEY_PATTERN, item -> {
            String nKey = new String(item, StandardCharsets.UTF_8);
            logger.info("redisToMySQL [nKey:{}]", nKey);

            //分布式锁key格式  l:n:statusId:uid
            String lockKey = Constant.PREFIX_LOCK.concat(Constant.SEPARTOR).concat(nKey);
            String lockId = redisService.lock(lockKey, 1);

            if (lockId != null) {
                try {
                    redisToMySQL(nKey);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    redisService.unLock(lockKey, lockId);
                }
            }
        });
    }

    private void redisToMySQL(String nKey) {
        Integer statusId = Integer.valueOf(nKey.split(Constant.SEPARTOR)[1]);
        Integer uid = Integer.valueOf(nKey.split(Constant.SEPARTOR)[2]);
        String yKey = redisService.genLikeKey(Constant.PREFIX_IN_MYSQL, statusId, uid);
        UserLikeStatus userLikeStatus = new UserLikeStatus(statusId, uid);

        //未入库
        if (userLikeStatusMapper.select(userLikeStatus) == 0) {
            try {
                //入库，设置yKey，删除nKey
                if (userLikeStatusMapper.insert(userLikeStatus) > 0) {
                    redisService.setIfAbsent(yKey, null, 30L, TimeUnit.DAYS);
                    redisService.delete(nKey);
                }
                //入库记录重复，设置yKey，删除nKey
            } catch (DuplicateKeyException dke) {
                redisService.setIfAbsent(yKey, null, 30L, TimeUnit.DAYS);
                redisService.delete(nKey);
                //入库异常，nKey不处理
            } catch (Exception e) {
                e.printStackTrace();
            }
            //已入库，设置yKey，删除nKey
        } else {
            redisService.setIfAbsent(yKey, null, 30L, TimeUnit.DAYS);
            redisService.delete(nKey);
        }
    }
}
