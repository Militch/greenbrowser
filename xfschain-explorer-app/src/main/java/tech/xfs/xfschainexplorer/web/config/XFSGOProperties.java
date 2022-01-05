package tech.xfs.xfschainexplorer.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "xfsgo")
public class XFSGOProperties {
    private String netId;
    private String jsonrpcHttpUrl;
}
