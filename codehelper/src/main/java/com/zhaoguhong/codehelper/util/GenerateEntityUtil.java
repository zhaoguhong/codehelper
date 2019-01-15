package com.zhaoguhong.codehelper.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhaoguhong.panda.util.VelocityUtils;

/**
 * 
 * 实体类生成工具类
 * 
 * @author zhaoguhong
 * @date 2017年10月19日
 */
public class GenerateEntityUtil {
  private static Set<String> baseEntityfields = Sets.newHashSet();
  private String packagePath = null;// 生成代码包路径
  static {
    baseEntityfields.add("IS_DELETED");
    baseEntityfields.add("CREATE_BY");
    baseEntityfields.add("CREATE_DT");
    baseEntityfields.add("UPDATE_BY");
    baseEntityfields.add("UPDATE_DT");
    baseEntityfields.add("MIMIC_BY");
    baseEntityfields.add("PROXY_BY");
  }

  public static String getEntity(Map<String, Object> map) {
    try {
      return new GenerateEntityUtil().generateEntity(map);
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  public String generateEntity(Map<String, Object> map) {
    String tableName = MapUtils.getString(map, "tableName").replace("'", "");
    Connection conn = DbUtil.openConnection(); // 得到数据库连接
    DbHelper dbHelper = new DbHelper(conn);
    boolean baseEntity = MapUtils.getBooleanValue(map, "baseEntity");
    if (baseEntity) {
      if (!dbHelper.getColNames(tableName).containsAll(baseEntityfields)) {
        return "该表不包含baseEntity的所有字段";
      }
    }
    Map<String, Object> parameters = Maps.newHashMap();
    parameters.put("tableName", tableName.contains(".") ? StringUtils.substringAfter(tableName, ".") : tableName);// 表名
    parameters.put("entityName", DbUtil.toFirstUpper(DbUtil.sqlField2JavaField(MapUtils.getString(parameters, "tableName"))));// 数据库列名
    parameters.put("colNames", dbHelper.getColNames(tableName));// 数据库列名
    parameters.put("fieldNames", dbHelper.getFieldNames(tableName));// 字段名
    parameters.put("fieldTypes", dbHelper.getFieldTypes(tableName));// 字段类型
    parameters.put("primary", dbHelper.getPrimary(tableName));// 主键
    parameters.put("comments", dbHelper.getComments(tableName));// 字段注释
    parameters.put("simpleComments", dbHelper.getSimpleComments(tableName));// 字段注释
    parameters.put("doradoAnnotation", MapUtils.getBooleanValue(map, "doradoAnnotation"));
    parameters.put("fieldAnnotation", MapUtils.getBooleanValue(map, "fieldAnnotation"));
    parameters.put("hibernateAnnotation", MapUtils.getBooleanValue(map, "hibernateAnnotation"));
    parameters.put("baseEntity", baseEntity);
    parameters.put("tableNameComment", dbHelper.getTableComment(tableName));// 表注释
    parameters.put("date", DateFormatUtils.ISO_DATE_FORMAT.format(new Date()));// 当前时间
    parameters.put("baseEntityfields", baseEntityfields);// baseEntityfields
    dbHelper.close();
    String entityStr = VelocityUtils.parse("entity.vm", parameters);
    if (packagePath != null) {
      this.GenerateFile(packagePath, MapUtils.getString(parameters, "entityName"), entityStr);
    }
    return entityStr;
  }

  /**
   * 生成实体类到指定目录
   * 
   * @param packagePath
   * @param tableName
   * @param entityStr
   */
  private void GenerateFile(String packagePath, String entityName, String entityStr) {
    String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
    // System.out.println("路径：" + path);
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    String resPath = path + "/" + entityName + ".java";
    // System.out.println("resPath=" + resPath);
    try {
      FileUtils.writeStringToFile(new File(resPath), entityStr);
    } catch (IOException e) {
      throw new RuntimeException("生成文件错误！");
    }
  }

}
