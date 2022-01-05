package tech.xfs.xfschainexplorer.web.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RemoteTransaction {
    @SerializedName("hash")
    private String hash;
    @SerializedName("version")
    private Integer version;
    @SerializedName("to")
    private String to;
    @SerializedName("from")
    private String from;
    @SerializedName("gas_price")
    private BigDecimal gasPrice;
    @SerializedName("gas_limit")
    private BigDecimal gasLimit;
    @SerializedName("nonce")
    private Long nonce;
    @SerializedName("value")
    private BigDecimal value;
    @SerializedName("data")
    private String data;
}
