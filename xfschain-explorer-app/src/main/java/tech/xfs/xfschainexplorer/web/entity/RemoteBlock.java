package tech.xfs.xfschainexplorer.web.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RemoteBlock {
    @SerializedName("height")
    private Long height;
    @SerializedName("version")
    private Integer version;
    @SerializedName("hash_prev_block")
    private String hashPrevBlock;
    @SerializedName("timestamp")
    private Long timestamp;
    @SerializedName("coinbase")
    private String coinbase;
    @SerializedName("state_root")
    private String stateRoot;
    @SerializedName("transactions_root")
    private String transactionsRoot;
    @SerializedName("receipts_root")
    private String receiptsRoot;
    @SerializedName("gas_limit")
    private Long gasLimit;
    @SerializedName("gas_used")
    private Long gasUsed;
    @SerializedName("bits")
    private Long bits;
    @SerializedName("nonce")
    private Long nonce;
    @SerializedName("extranonce")
    private BigDecimal extraNonce;
    @SerializedName("hash")
    private String hash;
    @SerializedName("transactions")
    private List<RemoteTransaction> transactions;
}
