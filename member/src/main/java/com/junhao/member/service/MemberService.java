package com.junhao.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.junhao.common.exception.BusinessException;
import com.junhao.common.exception.BusinessExceptionEnum;
import com.junhao.common.util.SnowUtil;
import com.junhao.member.domain.Member;
import com.junhao.member.domain.MemberExample;
import com.junhao.member.mapper.MemberMapper;
import com.junhao.member.req.MemberRegisterReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req){
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(members)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
//            return members.get(0).getId();
        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
