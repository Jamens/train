package com.junhao.generate.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Field {
    private String name;  // 字段名称 course_id
    private String nameHump; //字段名小驼峰 courseId
    private String nameBigHump; //字段名大驼峰 CourseId
    private  String nameCn; //字段中文名称 课程
    private String type; //字段类型 插入(8)
    private String javaType; //java类型 String
    private String comment; //字段注释 课程| Id
    private Boolean nullAble; //是否可为空（YES 可空 => true）
    private Integer length; //字符串长度
    private Boolean enums; //是否为枚举
    private String enumsConst; //枚举常量 COURSE_LEVEL
    private String enumName; //枚举类名 PassengerTypeEnum
    private List<Map<String, String>> enumList; //枚举可选项 [{key:"1", value:"成人"}]，取自Java枚举类的code/desc
}
