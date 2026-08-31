package com.junhao.generate.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DbUtil.class);

    public static String url = "";
    public static String user = "";
    public static String password = "";

    /**
     * 获取数据库连接，失败时直接抛异常，不返回 null（避免调用方出现难以定位的 NPE）
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DbUtil.url, DbUtil.user, DbUtil.password);
        } catch (ClassNotFoundException e) {
            // 驱动不在 classpath：检查 generate/pom.xml 是否引入 mysql-connector-j
            throw new IllegalStateException("加载MySQL驱动失败，请检查 mysql-connector-j 依赖是否在 classpath 中", e);
        } catch (SQLException e) {
            throw new IllegalStateException("获取数据库连接失败，url：" + DbUtil.url + "，user：" + DbUtil.user, e);
        }
    }

    /**
     * 合法表名：只允许字母、数字、下划线
     * 表名属于 SQL 标识符，无法用 PreparedStatement 占位，必须先做白名单校验防注入
     */
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    private static void checkTableName(String tableName) {
        if (tableName == null || !TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException("非法表名：" + tableName);
        }
    }

    /**
     * 获得表注释
     *
     * @param tableName 表名，只允许字母、数字、下划线
     * @return 表注释；表不存在时返回空字符串
     * @throws SQLException 查询失败时抛出
     */
    public static String getTableComment(String tableName) throws SQLException {
        checkTableName(tableName);
        String tableNameCH = "";
        String sql = "select table_comment from information_schema.tables"
                + " where table_schema = database() and table_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tableNameCH = rs.getString("table_comment");
                }
            }
        }
        LOG.info("表名：{}", tableNameCH);
        return tableNameCH;
    }

    /**
     * 获得所有列信息
     *
     * @param tableName 表名，只允许字母、数字、下划线
     * @return 列信息列表，按表内字段顺序排列；表不存在时返回空列表
     * @throws SQLException 查询失败时抛出
     */
    public static List<Field> getColumnByTableName(String tableName) throws SQLException {
        checkTableName(tableName);
        List<Field> fieldList = new ArrayList<>();
        // 不用 show full columns，因为表名无法用 ? 占位；information_schema 可全参数化，且列别名保持与 show full columns 一致
        String sql = "select column_name as `Field`, column_type as `Type`,"
                + " column_comment as `Comment`, is_nullable as `Null`"
                + " from information_schema.columns"
                + " where table_schema = database() and table_name = ?"
                + " order by ordinal_position";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery(); // stmt 关闭时会自动关闭 rs
            while (rs.next()) {
                String columnName = rs.getString("Field");
                String type = rs.getString("Type");
                String comment = rs.getString("Comment");
                String nullAble = rs.getString("Null"); //YES NO
                Field field = new Field();
                field.setName(columnName);
                field.setNameHump(lineToHump(columnName));
                field.setNameBigHump(lineToBigHump(columnName));
                field.setType(type);
                field.setJavaType(DbUtil.sqlTypeToJavaType(rs.getString("Type")));
                field.setComment(comment);
                if (comment.contains("|")) {
                    field.setNameCn(comment.substring(0, comment.indexOf("|")));
                } else {
                    field.setNameCn(comment);
                }
                field.setNullAble("YES".equals(nullAble)); // 模板里用 #if !field.nullAble 判断，必须是布尔类型
                if (type.toUpperCase().contains("varchar".toUpperCase())) {
                    String lengthStr = type.substring(type.indexOf("(") + 1, type.length() - 1);
                    field.setLength(Integer.valueOf(lengthStr));
                } else {
                    field.setLength(0);
                }
                if (comment.contains("枚举")) {
                    field.setEnums(true);

                    // 以课程等级为例：从注释中的“枚举[CourseLevelEnum]”，得到enumsConst = COURSE_LEVEL
                    int start = comment.indexOf("[");
                    int end = comment.indexOf("]");
                    String enumsName = comment.substring(start + 1, end); // CourseLevelEnum
                    String enumsConst = StrUtil.toUnderlineCase(enumsName)
                            .toUpperCase().replace("_ENUM", "");
                    field.setEnumsConst(enumsConst);
                } else {
                    field.setEnums(false);
                }
                fieldList.add(field);
            }
        }
        LOG.info("列信息：{}", JSONUtil.toJsonPrettyStr(fieldList));
        return fieldList;
    }

    /**
     * 下划线转小驼峰：member_id 转成 memberId
     *
     * @param str 下划线风格的字符串
     * @return 小驼峰风格的字符串
     */
    public static String lineToHump(String str) {
        Pattern linePattern = Pattern.compile("_(\\w)");
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 下划线转大驼峰：member_id 转成 MemberId
     *
     * @param str 下划线风格的字符串
     * @return 大驼峰风格的字符串
     */
    public static String lineToBigHump(String str) {
        String s = lineToHump(str);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 数据库类型转为Java类型
     *
     * @param sqlType 数据库字段类型，如 varchar(50)、bigint、datetime
     * @return 对应的Java类型简称，如 String、Long、Date；未匹配到时返回 String
     */
    public static String sqlTypeToJavaType(String sqlType) {
        if (sqlType.toUpperCase().contains("varchar".toUpperCase())
                || sqlType.toUpperCase().contains("char".toUpperCase())
                || sqlType.toUpperCase().contains("text".toUpperCase())) {
            return "String";
        } else if (sqlType.toUpperCase().contains("datetime".toUpperCase())) {
            return "Date";
        } else if (sqlType.toUpperCase().contains("bigint".toUpperCase())) {
            return "Long";
        } else if (sqlType.toUpperCase().contains("int".toUpperCase())) {
            return "Integer";
        } else if (sqlType.toUpperCase().contains("long".toUpperCase())) {
            return "Long";
        } else if (sqlType.toUpperCase().contains("decimal".toUpperCase())) {
            return "BigDecimal";
        } else if (sqlType.toUpperCase().contains("boolean".toUpperCase())) {
            return "Boolean";
        } else {
            return "String";
        }
    }
}
