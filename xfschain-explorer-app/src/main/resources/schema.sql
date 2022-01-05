
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chain_address
-- ----------------------------
DROP TABLE IF EXISTS `chain_address`;
CREATE TABLE `chain_address`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `balance` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  `nonce` bigint UNSIGNED NOT NULL,
  `extra` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `code` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` tinyint(4) UNSIGNED ZEROFILL NOT NULL DEFAULT 0000,
  `display` tinyint UNSIGNED NOT NULL DEFAULT 1,
  `from_state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `from_block_height` bigint UNSIGNED NOT NULL DEFAULT 0,
  `from_block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_from_address` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_from_block_height` bigint UNSIGNED NULL DEFAULT NULL,
  `create_from_block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_from_state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_from_tx_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chain_address
-- ----------------------------

-- ----------------------------
-- Table structure for chain_block_header
-- ----------------------------
DROP TABLE IF EXISTS `chain_block_header`;
CREATE TABLE `chain_block_header`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `height` bigint UNSIGNED NOT NULL,
  `hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` int UNSIGNED NOT NULL,
  `hash_prev_block` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` bigint UNSIGNED NOT NULL DEFAULT 0,
  `coinbase` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `transactions_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receipts_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gas_limit` bigint NOT NULL,
  `gas_used` bigint NOT NULL,
  `bits` bigint UNSIGNED NOT NULL,
  `nonce` bigint UNSIGNED NOT NULL,
  `extra_nonce` decimal(65, 0) UNSIGNED NOT NULL,
  `tx_count` int UNSIGNED NOT NULL DEFAULT 0,
  `rewards` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chain_block_header
-- ----------------------------

-- ----------------------------
-- Table structure for chain_block_tx
-- ----------------------------
DROP TABLE IF EXISTS `chain_block_tx`;
CREATE TABLE `chain_block_tx`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `block_height` bigint UNSIGNED NOT NULL,
  `block_time` bigint UNSIGNED NOT NULL DEFAULT 0,
  `version` int UNSIGNED NOT NULL DEFAULT 0,
  `from` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `to` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gas_price` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  `gas_limit` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  `gas_used` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  `gas_fee` decimal(65, 0) UNSIGNED NOT NULL DEFAULT 0,
  `data` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `nonce` bigint UNSIGNED NOT NULL DEFAULT 0,
  `value` decimal(65, 0) UNSIGNED NOT NULL,
  `signature` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1,
  `type` tinyint(4) UNSIGNED ZEROFILL NOT NULL DEFAULT 0001,
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chain_block_tx
-- ----------------------------

-- ----------------------------
-- Table structure for chain_token
-- ----------------------------
DROP TABLE IF EXISTS `chain_token`;
CREATE TABLE `chain_token`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `symbol` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_supply` decimal(65, 0) UNSIGNED NOT NULL,
  `decimals` int UNSIGNED NOT NULL,
  `address` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tx_count` bigint UNSIGNED NOT NULL,
  `from_tx_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `from_block_height` bigint UNSIGNED NOT NULL,
  `from_block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `from_state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chain_token
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
