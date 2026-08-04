package com.junhao.common.exception;

import lombok.Getter;

@Getter
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已存在"),
    MEMBER_CODE_NOT_MATCH("验证码不匹配"),
    MEMBER_MOBILE_NOT_EXIST("请先获取短信验证码");
    private final String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }
}
