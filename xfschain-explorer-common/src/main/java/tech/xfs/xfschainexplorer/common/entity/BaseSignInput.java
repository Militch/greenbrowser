package tech.xfs.xfschainexplorer.common.entity;

import lombok.Data;

@Data
public class BaseSignInput {
    private Long timestamp;
    private String signType;
    private String sign;
    private String apiKey;
}
