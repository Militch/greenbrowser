package tech.xfs.xfschainexplorer.chain.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CountByTime {
    private LocalDateTime time;
    private int count;
}
