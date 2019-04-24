package com.zhaoguhong.codehelper.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

/**
 * 数据库操作辅助类
 *
 * @author zhaoguhong
 * @date 2017年5月3日
 */
public class DbHelper {
    private Connection conn;

    public DbHelper(Connection conn) {
        this.conn = conn;
    }

    /**
     * 获取主键
     *
     * @param tableName
     * @return
     */
    public String getPrimary(String tableName) {
        // 这种方式不能动态指定数据库
        // DatabaseMetaData dbMetaData = conn.getMetaData();
        // ResultSet pkRSet = dbMetaData.getPrimaryKeys(null, null, tableName);
        String firstPrimaryName = null;
        String createSql = getCreateTableSql(tableName);
        List<String> primaryNames = RegularUtil.find(createSql.replace("`", ""), "PRIMARY KEY \\((.*)\\)");
        for (String primaryName : primaryNames) {
            // 有可能有联合主键，取包含ID的
            if (primaryName.toUpperCase().contains("ID")) {
                return primaryName;
            }
        }
        return primaryNames.get(0);
    }

    /**
     * 获取列名
     *
     * @param tableName
     * @return
     */
    public List<String> getColNames(String tableName) {
        List<String> colNames = Lists.newArrayList();// 列名
        String strsql = "select * from " + tableName;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount();
            for (int i = 1; i <= size; i++) {
                colNames.add(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return colNames;
    }

    /**
     * 获取字段名
     *
     * @param tableName
     * @return
     */
    public List<String> getFieldNames(String tableName) {
        List<String> fieldNames = Lists.newArrayList();
        List<String> colNames = getColNames(tableName);
        for (String colName : colNames) {
            fieldNames.add(DbUtil.sqlField2JavaField(colName));
        }
        return fieldNames;
    }

    /**
     * 获取列类型
     *
     * @param tableName
     * @return
     */
    public List<String> getColTypes(String tableName) {
        List<String> colTypes = Lists.newArrayList();// 列类型
        String strsql = "select * from " + tableName;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount();
            for (int i = 1; i <= size; i++) {
                colTypes.add(rsmd.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return colTypes;
    }

    /**
     * 获取字段类型
     *
     * @param tableName
     * @return
     */
    public List<String> getFieldTypes(String tableName) {
        List<String> fieldTypes = Lists.newArrayList();
        List<String> colTypes = getColTypes(tableName);
        for (String colType : colTypes) {
            fieldTypes.add(DbUtil.sqlType2JavaType(colType));
        }
        return fieldTypes;
    }

    /**
     * 获取数据库字段注释
     *
     * @param tableName
     * @return
     */
    public List<String> getComments(String tableName) {
        List<String> commentList = new ArrayList<String>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                // 替换掉注释中的双引号，避免生成实体类时，在注解里面造成语法错误
                commentList.add(rs.getString("Comment").replace("\"", "'"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库链接异常");
        }
        return commentList;
    }

    /**
     * 获取简单数据库字段注释 例如 注释为：含税方式 0不含税 1 含税，那么只取含税方式四个字
     *
     * @param tableName
     * @return
     */
    public List<String> getSimpleComments(String tableName) {
        List<String> commentList = getComments(tableName);
        List<String> simpleComments = Lists.newArrayList();
        String[] separates = {":", "：", ",", "，", " "};
        commentList.forEach(comment -> {
            int index = 0;
            for (String separate : separates) {
                int separateIndex = comment.indexOf(separate);
                if (separateIndex > 0 && (index == 0 || separateIndex < index)) {
                    index = separateIndex;
                }
            }
            simpleComments.add(index > 0 ? comment.substring(0, index) : comment);
        });
        return simpleComments;
    }

    /**
     * 获取表注释
     *
     * @param tableName
     * @return
     */
    public String getTableComment(String tableName) {
        String createSql = getCreateTableSql(tableName);
        String comment = null;
        int index = createSql.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = createSql.substring(index + 9, createSql.length() - 1);
        return comment;
    }

    /**
     * 获得某表的建表语句
     *
     * @param tableName
     * @return
     */
    public String getCreateTableSql(String tableName) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
            if (rs.next()) {
                return rs.getString(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库链接异常");
        }
        return "";
    }

    /**
     * 生成markdown 文档
     *
     * @param parameters
     */
    public String getMarkDown(String tableName) {
        List<String> colNames = getColNames(tableName);
        List<String> fieldNames = getFieldNames(tableName);
        List<String> comments = getComments(tableName);
        StringBuilder sb = new StringBuilder();
        sb.append("| 参数\t| 参数描述\t| 数据类型\t|\n");
        sb.append("|\t----\t|\t----\t|\t----\t|\n");
        for (int i = 0; i < colNames.size(); i++) {
            sb.append("|\t " + colNames.get(i) + "\t|\t "
                    + comments.get(i) + "\t|\t " + fieldNames.get(i)
                    + "\t|\n");
        }
        return sb.toString();
    }

    public void close() {
        DbUtil.closeDatabase(conn, null, null);
    }
}
