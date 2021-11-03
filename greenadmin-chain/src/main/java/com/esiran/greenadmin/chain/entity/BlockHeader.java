package com.esiran.greenadmin.chain.entity;

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
@TableName("chain_block_header")
public class BlockHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String hash;

    /**
     * 高度
     */
    private Long height;

    /**
     * 版本
     */
    private Integer version;

    /**
     * prev hash
     */
    private String hashPrevBlock;

    private Long timestamp;

    private String coinbase;

    private String stateRoot;

    private String transactionsRoot;

    private String receiptsRoot;

    private String gasLimit;

    private String gasUsed;

    private Long bits;

    private Long nonce;
    private Integer txCount;

}
