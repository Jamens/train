import request from "@/utils/request";

/** 登录请求参数 */
export interface LoginParams {
  mobile: string;
  code: string;
}

/** 发送验证码请求参数 */
export interface SendCodeParams {
  mobile: string;
}

/** 会员登录 */
export function login(params: LoginParams) {
  return request.post("/member/member/login", params);
}

/** 发送登录验证码 */
export function sendCode(params: SendCodeParams) {
  return request.post("/member/member/send-code", params);
}
