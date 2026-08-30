package com.junhao.member.controller;


import com.junhao.common.context.LoginMemberContext;
import com.junhao.common.resp.CommonResp;
import com.junhao.common.resp.PageResp;
import com.junhao.member.req.${Domain}QueryReq;
import com.junhao.member.req.${Domain}SaveReq;
import com.junhao.member.resp.${Domain}QueryResp;
import com.junhao.member.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {
    @Resource
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
        ${domain}Service.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<${Domain}QueryResp> lists = ${domain}Service.queryList(req);
        return new CommonResp<>(lists);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}
