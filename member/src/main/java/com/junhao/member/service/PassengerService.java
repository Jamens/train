package com.junhao.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.junhao.common.context.LoginMemberContext;
import com.junhao.common.resp.PageResp;
import com.junhao.common.util.SnowUtil;
import com.junhao.member.domain.Passenger;
import com.junhao.member.domain.PassengerExample;
import com.junhao.member.mapper.PassengerMapper;
import com.junhao.member.req.PassengerQueryReq;
import com.junhao.member.req.PassengerSaveReq;
import com.junhao.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);
    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        Long reqId = null;
        if (req.getId() != null) {
            try {
                reqId = Long.parseLong(req.getId());
            } catch (NumberFormatException e) {
                LOG.error("id转换失败：{}", req.getId());}
        }
        if (ObjectUtil.isNull(reqId)) {
            passenger.setId(SnowUtil.getSnowflakeId());
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setCreateTime(now.toLocalDateTime());
            passenger.setUpdateTime(now.toLocalDateTime());
            passengerMapper.insert(passenger);
        } else {
            passenger.setId(reqId);
            if (req.getMemberId() != null) {
                try {
                    passenger.setMemberId(Long.parseLong(req.getMemberId()));
                } catch (NumberFormatException ignored) {
                }
            }
            passenger.setUpdateTime(now.toLocalDateTime());
            passengerMapper.updateByPrimaryKey(passenger);
        }
    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }

    public void delete(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }
}
