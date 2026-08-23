package com.junhao.generate.server;

import com.junhao.generate.util.FreemarkerUtil;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerate {
    static String toPath = "generate\\src\\main\\java\\com\\junhao\\generate\\test\\";
    static {
        try {
            Files.createDirectories(Path.of(toPath));
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("test.ftl");
        Map<String, Object> param = new HashMap<>();
        param.put("domain", "Test");
        FreemarkerUtil.generator(toPath + "Test.java", param);
    }
}
