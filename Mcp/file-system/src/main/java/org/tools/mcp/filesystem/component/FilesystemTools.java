package org.tools.mcp.filesystem.component;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Component;
import org.tools.mcp.filesystem.utils.PathSandbox;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

@Component
public class FilesystemTools {


    private final PathSandbox sandbox;

    public FilesystemTools(PathSandbox sandbox) {
        this.sandbox = sandbox;
    }

    /**
     * 读取文件内容
     */
    public SyncToolSpecification readFile(){

        //1.使用Map 定义输入的 schema
        Map<String, Object> schemaMap = Map.of(
                "type","object",
                "properties", Map.of(
                        "path",Map.of(
                                "type","string",
                                "description","相对于根目录的文件路径"
                        )
                ),
                "required", List.of("path")
        );

        //2.使用 build 构建 tool
        McpSchema.Tool tool = McpSchema.Tool.builder("read_input", schemaMap).description("读取指定文件的全部文本内容").build();

        //3.使用 build 构建 SyncToolSpecification 从 request 中提取参数
        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    //从request的 arguments 中获取参数
                    String path = (String) request.arguments().get("path");
                    try {
                        String content = Files.readString(sandbox.resolveSafe(path));
                        return McpSchema.CallToolResult.builder()
                                .addTextContent(content)
                                .isError(false)
                                .build();
                    } catch (Exception e) {
                        return McpSchema.CallToolResult.builder()
                                .addTextContent("读取失败: " + e.getMessage())
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }



    public SyncToolSpecification writeFile(){
        Map<String,Object> schemaMap = Map.of(
                "type","object",
                "properties",Map.of(
                        "path",Map.of(
                                "type","string",
                                "description", "相对于根目录的文件路径"
                        ),
                        "content", Map.of(
                                "type","string",
                                "description","要写入的文本内容"
                        )
                ),
                "required",List.of("path","content")
        );

        McpSchema.Tool tool = McpSchema.Tool.builder("write_file", schemaMap)
                .description("将文本内容写入指定文件（如果文件不存在则创建）")
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    String path = (String) request.arguments().get("path");
                    String content = (String) request.arguments().get("content");
                    try {
                        Path target = sandbox.resolveSafe(path);
                        Files.createDirectories(target.getParent());
                        Files.writeString(target, content,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING);
                        return McpSchema.CallToolResult.builder()
                                .addTextContent("写入成功：" + target)
                                .isError(false)
                                .build();
                    } catch (Exception e) {
                        return McpSchema.CallToolResult.builder()
                                .addTextContent("写入失败： "+ e.getMessage())
                                .isError(true)
                                .build();
                    }

                })
                .build();

    }


    // ... 其他工具方法 (listDirectoryTool, existsTool, deleteTool)
    // 采用完全相同的模式：
    // 1. 用 Map 定义 inputSchema
    // 2. 用 McpSchema.Tool.builder() 创建 Tool
    // 3. 用 McpServerFeatures.SyncToolSpecification.builder() 绑定 tool 和 callHandler
    // 4. 在 callHandler 中通过 request.arguments() 获取参数

}
