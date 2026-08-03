package com.junhao.common.exception;

import lombok.Getter;

@Getter
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已存在");

    private final String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }
}
