package com.feng.nio.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件拷贝，包括目录
 */
public class TestFileCopy {

    public static void main(String[] args) throws IOException {

        String source = "D:\\var";
        String target = "D:\\var_copy";

        Files.walk(Paths.get(source)).forEach(path -> {
            System.out.println(path); // 理解path，就是原来的路径
            String targetName = path.toString().replace(source, target);  // 将原来的路径替换成目标路径
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                }
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
