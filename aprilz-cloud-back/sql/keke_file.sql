/*
Navicat MySQL Data Transfer

Source Server         : localhost_mysql
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : keke_file

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2022-03-01 16:10:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
                        `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件id',
                        `file_size` bigint(10) DEFAULT NULL COMMENT '文件大小',
                        `file_url` varchar(500) DEFAULT NULL COMMENT '文件url',
                        `identifier` varchar(32) DEFAULT NULL COMMENT 'md5标识',
                        `is_oss` int(1) DEFAULT NULL COMMENT '是否是OSS云存储 0-否, 1-是',
                        `point_count` int(11) DEFAULT NULL COMMENT '文件引用数量',
                        `time_stamp_name` varchar(500) DEFAULT NULL COMMENT '时间戳名称',
                        PRIMARY KEY (`file_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of file
-- ----------------------------

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
                              `permission_id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `available` bit(1) DEFAULT NULL,
                              `name` varchar(255) DEFAULT NULL,
                              `parent_id` bigint(20) DEFAULT NULL,
                              `parent_ids` varchar(255) DEFAULT NULL,
                              `permission` varchar(255) DEFAULT NULL,
                              `resource_type` varchar(255) DEFAULT NULL,
                              `url` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`permission_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', null, null, null, null, 'admin', null, null);
INSERT INTO `permission` VALUES ('2', null, null, null, null, 'user', null, null);

-- ----------------------------
-- Table structure for recoveryfile
-- ----------------------------
DROP TABLE IF EXISTS `recoveryfile`;
CREATE TABLE `recoveryfile` (
                                `recovery_file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回收文件id',
                                `delete_batch_num` varchar(50) DEFAULT NULL COMMENT '删除批次号',
                                `delete_time` varchar(25) DEFAULT NULL COMMENT '删除时间',
                                `user_file_id` bigint(20) DEFAULT NULL COMMENT '用户文件id',
                                PRIMARY KEY (`recovery_file_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of recoveryfile
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                        `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `available` bit(1) DEFAULT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `role` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', null, '管理员', 'admin');
INSERT INTO `role` VALUES ('2', null, '普通用户', 'user');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
                                   `role_id` bigint(20) NOT NULL,
                                   `permission_id` bigint(20) NOT NULL,
                                   KEY `FKmsjtuo1smqbduu6wt9gekj7k6` (`permission_id`) USING BTREE,
                                   KEY `FKsrw6jhwxy1l8i8urr987m0byj` (`role_id`) USING BTREE,
                                   CONSTRAINT `FKmsjtuo1smqbduu6wt9gekj7k6` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
                                   CONSTRAINT `FKsrw6jhwxy1l8i8urr987m0byj` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('1', '1');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
                        `addr_area` varchar(10) DEFAULT NULL COMMENT '区',
                        `addr_city` varchar(10) DEFAULT NULL COMMENT '市',
                        `addr_province` varchar(10) DEFAULT NULL COMMENT '省',
                        `birthday` varchar(30) DEFAULT NULL COMMENT '生日',
                        `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                        `image_url` varchar(100) DEFAULT NULL COMMENT '用户头像URL',
                        `industry` varchar(50) DEFAULT NULL COMMENT '行业',
                        `intro` varchar(5000) DEFAULT NULL COMMENT '介绍',
                        `open_id` varchar(30) DEFAULT NULL COMMENT 'openId qq登录使用',
                        `password` varchar(35) DEFAULT NULL COMMENT '密码',
                        `position` varchar(50) DEFAULT NULL COMMENT '职位',
                        `qq_password` varchar(35) DEFAULT NULL COMMENT 'qq密码',
                        `realname` varchar(30) DEFAULT NULL COMMENT '真实名',
                        `register_time` varchar(30) DEFAULT NULL COMMENT '注册时间',
                        `salt` varchar(20) DEFAULT NULL COMMENT '盐值',
                        `sex` varchar(3) DEFAULT NULL COMMENT '年龄',
                        `telephone` varchar(15) DEFAULT NULL COMMENT '手机号码',
                        `username` varchar(30) DEFAULT NULL COMMENT '用户名',
                        PRIMARY KEY (`user_id`) USING BTREE,
                        UNIQUE KEY `open_id_index` (`open_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('8', null, null, null, null, null, null, null, null, null, 'daee50bae5b0cd67b49ad8b50be0080e', null, null, null, '2022-02-28 09:55:58', '6810977554128198', null, '15118709413', 'admin');

-- ----------------------------
-- Table structure for userfile
-- ----------------------------
DROP TABLE IF EXISTS `userfile`;
CREATE TABLE `userfile` (
                            `user_file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户文件id',
                            `delete_batch_num` varchar(50) DEFAULT NULL COMMENT '删除批次号',
                            `delete_flag` int(11) DEFAULT NULL COMMENT '文件删除标志 0/null-正常, 1-删除',
                            `delete_time` varchar(25) DEFAULT NULL COMMENT '删除时间',
                            `extend_name` varchar(100) DEFAULT NULL COMMENT '扩展名',
                            `file_id` bigint(20) DEFAULT NULL COMMENT '文件id',
                            `file_name` varchar(100) DEFAULT NULL COMMENT '文件名',
                            `file_path` varchar(500) DEFAULT NULL COMMENT '文件路径',
                            `is_dir` int(1) DEFAULT NULL COMMENT '是否是目录 0-否, 1-是',
                            `upload_time` varchar(25) DEFAULT NULL COMMENT '上传时间',
                            `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                            PRIMARY KEY (`user_file_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of userfile
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                             `user_id` bigint(20) NOT NULL COMMENT '用户id',
                             `role_id` bigint(20) NOT NULL,
                             KEY `FKbo5ik0bthje7hum554xb17ry6` (`role_id`) USING BTREE,
                             KEY `FKj5g46wgmq1wmqfhv78g7cxaqe` (`user_id`) USING BTREE,
                             CONSTRAINT `FKbo5ik0bthje7hum554xb17ry6` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`),
                             CONSTRAINT `FKj5g46wgmq1wmqfhv78g7cxaqe` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('8', '2');
