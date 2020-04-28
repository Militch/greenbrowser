package com.esiran.greenpay.agentpay.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class AgentPayPassageDTO {

    private Integer id;

    /**
     * 支付通道名称
     */
    private String passageName;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 支付接口编码
     */
    private String interfaceCode;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}
