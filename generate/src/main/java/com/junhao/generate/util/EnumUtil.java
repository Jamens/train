package com.junhao.generate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析Java枚举类，把 code/desc 取出来供前端模板生成下拉选项。
 * 前端不再依赖 window.XXX_ARRAY 的运行时注入，避免枚举未注入时 undefined.reduce() 崩溃。
 */
public class EnumUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EnumUtil.class);

    /** 从字段注释中提取枚举类名：旅客类型|枚举[PassengerTypeEnum] -> PassengerTypeEnum */
    private static final Pattern ENUM_NAME_PATTERN = Pattern.compile("枚举\\[(\\w+)]");

    /** 匹配枚举项：ADULT("1", "成人"), */
    private static final Pattern ENUM_ITEM_PATTERN =
            Pattern.compile("(\\w+)\\s*\\(\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*\\)");

    /**
     * 从字段注释中提取枚举类名
     *
     * @param comment 字段注释，如 旅客类型|枚举[PassengerTypeEnum]
     * @return 枚举类名；注释中没有枚举声明时返回 null
     */
    public static String getEnumName(String comment) {
        if (comment == null) {
            return null;
        }
        Matcher m = ENUM_NAME_PATTERN.matcher(comment);
        return m.find() ? m.group(1) : null;
    }

    /**
     * 读取Java枚举类文件，解析出枚举项列表
     *
     * @param module   模块名，如 member
     * @param enumName 枚举类名，如 PassengerTypeEnum
     * @return 枚举项列表，每项含 key(code) / value(desc)；文件不存在或解析失败时返回空列表
     */
    public static List<Map<String, String>> parseEnum(String module, String enumName) {
        List<Map<String, String>> list = new ArrayList<>();
        if (enumName == null || module == null) {
            return list;
        }
        Path path = Path.of(module, "src", "main", "java", "com", "junhao", module, "enums", enumName + ".java");
        if (!Files.isRegularFile(path)) {
            LOG.warn("枚举类文件不存在，跳过枚举解析：{}", path);
            return list;
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            Matcher m = ENUM_ITEM_PATTERN.matcher(content);
            while (m.find()) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("key", m.group(2));
                item.put("value", m.group(3));
                list.add(item);
            }
            LOG.info("解析枚举 {} 得到 {} 项", enumName, list.size());
        } catch (IOException e) {
            LOG.error("读取枚举类失败：" + path, e);
        }
        return list;
    }
}
