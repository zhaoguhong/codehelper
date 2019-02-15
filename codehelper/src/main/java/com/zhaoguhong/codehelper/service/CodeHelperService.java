package com.zhaoguhong.codehelper.service;

import com.google.common.collect.Maps;
import com.zhaoguhong.codehelper.util.DbHelper;
import com.zhaoguhong.codehelper.util.DbUtil;
import com.zhaoguhong.codehelper.util.FreeMarkerUtil;
import com.zhaoguhong.codehelper.vo.EntityConfigVO;
import com.zhaoguhong.panda.util.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhaoguhong on 2019/1/17.
 */
@Service
@Slf4j
public class CodeHelperService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String generateEntiy(EntityConfigVO config) {
        if(StringUtils.isNotBlank(config.getIgnoreFields())){
            config.setIgnoreFieldList(Arrays.asList(config.getIgnoreFields().toUpperCase().split(",")));
        }

        String tableName = config.getDatabaseName() + "." +config.getTableName();
        Connection conn = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection(); // 得到数据库连接
        } catch (SQLException e) {
            log.info("获取connection异常");
        }

        DbHelper dbHelper = new DbHelper(conn);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("tableName", tableName.contains(".") ? StringUtils.substringAfter(tableName, ".") : tableName);// 表名
        parameters.put("entityName", DbUtil.toFirstUpper(DbUtil.sqlField2JavaField(MapUtils.getString(parameters, "tableName"))));// 数据库列名
        parameters.put("colNames", dbHelper.getColNames(tableName));// 数据库列名
        parameters.put("fieldNames", dbHelper.getFieldNames(tableName));// 字段名
        parameters.put("fieldTypes", dbHelper.getFieldTypes(tableName));// 字段类型
        parameters.put("primary", dbHelper.getPrimary(tableName));// 主键
        parameters.put("comments", dbHelper.getComments(tableName));// 字段注释
        parameters.put("simpleComments", dbHelper.getSimpleComments(tableName));// 字段注释

       // parameters.put("baseEntity", config.g);
        parameters.put("tableNameComment", dbHelper.getTableComment(tableName));// 表注释
        parameters.put("date", DateFormatUtils.ISO_DATE_FORMAT.format(new Date()));// 当前时间
        parameters.put("config", config);// 当前时间
        parameters.put("entityName", DbUtil.toFirstUpper(DbUtil.sqlField2JavaField(config.getTableName())));// 数据库列名

        String entityStr = FreeMarkerUtil.generate("entity.ftl", parameters);
        if(conn!=null){
            try {
                conn.close();
            }catch( Exception e){
                log.info("关闭connection异常");
            }
        }
        return  entityStr;
    }

}
