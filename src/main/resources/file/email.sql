/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50523
 Source Host           : localhost:3306
 Source Schema         : email

 Target Server Type    : MySQL
 Target Server Version : 50523
 File Encoding         : 65001

 Date: 03/06/2021 18:00:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_email
-- ----------------------------
DROP TABLE IF EXISTS `tb_email`;
CREATE TABLE `tb_email`  (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` tinyint(1) NULL DEFAULT NULL,
  `batch_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_email
-- ----------------------------
INSERT INTO `tb_email` VALUES (1400373611345571842, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611429457921, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611433652226, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611433652227, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611433652228, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611437846530, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611437846531, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611437846532, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611437846533, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235137, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235138, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235139, '1126480696@qq.com', 1, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235140, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235141, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235142, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611446235143, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623745, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623746, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623747, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623748, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623749, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623750, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623751, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623752, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623753, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623754, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611454623755, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611463012353, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611463012354, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611463012355, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);
INSERT INTO `tb_email` VALUES (1400373611463012356, '1126480696@qq.com', 0, '56d3a0760fe64b1cbff9bd7c52acbf22', NULL);

-- ----------------------------
-- Table structure for tb_email_error
-- ----------------------------
DROP TABLE IF EXISTS `tb_email_error`;
CREATE TABLE `tb_email_error`  (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
