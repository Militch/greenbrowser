package com.esiran.greenadmin.chain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chain_address")
public class Address {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String address;
    private String balance;
    private Long nonce;
    private String extra;
    private String code;
    private String stateRoot;
    private String alias;
    private Integer type;
    private Boolean display;
    private String fromStateRoot;
    private Long fromBlockHeight;
    private String fromBlockHash;
    private String createFromAddress;
    private Long createFromBlockHeight;
    private String createFromBlockHash;
    private String createFromStateRoot;
    private String createFromTxHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
