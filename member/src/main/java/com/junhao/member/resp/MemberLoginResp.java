package com.junhao.member.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginResp {
    private Long id;

    private String mobile;

    private String token;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberLoginResp{");
        sb.append("id=").append(id);
        sb.append(", mobile='").append(mobile).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
