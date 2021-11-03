INSERT INTO `chain_block_header` (`id`, `height`, `hash`, `version`, `hash_prev_block`,
                                  `timestamp`, `coinbase`, `state_root`, `transactions_root`,
                                  `receipts_root`, `gas_limit`, `gas_used`, `bits`, `nonce`, `tx_count`)
VALUES  (1, 0,'0xf0504923ce68c0edd97bf04eef004ade8133cb08ee4f3f667d6db06b6db9ab2c', 0,
         '0x0000000000000000000000000000000000000000000000000000000000000000', 0,
         '1Eux8FG5RuaEiqRKEZgbSqHZwhskDjmS2p',
         '0x4d673cbdb2175baeccdfca51872a1ea2e84b3081521aac476c96f95bc68215d0',
         '0x0000000000000000000000000000000000000000000000000000000000000000',
         '0x0000000000000000000000000000000000000000000000000000000000000000',
         '250000', '0', 4278190109, 0, 0);

INSERT INTO `chain_head_index` (`id`, `head`) VALUES (1, '0xf0504923ce68c0edd97bf04eef004ade8133cb08ee4f3f667d6db06b6db9ab2c');