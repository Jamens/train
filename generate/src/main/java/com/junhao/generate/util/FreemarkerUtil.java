package com.junhao.generate.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class FreemarkerUtil {
    static String ftlPath = "generate\\src\\main\\java\\com\\junhao\\generate\\ftl\\";

    static Template temp;

    /**
     * 读模板
     */
    public static void initConfig(String ftlName) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setDirectoryForTemplateLoading(new File(ftlPath));
        // 模板文件本身是 UTF-8，必须显式指定，否则会按平台默认编码(Windows=GBK)读取导致中文乱码
        cfg.setDefaultEncoding("UTF-8");
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_34));
        System.out.println(ftlPath + ftlName);
        temp = cfg.getTemplate(ftlName);
    }

    /**
     * 根据模板，生成文件
     */
    public static void generator(String fileName, Map<String, Object> map) throws IOException, TemplateException {
        // 不能用 FileWriter：它固定使用平台默认编码(Windows=GBK)，会把中文写成乱码，必须显式指定 UTF-8
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            temp.process(map, out);
        }
    }
}
