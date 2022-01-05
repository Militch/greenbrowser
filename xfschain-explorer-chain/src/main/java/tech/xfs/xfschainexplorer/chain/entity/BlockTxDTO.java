package tech.xfs.xfschainexplorer.chain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BlockTxDTO {
    private Integer id;
    private String blockHash;
    private Long blockHeight;
    private Integer version;
    private String from;
    private String to;
    private String gasPrice;
    private String gasFee;
    private String gasUsed;
    private String gasLimit;

    private String data;

    private Long nonce;

    private String value;

    private Long timestamp;

    private String signature;

    private String hash;
    private Integer status;
}
