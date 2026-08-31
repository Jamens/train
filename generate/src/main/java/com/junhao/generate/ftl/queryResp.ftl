package com.junhao.${module}.resp;

<#-- 按需 import：只有存在 id / xxx_id 字段时才需要 @JsonSerialize 转字符串 -->
<#assign needJsonSerialize = false>
<#list fieldList as f>
<#if f.name=='id' || f.name?ends_with('_id')>
<#assign needJsonSerialize = true>
</#if>
</#list>
<#if needJsonSerialize>
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;
</#if>
<#list typeSet as type>
<#if type=='Date'>
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
</#if>
<#if type=='BigDecimal'>
import java.math.BigDecimal;
</#if>
</#list>
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ${Domain}QueryResp {

    <#list fieldList as field>
    /**
     * ${field.comment}
     */
    <#if field.javaType=='Date'>
    <#-- field.type 是数据库原始类型，形如 datetime(3)/date/time(6)，需去掉括号及精度后再比较，否则永远匹配不上 -->
    <#assign baseType = field.type?split('(')[0]>
        <#if baseType=='time'>
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
        <#elseif baseType=='date'>
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
        <#else>
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        </#if>
    </#if>
    <#if field.name=='id' || field.name?ends_with('_id')>
    @JsonSerialize(using= ToStringSerializer.class)
    </#if>
    private ${field.javaType} ${field.nameHump};

    </#list>
}
