package com.junhao.member.resp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class PassengerQueryResp {
    private Long id;
    private Long memberId;

    private String name;
    private String idCard;
    private String type;

    private Date createTime;

    private Date updateTime;

}
