package ${config.packagePath};

<#list fieldTypes as fieldType>
    <#if !config.ignoreFieldList?seq_contains(colNames[fieldType_index]?upper_case) && fieldType == "Date">
import java.util.Date;
        <#break>
    </#if>
</#list>
<#list fieldTypes as fieldType>
    <#if !config.ignoreFieldList?seq_contains(colNames[fieldType_index]?upper_case) && fieldType == "BigDecimal">
    import java.math.BigDecimal;
        <#break>
    </#if>
</#list>
<#if config.jpaEnable>
import javax.persistence.*;
</#if>
<#if config.doradoAnnotation>
import com.bstek.dorado.annotation.PropertyDef;
</#if>

<#if config.classAnnotation>
/**
 * ${tableNameComment}
 */
</#if>
<#list config.lombokConfig as lombok>
@${lombok}
</#list>
<#if config.jpaEnable>
@Entity
@Table(name = "${tableName}")
</#if>
<#if config.swaggerAnnotation>
@ApiModel(value="${tableNameComment}")
</#if>
public class ${entityName} <#if config.extendClass?? && config.extendClass != "">extends ${config.extendClass} </#if>{

<#list fieldNames as fieldName>
<#--包含忽略字段，不输出-->
<#if !config.ignoreFieldList?seq_contains(colNames[fieldName_index]?upper_case)>
    <#--判断是否输出字段注释-->
    <#if config.fieldAnnotation>
    /**
    * ${(comments[fieldName_index]=="")?string('',comments[fieldName_index])}
    */
    </#if>
    <#--判断是否输出dorado注释-->
    <#if config.doradoAnnotation>
    @PropertyDef(label = "${simpleComments[fieldName_index]}")
    </#if>
    <#--判断是否输出swagger注释-->
    <#if config.swaggerAnnotation>
    @ApiModelProperty(value="${simpleComments[fieldName_index]}")
    </#if>
    <#if config.jpaEnable && (!config.inMethod || config.lombokConfig?seq_contains("Data") || config.lombokConfig?seq_contains("Getter")) >
        <#if colNames[fieldName_index] = primary>
    @Id
    @GeneratedValue <#if config.primaryKeyStrategy?? && config.primaryKeyStrategy != "">(strategy = GenerationType.${config.primaryKeyStrategy})</#if>
        </#if>
        <#if config.column == 3 || (config.column == 1  && fieldName?upper_case!=colNames[fieldName_index]?upper_case)>
    @Column(name = "${colNames[fieldName_index]}")
        </#if>
    </#if>
    private ${fieldTypes[fieldName_index]} ${fieldName};

</#if>
</#list>
<#--用lombok的data注解不生成getset方法-->
<#if !config.lombokConfig?seq_contains("Data")>
<#list fieldNames as fieldName>
<#--包含忽略字段，不输出1 字段不相同时添加, 2 不符合驼峰时添加,3 全部添加-->
<#if !config.ignoreFieldList?seq_contains(colNames[fieldName_index]?upper_case)>
    <#if !config.lombokConfig?seq_contains("Getter")>
        <#if config.jpaEnable && config.inMethod>
            <#if colNames[fieldName_index] = primary>
    @Id
    @GeneratedValue <#if config.primaryKeyStrategy?? && config.primaryKeyStrategy != "">(strategy = GenerationType.${config.primaryKeyStrategy})</#if>
            </#if>
            <#if config.column == 3 || (config.column == 1  && fieldName?upper_case!=colNames[fieldName_index]?upper_case)>
    @Column(name = "${colNames[fieldName_index]}")
            </#if>
        </#if>
    public ${fieldTypes[fieldName_index]} get${fieldName?cap_first}() {
        return ${fieldName};
    }
    </#if>

    <#if !config.lombokConfig?seq_contains("Setter")>
    public void set${fieldName?cap_first}(${fieldTypes[fieldName_index]} ${fieldName}) {
        this.${fieldName} = ${fieldName};
    }

    </#if>
</#if>
</#list>
</#if>
}
