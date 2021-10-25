package com.esiran.greenadmin.web.entity;

public class Status {
    private Long latestHeight;
    private Long blockReward;
    private Long difficulty;

    public Long getLatestHeight() {
        return latestHeight;
    }

    public void setLatestHeight(Long latestHeight) {
        this.latestHeight = latestHeight;
    }

    public Long getBlockReward() {
        return blockReward;
    }

    public void setBlockReward(Long blockReward) {
        this.blockReward = blockReward;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }
}
