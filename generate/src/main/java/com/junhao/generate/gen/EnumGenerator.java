package com.junhao.generate.gen;

import cn.hutool.core.util.StrUtil;
import com.junhao.member.enums.PassengerTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 读取后端Java枚举类，生成前端可直接使用的枚举常量文件（TypeScript）。
 * <p>生成的常量名与字段注释中的“枚举[XxxEnum]”一致（如 PassengerTypeEnum -> PASSENGER_TYPE），
 * 与 vue.ftl 里的 import 名对应。
 * <p>后端枚举改动后，重新运行本类即可同步到前端。
 */
public class EnumGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(EnumGenerator.class);

    /** 输出为 .ts：与 vue.ftl 中 "@/assets/js/enums.ts" 的显式后缀 import 对应，避免被同名 .js 抢占解析 */
    static String path = "web/src/assets/js/enums.ts";

    /** 需要生成到前端的枚举类，新增枚举在此登记即可 */
    private static final List<Class<? extends Enum<?>>> ENUM_CLASSES =
            List.<Class<? extends Enum<?>>>of(PassengerTypeEnum.class);

    public static void main(String[] args) {
        StringBuilder bufferObject = new StringBuilder();
        StringBuilder bufferArray = new StringBuilder();
        long begin = System.currentTimeMillis();
        try {
            for (Class<? extends Enum<?>> clazz : ENUM_CLASSES) {
                toJson(clazz, bufferObject, bufferArray);
            }
            writeTs(bufferObject.append("\r\n").append(bufferArray));
        } catch (Exception e) {
            LOG.error("生成枚举文件失败：" + path, e);
        }
        long end = System.currentTimeMillis();
        LOG.info("执行耗时:{} 毫秒", end - begin);
    }

    /**
     * 把一个枚举类渲染成TS代码：一段以枚举名为key的对象，一段以 _ARRAY 结尾的数组
     *
     * @param clazz        枚举类，需提供 getCode() 与 getDesc() 方法
     * @param bufferObject 对象段内容的追加目标
     * @param bufferArray  数组段内容的追加目标
     * @throws Exception 反射调用 getCode/getDesc/name 失败时抛出
     */
    private static void toJson(Class<? extends Enum<?>> clazz,
                               StringBuilder bufferObject,
                               StringBuilder bufferArray) throws Exception {
        // enumConst：将YesNoEnum变成YES_NO
        String enumConst = StrUtil.toUnderlineCase(clazz.getSimpleName())
                .toUpperCase().replace("_ENUM", "");
        Enum<?>[] enums = clazz.getEnumConstants();
        Method name = clazz.getMethod("name");
        Method getDesc = clazz.getMethod("getDesc");
        Method getCode = clazz.getMethod("getCode");

        // 生成对象：供按枚举名取单个值
        bufferObject.append("export const ").append(enumConst).append(": Record<string, EnumItem> = {");
        for (int i = 0; i < enums.length; i++) {
            Object obj = enums[i];
            bufferObject.append(name.invoke(obj)).append(": {code:\"").append(getCode.invoke(obj)).append("\", desc:\"").append(getDesc.invoke(obj)).append("\"}");
            if (i < enums.length - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("};\r\n");

        // 生成数组：供下拉框/表格渲染遍历
        bufferArray.append("export const ").append(enumConst).append("_ARRAY: EnumItem[] = [");
        for (int i = 0; i < enums.length; i++) {
            Object obj = enums[i];
            bufferArray.append("{code:\"").append(getCode.invoke(obj)).append("\", desc:\"").append(getDesc.invoke(obj)).append("\"}");
            if (i < enums.length - 1) {
                bufferArray.append(",");
            }
        }
        bufferArray.append("];\r\n");
    }

    /**
     * 写文件
     *
     * @param content 要写入的枚举常量内容（不含文件头）
     */
    public static void writeTs(CharSequence content) {
        LOG.info("写出枚举文件：{}", path);
        // 必须 export，否则 ESM 严格模式下前端 import 会报 does not provide an export named ...
        String header = "// 本文件由 com.junhao.generate.server.EnumGenerator 自动生成，请勿手工修改。\r\n"
                + "// 后端 Java 枚举（如 PassengerTypeEnum）改动后，重新运行 EnumGenerator 即可同步。\r\n"
                + "export interface EnumItem {\r\n"
                + "  code: string;\r\n"
                + "  desc: string;\r\n"
                + "}\r\n";
        // 不能用 FileWriter：它固定使用平台默认编码(Windows=GBK)，会把中文写成乱码
        try (Writer osw = new OutputStreamWriter(
                Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8)) {
            osw.write(header);
            osw.write(content.toString());
        } catch (Exception e) {
            LOG.error("写出枚举文件失败：" + path, e);
        }
    }
}
