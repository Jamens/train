package com.junhao.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.junhao.common.resp.PageResp;
import com.junhao.common.util.SnowUtil;
import com.junhao.${module}.domain.${Domain};
import com.junhao.${module}.domain.${Domain}Example;
import com.junhao.${module}.mapper.${Domain}Mapper;
import com.junhao.${module}.req.${Domain}QueryReq;
import com.junhao.${module}.req.${Domain}SaveReq;
import com.junhao.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service {

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);
    @Resource
    private ${Domain}Mapper ${domain}Mapper;

    public void save(${Domain}SaveReq req) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        Long reqId = null;
        if (req.getId() != null) {
            try {
                reqId = Long.parseLong(req.getId());
            } catch (NumberFormatException e) {
                LOG.error("id转换失败：{}", req.getId());}
        }
        if (ObjectUtil.isNull(reqId)) {
            ${domain}.setId(SnowUtil.getSnowflakeId());
            ${domain}.setCreateTime(now.toLocalDateTime());
            ${domain}.setUpdateTime(now.toLocalDateTime());
            ${domain}Mapper.insert(${domain});
        } else {
            ${domain}.setId(reqId);
            if (req.getMemberId() != null) {
                try {
                    ${domain}.setMemberId(Long.parseLong(req.getMemberId()));
                } catch (NumberFormatException ignored) {
                }
            }
            ${domain}.setUpdateTime(now.toLocalDateTime());
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }
    }

    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }

    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
