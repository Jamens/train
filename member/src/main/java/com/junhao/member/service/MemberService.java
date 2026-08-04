package com.junhao.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.junhao.common.exception.BusinessException;
import com.junhao.common.exception.BusinessExceptionEnum;
import com.junhao.common.util.SnowUtil;
import com.junhao.member.domain.Member;
import com.junhao.member.domain.MemberExample;
import com.junhao.member.mapper.MemberMapper;
import com.junhao.member.req.MemberRegisterReq;
import com.junhao.member.req.MemberSendCodeReq;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req) {
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

    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(members)) {
            logger.info("手机号{}未注册", mobile);
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            logger.info("手机号{}已注册", mobile);
        }
//        String code = RandomUtil.randomString(4);
        String code = "1234";
        logger.info("手机号{}发送验证码{}", mobile, code);
        //后续可对接短信平台

    }
}
