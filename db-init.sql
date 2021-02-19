/*
 Navicat MySQL Data Transfer

 Source Server         : saas-dev
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 192.168.16.246:3306
 Source Schema         : gateway

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 15/12/2020 19:25:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE SCHEMA IF NOT EXISTS `gateway` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
-- ----------------------------
-- Table structure for api
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`api`;
CREATE TABLE `gateway`.`api` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `state` int(4) NOT NULL DEFAULT '0' COMMENT '0:草稿 1:已发布 2:已停用 ',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `api_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'API名',
  `api_desc` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'API描述',
  `api_group` int(11) NOT NULL COMMENT 'API分组ID',
  `api_version` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT 'API版本号',
  `rpc_interface` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '远程接口名',
  `rpc_method` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '远程方法名',
  `rpc_version` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '远程接口版本号',
  `timeout` int(11) NOT NULL COMMENT 'API超时时间',
  `format` varchar(10) COLLATE utf8mb4_bin NOT NULL DEFAULT 'json' COMMENT '格式化方式',
  `sign_type` varchar(10) COLLATE utf8mb4_bin NOT NULL DEFAULT 'hmac' COMMENT '签名方式',
  `call_limit` int(11) NOT NULL DEFAULT '-1' COMMENT '限流次数, -1:不限流',
  `need_login` int(4) NOT NULL DEFAULT '1' COMMENT '是否需要登录 0-需要,1-不需要',
  `need_auth` int(4) NOT NULL DEFAULT '0' COMMENT '是否需要访问权限 0-需要 1-不需要',
  `http_method` varchar(10) COLLATE utf8mb4_bin NOT NULL DEFAULT 'GET' COMMENT '请求类型 GET或POST',
  `api_type` int(4) NOT NULL DEFAULT '0' COMMENT 'API类型 0-普通API,1-回调API',
  PRIMARY KEY (`id`),
  KEY `idx_api_group` (`api_group`) USING BTREE,
  KEY `idx_api_name` (`api_name`,`api_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for api_count
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`api_count`;
CREATE TABLE `gateway`.`api_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `api_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'API名称',
  `api_version` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT 'API版本',
  `count_date` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '统计日期',
  `total_count` int(10) NOT NULL DEFAULT '0' COMMENT '总次数',
  `succ_count` int(10) NOT NULL DEFAULT '0' COMMENT '成功次数',
  `total_spends` int(10) NOT NULL DEFAULT '0' COMMENT '总耗时',
  `max_spends` int(10) NOT NULL DEFAULT '0' COMMENT '最大耗时',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_account` (`count_date`,`api_name`,`api_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for api_group
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`api_group`;
CREATE TABLE `gateway`.`api_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `group_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '分组名',
  `group_desc` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分组描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for api_param
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`api_param`;
CREATE TABLE `gateway`.`api_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `api_id` int(11) NOT NULL COMMENT 'API_ID',
  `param_index` int(4) NOT NULL COMMENT '参数序号',
  `param_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '参数名',
  `param_desc` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数描述',
  `param_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '参数类型',
  `param_type_id` int(11) NOT NULL COMMENT '参数类型ID',
  `example` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数示例',
  `is_required` int(4) NOT NULL DEFAULT '0' COMMENT '是否必填 0:必填,1:非必填',
  `param_source` int(4) NOT NULL DEFAULT '0' COMMENT '参数获取来源 0-非body 1-body',
  PRIMARY KEY (`id`),
  KEY `idx_api_id` (`api_id`,`param_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for api_result
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`api_result`;
CREATE TABLE `gateway`.`api_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否已删除 0-未删除 1-已删除',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `api_id` int(11) NOT NULL COMMENT 'API_ID',
  `field_index` int(4) NOT NULL COMMENT '字段序号',
  `field_desc` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段描述',
  `field_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '前端字段名',
  `back_field_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '服务端字段名',
  `field_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段类型',
  `example` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段示例',
  PRIMARY KEY (`id`),
  KEY `idx_api_id` (`api_id`,`back_field_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`app`;
CREATE TABLE `gateway`.`app` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `app_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '应用名',
  `app_key` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '访问key',
  `app_secret` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '访问秘钥',
  `app_type` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `idx_appkey` (`app_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for pojo_struct
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`pojo_struct`;
CREATE TABLE `gateway`.`pojo_struct` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `state` int(4) NOT NULL DEFAULT '0' COMMENT '0:草稿 1:已发布 2:已停用 ',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `type_id` int(11) NOT NULL DEFAULT '0' COMMENT '类型ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '数据结构名',
  `struct_ref` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '子对象关系，json格式',
  `struct_type` int(4) NOT NULL COMMENT '数据结构类型：0-单对象 1-list 2-set 3-map 4-array',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for pojo_type
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`pojo_type`;
CREATE TABLE `gateway`.`pojo_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `state` int(4) NOT NULL DEFAULT '0' COMMENT '0:草稿 1:已发布 2:已停用 ',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `type_ename` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '参数类型英文名',
  `type_cname` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数类型中文名',
  `type_class` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '参数类型class',
  PRIMARY KEY (`id`),
  KEY `idx_type_ename` (`type_ename`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for pojo_type_json
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`pojo_type_json`;
CREATE TABLE `gateway`.`pojo_type_json` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-有效 1-已删除',
  `state` int(4) NOT NULL DEFAULT '0' COMMENT '0:草稿 1:已发布 2:已停用 ',
  `creator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
  `is_pojo` int(4) NOT NULL DEFAULT '0' COMMENT '是否POJO对象 0:否 1：是',
  `type_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '结构名称',
  `type_desc` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '结构描述',
  `type_json` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据结构原生json',
  PRIMARY KEY (`id`),
  KEY `idx_struct_name` (`type_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`user`;
CREATE TABLE `gateway`.`user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` int(4) NOT NULL DEFAULT '0' COMMENT '0:有效 1：删除',
  `account` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '登录账号',
  `password` varchar(300) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `role` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '角色',
  `department` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '部门',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_account` (`account`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for auth
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`auth`;
CREATE TABLE `gateway`.`auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `api_group` int(11) NOT NULL COMMENT '类目ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_userid_group` (`user_id`,`api_group`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for opt_log
-- ----------------------------
DROP TABLE IF EXISTS `gateway`.`opt_log`;
CREATE TABLE `gateway`.`opt_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `operator_id` int(11) NOT NULL COMMENT '操作人ID',
  `operator` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '操作人',
  `action` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '执行动作',
  `content` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作内容',
  `result` int(4) NOT NULL COMMENT '操作结果 0-成功 1-失败',
  PRIMARY KEY (`id`),
  KEY `idx_operator_id` (`operator_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- 初始化参数基本类型
-- ----------------------------
INSERT INTO `gateway`.`pojo_type_json` VALUES (1, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.String', 'java.lang.String', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (2, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Integer', 'java.lang.Integer', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (3, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Long', 'java.lang.Long', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (4, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Float', 'java.lang.Float', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (5, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Double', 'java.lang.Double', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (6, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Boolean', 'java.lang.Boolean', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (7, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.String[]', 'java.lang.String[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (8, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Integer[]', 'java.lang.Integer[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (9, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Long[]', 'java.lang.Long[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (10, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Float[]', 'java.lang.Float[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (11, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Double[]', 'java.lang.Double[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (12, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.lang.Boolean[]', 'java.lang.Boolean[]', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (13, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.util.List', 'java.util.List', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (14, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.util.Set', 'java.util.Set', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (15, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.util.Map', 'java.util.Map', NULL);
INSERT INTO `gateway`.`pojo_type_json` VALUES (16, '2020-12-03 16:41:36', '2020-12-03 16:41:39', 0, 1, 'admin', 'admin', 0, 'java.util.Date', 'java.util.Date', NULL);

-- ----------------------------
-- 初始化管理员
-- ----------------------------
INSERT INTO `gateway`.`user` VALUES (1, '2020-12-02 18:44:36', '2020-12-02 18:44:39', 0, 'admin', 'admin123.', '管理员', '网关部门');