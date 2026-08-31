package com.junhao.member.req;

<#list typeSet as type>
<#if type=='Date'>
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
</#if>
<#if type=='BigDecimal'>
import java.math.BigDecimal;
</#if>
</#list>

<#-- 先扫一遍，判断实际需要哪些校验注解，避免生成无用的 import -->
<#assign needNotBlank = false>
<#assign needNotNull = false>
<#list fieldList as f>
<#if f.name!="id" && f.nameHump!="memberId" && f.nameHump!="createTime" && f.nameHump!="updateTime" && !f.nullAble>
<#if f.javaType=='String'>
<#assign needNotBlank = true>
<#else>
<#assign needNotNull = true>
</#if>
</#if>
</#list>
<#if needNotBlank>
import jakarta.validation.constraints.NotBlank;
</#if>
<#if needNotNull>
import jakarta.validation.constraints.NotNull;
</#if>
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ${Domain}SaveReq {
<#list fieldList as field>
    /**
     * ${field.comment!}
     */
<#if field.javaType=='Date'>
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
</#if>
<#-- id 由后端雪花生成、memberId 由登录上下文填充，都不由前端传入，故不加非空校验 -->
<#if field.name!="id" && field.nameHump!="memberId" && field.nameHump!="createTime" && field.nameHump!="updateTime" && !field.nullAble>
<#if field.javaType=='String'>
    @NotBlank(message = "【${field.nameCn}】不能为空")
<#else>
    @NotNull(message = "【${field.nameCn}】不能为空")
</#if>
</#if>
<#-- 雪花ID超过JS的53位精度，bigint 字段在 Req 中统一用 String 接收 -->
<#if field.javaType=='Long'>
    private String ${field.nameHump};
<#else>
    private ${field.javaType} ${field.nameHump};
</#if>
</#list>
}
