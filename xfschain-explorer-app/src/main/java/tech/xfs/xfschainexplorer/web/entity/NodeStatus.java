package tech.xfs.xfschainexplorer.web.entity;

import lombok.Data;

@Data
public class NodeStatus {
    private long height;
    private String latestBlock;
}
