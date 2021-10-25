package tech.xfs.libxfs4j.entity;

import java.time.LocalDateTime;
import java.util.List;


public class Block {
    private BlockHeader header;
    private List<Transaction> transactions;
    private List<Receipt> receipts;

    public BlockHeader getHeader() {
        return header;
    }

    public void setHeader(BlockHeader header) {
        this.header = header;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }
}
