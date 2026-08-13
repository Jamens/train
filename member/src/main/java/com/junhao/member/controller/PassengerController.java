package com.junhao.member.controller;


import com.junhao.common.context.LoginMemberContext;
import com.junhao.common.resp.CommonResp;
import com.junhao.member.req.PassengerQueryReq;
import com.junhao.member.req.PassengerSaveReq;
import com.junhao.member.resp.PassengerQueryResp;
import com.junhao.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<Object> queryList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        List<PassengerQueryResp> lists = passengerService.queryList(req);
        return new CommonResp<>(lists);
    }
}
