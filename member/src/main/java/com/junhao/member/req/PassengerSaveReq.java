package com.junhao.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class PassengerSaveReq {
    private String id;
    private String memberId;
    @NotBlank(message = "旅客姓名不能为空")
    private String name;
    @NotBlank(message = "身份证号不能为空")
    private String idCard;
    @NotBlank(message = "旅客类型不能为空")
    private String type;

    private Date createTime;

    private Date updateTime;
}
