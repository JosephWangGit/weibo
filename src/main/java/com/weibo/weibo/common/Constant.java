package com.weibo.weibo.common;

public interface Constant {
    /**
     * 已入库key前缀
     */
    String PREFIX_IN_MYSQL = "y";

    /**
     * 未入库key前缀
     */
    String PREFIX_NOT_IN_MYSQL = "n";

    /**
     * 无效请求攻击key前缀
     */
    String PREFIX_ATTACK = "a";

    /**
     * 定时任务分布式锁key前缀
     */
    String PREFIX_LOCK = "l";

    /**
     * 分隔符
     */
    String SEPARTOR = ":";

    /**
     * redis scan count
     */
    long SCAN_OPTION_COUNT = 100L;

    /**
     * redis scan pattern
     */
    String N_KEY_PATTERN = "n:*";

    /**
     * 定时任务频率
     */
    long FIXED_RATE = 60 * 1000;
}
