package tech.xfs.libxfs4j.entity;

import java.time.LocalDateTime;

public class BlockHeader {
    private Long height;
    private Integer version;
    private String hash_prev_block;
    private Long timestamp;
    private String coinbase;
    private String state_root;
    private String transactions_root;
    private String receipts_root;
    private String gas_limit;
    private String gas_used;
    private Integer bits;
    private Long nonce;
    private String hash;
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getHash_prev_block() {
        return hash_prev_block;
    }

    public void setHash_prev_block(String hash_prev_block) {
        this.hash_prev_block = hash_prev_block;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(String coinbase) {
        this.coinbase = coinbase;
    }

    public String getState_root() {
        return state_root;
    }

    public void setState_root(String state_root) {
        this.state_root = state_root;
    }

    public String getTransactions_root() {
        return transactions_root;
    }

    public void setTransactions_root(String transactions_root) {
        this.transactions_root = transactions_root;
    }

    public String getReceipts_root() {
        return receipts_root;
    }

    public void setReceipts_root(String receipts_root) {
        this.receipts_root = receipts_root;
    }

    public String getGas_limit() {
        return gas_limit;
    }

    public void setGas_limit(String gas_limit) {
        this.gas_limit = gas_limit;
    }

    public String getGas_used() {
        return gas_used;
    }

    public void setGas_used(String gas_used) {
        this.gas_used = gas_used;
    }

    public Integer getBits() {
        return bits;
    }

    public void setBits(Integer bits) {
        this.bits = bits;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
