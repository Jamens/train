package com.junhao.common.interceptor;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.junhao.common.context.LoginMemberContext;
import com.junhao.common.resp.MemberLoginResp;
import com.junhao.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header的token参数（兼容 token / Token 两种头名）
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("Token");
        }
        if (StrUtil.isNotBlank(token)) {
            token = token.trim();
            // 兼容前端使用 "Bearer xxx" 标准写法
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7).trim();
            }
            LOG.info("获取会员登录token：{}", token);
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            LOG.info("当前登录会员：{}", loginMember);
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            LoginMemberContext.setMember(member);
        }
        return true;
    }
}
