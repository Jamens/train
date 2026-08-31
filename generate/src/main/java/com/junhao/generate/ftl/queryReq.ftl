package com.junhao.${module}.req;

import com.junhao.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ${Domain}QueryReq extends PageReq {

<#-- service.ftl / controller.ftl 都固定按会员维度过滤与填充，故每个 QueryReq 都要有 memberId -->
    private Long memberId;
}
