package org.tools.mcp.filesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileSystemApplication.class, args);

        // 保持主线程存活，避免 Stdio 传输因主线程退出而中断
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
