/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 100432 (10.4.32-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : langla_data

 Target Server Type    : MySQL
 Target Server Version : 100432 (10.4.32-MariaDB)
 File Encoding         : 65001

 Date: 08/06/2024 17:17:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for danhhieu
-- ----------------------------
DROP TABLE IF EXISTS `danhhieu`;
CREATE TABLE `danhhieu`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idItem` int NOT NULL,
  `x` int NOT NULL,
  `y` int NOT NULL,
  `timeMS` int NOT NULL,
  `size` smallint NOT NULL,
  `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of danhhieu
-- ----------------------------
INSERT INTO `danhhieu` VALUES (1, 933, 0, -20, 20, 60, '{943,323,434,545,66,44,565}');

SET FOREIGN_KEY_CHECKS = 1;
