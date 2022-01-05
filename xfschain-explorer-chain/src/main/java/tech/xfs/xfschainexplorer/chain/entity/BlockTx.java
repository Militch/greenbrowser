package tech.xfs.xfschainexplorer.chain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    private Long blockTime;
    private Integer version;
    @TableField("`from`")
    private String from;
    @TableField("`to`")
    private String to;
    private String gasPrice;
    private String gasLimit;
    private String gasUsed;
    private String gasFee;
    private String data;
    private Long nonce;
    private String value;
    private String signature;
    private String hash;
    @TableField("`status`")
    private Integer status;
    @TableField("`type`")
    private Integer type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
