package org.tools.mcp.filesystem.utils;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 这是服务器端的核心逻辑。所有文件操作都必须经过沙箱校验，确保路径不会逃逸到授权目录之外。
 */
public final class PathSandbox {

    private final Path allowedRoot;

    public PathSandbox(String allowedRoot) {
        //获取绝对路径

        this.allowedRoot = Paths.get(allowedRoot).toAbsolutePath().normalize();
    }


    /**
     * 将相对路径解析为绝对路径，并校验是否在沙箱范围内
     * @throws SecurityException 如果路径超出沙箱边界
     */
    public Path resolveSafe(String relativePath){
        Path resolved = allowedRoot.resolve(relativePath).normalize();
        //判断 路径是否 以根路径开始
        if (!resolved.startsWith(allowedRoot)) {
            throw new SecurityException("访问被拒绝：路径超出允许范围 -> " + relativePath);
        }
        return resolved;
    }


    public Path getAllowedRoot() {
        return allowedRoot;
    }
}
