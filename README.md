### 0. 介绍

点赞

### 1. 设计

#### 1.1 思路
点赞数据先存储在Redis中，缓存一个月。定时任务每分钟读取Redis中未入库的数据，存入MySQL。    

查询时查询缓存，缓存未命中则查询MySQL。可以缓存查询MySQL未命中的穿透请求，在业务允许的极短时间范围内
下次相同的查询直接拒绝。

#### 1.2 Redis数据结构
 Redis key格式:  (n/y):statusId:uid   n 未入库 y 已入库  
 
 ex. n:123:90 （微博123被用户90点赞，未入库）  
     y:123:33 （微博123被用户33点赞，已入库）  
 value 为空
 
#### 1.3 MySQL表结构

```sql
CREATE TABLE `user_like_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `status_id` bigint(20) unsigned DEFAULT NULL COMMENT '微博id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '点赞用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_status_user` (`status_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COMMENT='用户点赞微博关系';

```

### 2. 存储容量  
#### Redis  
Redis key示例： n:2147483647:2147483647  

按每秒1万次点赞请求，如果都是有效请求，1秒产生1万个key，dump.rdb大小254KB  

缓存有效期30天，key个数：1w * 30 * 24 * 60 * 60 = 1w * 2,592,000

Redis占用内存大小：254KB * 2,592,000 = 658,368,000 KB = 658,368 MB = 658G  
（实际应该远小于这个量，dump文件大于内存占用）  

采用key -> statusId, value -> set of uid，能大量节省内存空间，热点微博越多节省越明显。

key入库时加锁和临时yKey产生的两倍key大小的占用时间很短，在30天有效期内可以忽略。

#### MySQL  
每秒1万条记录存入MySQL占用磁盘：496KB数据 + 512KB索引 = 1MB  

3年占用：1MB * 3 * 365 * 24 * 60 * 60 = 94,608,000 MB = 95TB  

采用Primary Key(status_id, user_id)不占用额外索引空间，大约节省60%存储空间，只需40TB。但是无法分表。  

### 3. 应用集群
20万QPS，假设每次请求响应时间50ms，并发数为 200,000 / (1000/50) = 10,000  

Linux Tomcat单机并发数最大1000，经验值在200左右(具体以特定机器配置下的压测表现来定)，大约需要50个应用server。

### 4. Redis 和 MySQL 集群 以及扩容
Redis：VIP+twemproxy二级代理。 

MySQL：使用代理软件如MyCAT，需要分布式全局ID，采用一致性Hash分片，减少扩容时做数据迁移。 可以使用双写迁移。


