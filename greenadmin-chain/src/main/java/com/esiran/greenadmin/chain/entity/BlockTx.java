package com.esiran.greenadmin.chain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chain_block_tx")
public class BlockTx implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String blockHash;
    private Long blockHeight;

    private Integer version;
    @TableField("`from`")
    private String from;
    @TableField("`to`")
    private String to;

    private String gasPrice;

    private String gasLimit;

    private String data;

    private Long nonce;

    private String value;

    private Long timestamp;

    private String signature;

    private String hash;
}
