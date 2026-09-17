package org.tools.mcp.filesystem.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mcp.filesystem")
public class FilesystemProperties {

    /**
     * AI 可访问的根目录（安全沙箱边界）
     */
    private String allowedRoot;

    public String getAllowedRoot() {
        return allowedRoot;
    }

    public void setAllowedRoot(String allowedRoot) {
        this.allowedRoot = allowedRoot;
    }
}
