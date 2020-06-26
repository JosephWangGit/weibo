
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for status
-- ----------------------------
DROP TABLE IF EXISTS `status`;
CREATE TABLE `status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `content` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '微博内容',
  `publisher` bigint(20) unsigned DEFAULT NULL COMMENT '发布者id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COMMENT='微博表';

-- ----------------------------
-- Records of status
-- ----------------------------
BEGIN;
INSERT INTO `status` VALUES (1, 'like me pls', 1);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '手机号',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COMMENT='用户';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, '13800000001');
INSERT INTO `user` VALUES (2, '13800000002');
COMMIT;

-- ----------------------------
-- Table structure for user_like_status
-- ----------------------------
DROP TABLE IF EXISTS `user_like_status`;
CREATE TABLE `user_like_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `status_id` bigint(20) unsigned DEFAULT NULL COMMENT '微博id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '点赞用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_status_user` (`status_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COMMENT='用户点赞微博关系';

-- ----------------------------
-- Records of user_like_status
-- ----------------------------
BEGIN;
INSERT INTO `user_like_status` VALUES (1, 1, 1);
INSERT INTO `user_like_status` VALUES (2, 1, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
