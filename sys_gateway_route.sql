/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : pys

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2020-03-26 13:37:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_gateway_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_gateway_route`;
CREATE TABLE `sys_gateway_route` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `service_id` varchar(32) DEFAULT NULL,
  `uri` varchar(64) DEFAULT NULL,
  `predicates` varchar(64) DEFAULT NULL,
  `filters` varchar(1) DEFAULT NULL,
  `sort` int(10) DEFAULT NULL,
  `create_id` int(10) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_id` int(10) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `is_delete` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_gateway_route
-- ----------------------------
INSERT INTO `sys_gateway_route` VALUES ('1', 'rpt-dept-service', 'rpt-dept-service', '/dept/**', '1', '0', null, null, null, null, null, null);
