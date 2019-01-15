package com.zhaoguhong.codehelper.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhaoguhong on 2019/1/15.
 */
@Getter
@Setter
public class EntityConfigVO {
    public String databaseName;
    public String extendClass;
    public String packagePath;
    public String ignoreFields;
    public boolean fieldAnnotation;
    public boolean classAnnotation;
    public boolean doradoAnnotation;
    public Jpa jpa;
    public String lombokConfig;
}

@Getter
@Setter
class Jpa {
    public boolean enable;
    public boolean inMethod;
    public Integer column;
    public String primaryKeyStrategy;
}