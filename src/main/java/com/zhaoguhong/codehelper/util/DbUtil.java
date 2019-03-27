package com.zhaoguhong.codehelper.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据库操作工具类
 * 
 * @author zhaoguhong
 * @date 2017年5月3日
 */
public class DbUtil {

  public static Connection openConnection() {



    return null;
  }

  /**
   * 关闭数据库链接
   * 
   * @param con
   * @param statement
   * @param rs
   */
  public static void closeDatabase(Connection con, Statement statement, ResultSet rs) {
    try {
      if (rs != null) {
        rs.close();
      }
      if (statement != null) {
        statement.close();
      }
      if (con != null) {
        con.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException("关闭数据库链接异常");
    }
  }

  public static void closeDatabase(Connection con, Statement statement) {
    closeDatabase(con, statement, null);
  }

  public static void closeDatabase(Connection con) {
    closeDatabase(con, null, null);
  }

  /**
   * 数据库类型转化为java类型
   */
  public static String sqlType2JavaType(String sqlType) {
    if (sqlType.equalsIgnoreCase("bit")) {
      return "Boolean";
    } else if (sqlType.equalsIgnoreCase("tinyint")) {
      return "byte";
    } else if (sqlType.equalsIgnoreCase("smallint")) {
      return "short";
    } else if (sqlType.equalsIgnoreCase("int") || sqlType.equalsIgnoreCase("integer")) {
      return "Integer";
    } else if (sqlType.equalsIgnoreCase("bigint")) {
      return "Long";
    } else if (sqlType.equalsIgnoreCase("float")) {
      return "float";
    } else if (sqlType.equalsIgnoreCase("decimal")) {
      return "BigDecimal";
    } else if (sqlType.equalsIgnoreCase("money") || sqlType.equalsIgnoreCase("smallmoney")
        || sqlType.equalsIgnoreCase("numeric")
        || sqlType.equalsIgnoreCase("real")) {
      return "double";
    } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")) {
      return "Date";
    } else if (sqlType.equalsIgnoreCase("image")) {
      return "Blob";
    } else if (sqlType.equalsIgnoreCase("text")) {
      return "Clob";
    }
    return "String";
  }

  /**
   * 数据库字段转化为java字段
   * 
   * @param str
   * @return
   */
  public static String sqlField2JavaField(String str) {
    boolean allUpperCase = true;
    for (int i = 0; i < str.length(); i++) {
      if (Character.isLowerCase(str.charAt(i))) {
        allUpperCase = false;
        break;
      }
    }
    if (allUpperCase) {
      str = str.toLowerCase();
    }
    int index;
    while ((index = str.indexOf("_")) > 0) {
      if (index == str.length() - 1) {
        str = str.substring(0, index);
      } else {
        str = str.substring(0, index) + str.substring(index + 1, index + 2).toUpperCase()
            + str.substring(index + 2);
      }
    }
    return str;
  }

  /**
   * 第一个字母大写
   * 
   * @param str
   * @return
   */
  public static String toFirstUpper(String str) {
    if (StringUtils.isEmpty(str)) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

}
