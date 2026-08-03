package com.junhao.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberRegisterReq {
    @NotBlank(message = "手机号不能为空")
    private String mobile;
}
