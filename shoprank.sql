/*
 Navicat Premium Data Transfer

 Source Server         : nsotest
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : langladata

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 31/05/2024 06:27:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for shoprank
-- ----------------------------
DROP TABLE IF EXISTS `shoprank`;
CREATE TABLE `shoprank`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_item_id` smallint NULL DEFAULT NULL,
  `price` int NULL DEFAULT NULL,
  `items` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `yeucau` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of shoprank
-- ----------------------------
INSERT INTO `shoprank` VALUES (1, -1000, 100, '[{\"id\": 404,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 5}, {\"id\": 277,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 5}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1000000}]', 1);
INSERT INTO `shoprank` VALUES (2, -1001, 200, '[{\"id\": 644,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 347,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 2000000}]', 2);
INSERT INTO `shoprank` VALUES (3, -1002, 300, '[{\"id\": 277,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 161,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 3000000}]', 3);
INSERT INTO `shoprank` VALUES (4, -1003, 400, '[{\"id\": 267,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 347,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 4000000}]', 4);
INSERT INTO `shoprank` VALUES (5, -1004, 500, '[{\"id\": 515,\"expire_time\":-1, \"locked\": true, \"options\": \"137,20;209,30;174,5\", \"amount\": 1}, {\"id\": 347,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 10}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 5000000}]', 5);
INSERT INTO `shoprank` VALUES (6, -1005, 800, '[{\"id\": 435,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 10,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 6000000}]', 6);
INSERT INTO `shoprank` VALUES (7, -1006, 1000, '[{\"id\": 295,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 182,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 50}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 7000000}]', 7);
INSERT INTO `shoprank` VALUES (8, -1007, 1250, '[{\"id\": 296,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 428,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 30}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 8000000}]', 8);
INSERT INTO `shoprank` VALUES (9, -1008, 1750, '[{\"id\": 297,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 428,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 50}, {\"id\": 163,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 9000000}]', 9);
INSERT INTO `shoprank` VALUES (10, -1009, 2500, '[{\"id\": 688,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 5}, {\"id\": 468,\"expire_time\":-1, \"locked\": true, \"options\": \"\", \"amount\": 1}, {\"id\": 906,\"expire_time\":2419200000, \"locked\": true, \"options\": \"0,1500;1,1500;2,150;152,150;70,15;71,15;306,75\", \"amount\": 1}]', 10);

SET FOREIGN_KEY_CHECKS = 1;
