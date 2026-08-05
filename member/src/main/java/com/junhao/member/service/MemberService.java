package com.junhao.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.junhao.common.exception.BusinessException;
import com.junhao.common.exception.BusinessExceptionEnum;
import com.junhao.common.util.SnowUtil;
import com.junhao.member.domain.Member;
import com.junhao.member.domain.MemberExample;
import com.junhao.member.mapper.MemberMapper;
import com.junhao.member.req.MemberLoginReq;
import com.junhao.member.req.MemberRegisterReq;
import com.junhao.member.req.MemberSendCodeReq;
import com.junhao.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        Member members = selectByMobile(mobile);
        if (ObjectUtil.isNotNull(members)) {
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
        Member member = selectByMobile(mobile);
        if (ObjectUtil.isNull(member)) {
            logger.info("手机号{}未注册，自动注册", mobile);
            member = new Member();
            member.setId(SnowUtil.getSnowflakeId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }
        String code = RandomUtil.randomNumbers(4);
        memberMapper.updateByPrimaryKey(member);
        logger.info("手机号{}发送验证码{}", mobile, code);
        //后续可对接短信平台
    }

    public MemberLoginResp sendLogin(MemberLoginReq req) {
        String mobile = req.getMobile();
        String code = req.getCode();
        Member members = selectByMobile(mobile);
        if (ObjectUtil.isNull(members)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        if (!"1234".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_CODE_NOT_MATCH);

        }
        return BeanUtil.copyProperties(members, MemberLoginResp.class);

    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(members)) {
            return null;
        } else {
            return members.get(0);
        }
    }
}
