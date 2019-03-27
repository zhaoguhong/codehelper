package com.zhaoguhong.codehelper.vo;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by zhaoguhong on 2019/1/15.
 */
@Getter
@Setter
@ToString
public class EntityConfigVO {
    public String databaseName;
    public String tableName;
    public String extendClass;
    public String packagePath;
    public String ignoreFields;
    public List<String> ignoreFieldList = Lists.newArrayList();
    public boolean fieldAnnotation;
    public boolean classAnnotation;
    public boolean doradoAnnotation;
    public boolean swaggerAnnotation;
    public List<String> lombokConfig;

    public boolean jpaEnable;

    public boolean inMethod;
    // 1 字段不相同时添加, 2 不符合驼峰时添加,3 全部添加
    public Integer column;
    public String  primaryKeyStrategy;
}
