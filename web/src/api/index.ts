import request from "@/utils/request";
import type { UserInfo } from "@/store";

/** 登录请求参数 */
export interface LoginParams {
  mobile: string;
  code: string;
}

/** 发送验证码请求参数 */
export interface SendCodeParams {
  mobile: string;
}

/** 登录接口返回结构（content 在响应体顶层） */
export interface LoginResponse {
  success: boolean;
  message?: string;
  content?: UserInfo;
}

/** 通用响应结构 */
export interface ApiResponse {
  success: boolean;
  message?: string;
}

/** 会员登录 */
export function login(params: LoginParams) {
  return request.post<LoginResponse>("/member/login", params);
}

/** 发送登录验证码 */
export function sendCode(params: SendCodeParams) {
  return request.post<ApiResponse>("/member/send-code", params);
}

/** 会员数量接口返回结构（content 为数字） */
export interface MemberCountResponse {
  success: boolean;
  message?: string;
  content?: number;
}

/** 获取会员数量信息 */
export function getMemberCount() {
  return request.get<MemberCountResponse>("/member/count");
}
