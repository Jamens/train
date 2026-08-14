package com.junhao.member.req;

import com.junhao.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PassengerQueryReq extends PageReq {

    private Long memberId;
}
