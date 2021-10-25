
-- ----------------------------
-- Table structure for chain_block_header
-- ----------------------------
DROP TABLE IF EXISTS `chain_block_header`;
CREATE TABLE `chain_block_header`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `height` int UNSIGNED NOT NULL,
    `hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `version` int UNSIGNED NOT NULL,
    `hash_prev_block` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `timestamp` int UNSIGNED NOT NULL DEFAULT 0,
    `coinbase` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `state_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `transactions_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `receipts_root` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `gas_limit` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `gas_used` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `bits` int UNSIGNED NOT NULL,
    `nonce` int UNSIGNED NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chain_block_tx
-- ----------------------------
DROP TABLE IF EXISTS `chain_block_tx`;
CREATE TABLE `chain_block_tx`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `version` int NOT NULL,
    `to` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `gas_price` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `gas_limit` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `nonce` int NOT NULL,
    `value` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `timestamp` int UNSIGNED NOT NULL,
    `signature` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chain_block_tx
-- ----------------------------
DROP TABLE IF EXISTS `chain_block_tx`;
CREATE TABLE `chain_block_tx`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `block_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `version` int NOT NULL,
    `to` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `gas_price` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `gas_limit` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `nonce` int NOT NULL,
    `value` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `timestamp` int UNSIGNED NOT NULL,
    `signature` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chain_head_index
-- ----------------------------
DROP TABLE IF EXISTS `chain_head_index`;
CREATE TABLE `chain_head_index`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `head` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chain_tx_receipt
-- ----------------------------
DROP TABLE IF EXISTS `chain_tx_receipt`;
CREATE TABLE `chain_tx_receipt`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `version` int NOT NULL,
    `status` int NOT NULL,
    `tx_hash` varchar(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `gas_used` varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;