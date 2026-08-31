package com.junhao.generate.server;


import com.junhao.generate.util.DbUtil;
import com.junhao.generate.util.Field;
import com.junhao.generate.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ServerGenerate {
    static String servicePath = "member/src/main/java/com/junhao/member/";
    static String pomPath = "generate\\pom.xml";

    static {
        try {
            Files.createDirectories(Path.of(servicePath));
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public static void main(String[] args) throws Exception {
        // 获取mybatis-generator
        String generatorPath = getGeneratorPath();
        // 比如generator-config-member.xml，得到module = member
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        servicePath = servicePath.replace("[module]", module);
        // new File(servicePath).mkdirs();
        System.out.println("servicePath: " + servicePath);

        Document document = new SAXReader().read("generate/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // connectionURL / userId / password 是 <jdbcConnection> 的属性，不是 <property> 子元素
        Node connectionURL = document.selectSingleNode("//jdbcConnection/@connectionURL");
        Node userId = document.selectSingleNode("//jdbcConnection/@userId");
        Node password = document.selectSingleNode("//jdbcConnection/@password");
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();
        System.out.println("url: " + DbUtil.url);
        System.out.println("user: " + DbUtil.user);

        // 示例：表名 junhao_test
        // Domain = JunhaoTest
        String Domain = domainObjectName.getText();
        // domain = junhaoTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = junhao-test
        String do_main = tableName.getText().replace("_", "-");
        //表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);
        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn", tableNameCn);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        System.out.println("组装参数：" + param);

        gen(Domain, param,"service", "service");
        gen(Domain, param,"controller", "controller");
        gen(Domain, param, "req","saveReq");
    }

    private static void gen(String Domain, Map<String, Object> param, String packageName, String targetPath) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(targetPath + ".ftl");
        String toPath = servicePath + packageName + "/";
        Files.createDirectories(Path.of(toPath));
        String Target = targetPath.substring(0, 1).toUpperCase() + targetPath.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        // 打印生成的绝对路径，方便直接在 IDEA / 文件管理器中定位
        System.out.println("生成文件：" + Path.of(fileName).toAbsolutePath().normalize());
        FreemarkerUtil.generator(fileName, param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

    /**
     * 获取java类型
     *
     * @param fieldList 字段列表
     * @return java类型集合
     */

    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> typeSet = new HashSet<>();
        for (Field field : fieldList) {
            typeSet.add(field.getJavaType());
        }
        return typeSet;
    }
}
