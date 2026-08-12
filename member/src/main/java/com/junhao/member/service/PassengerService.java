package com.junhao.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.junhao.common.context.LoginMemberContext;
import com.junhao.common.util.SnowUtil;
import com.junhao.member.domain.Passenger;
import com.junhao.member.mapper.PassengerMapper;
import com.junhao.member.req.PassengerSaveReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {
    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeId());
        passenger.setCreateTime(now.toLocalDateTime());
        passenger.setUpdateTime(now.toLocalDateTime());
        passengerMapper.insert(passenger);
    }
}
