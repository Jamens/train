package com.junhao.generate.server;


import com.junhao.generate.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerate {
    static String servicePath = "member/src/main/java/com/junhao/member/service/";
    static String pomPath = "generate\\pom.xml";
    static {
        try {
            Files.createDirectories(Path.of(servicePath));
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public static void main(String[] args) throws DocumentException, IOException, TemplateException {
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

        // 示例：表名 junhao_test
        // Domain = JunhaoTest
        String Domain = domainObjectName.getText();
        // domain = junhaoTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = junhao-test
        String do_main = tableName.getText().replace("_", "-");

        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("组装参数：" + param);

        FreemarkerUtil.initConfig("service.ftl");
        FreemarkerUtil.generator(servicePath + Domain + "Service.java", param);
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
}
