package org.tools.mcp.filesystem.configuration;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tools.mcp.filesystem.component.FilesystemTools;
import org.tools.mcp.filesystem.utils.PathSandbox;

@Configuration
public class McpServerConfig {


    @Bean
    public PathSandbox pathSandbox(FilesystemProperties properties){
        return new PathSandbox(properties.getAllowedRoot());
    }

    /**
     * 创建 Stdio 传输提供者。
     * StdioServerTransportProvider 通过 System.in / System.out
     * 与 MCP 客户端进行 JSON-RPC 通信。
     */
    @Bean
    public StdioServerTransportProvider stdioTransportProvider() {
        return new StdioServerTransportProvider(McpJsonDefaults.getMapper());
    }


    /**
     * 创建并启动 MCP 同步服务器。
     * 并注册工具
     */
    @Bean(destroyMethod = "close")
    public McpSyncServer mcpSyncServer(StdioServerTransportProvider transportProvider, FilesystemTools tools){

        McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo("java-filesystem-mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .build();

        // 逐个注册工具规范
        server.addTool(tools.readFile());
        server.addTool(tools.writeFile());
        //...注册其他工具

        return server;
    }



}
