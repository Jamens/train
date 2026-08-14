import axios from "axios";
import type { AxiosInstance, AxiosRequestConfig } from "axios";
import { notification } from "ant-design-vue";
import { useAuthStore } from "@/store";

/** 后端统一返回结构 */
export interface Result<T = unknown> {
  success: boolean;
  message?: string;
  data?: T;
}

// 支持通过环境变量 VITE_API_BASE_URL 覆盖默认地址
const env = import.meta.env as unknown as Record<string, string | undefined>;
const BASE_URL = env.VITE_API_BASE_URL ?? "http://localhost:8000";

const instance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10_000,
  headers: { "Content-Type": "application/json" },
});

// 请求拦截器：自动附加登录令牌
instance.interceptors.request.use((config) => {
  const token = useAuthStore().user?.token;
  if (token) {
    config.headers.set("token", `Bearer ${token}`);
  }
  return config;
});

// 响应拦截器：解包后端结构，统一错误提示
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    const msg =
      error.response?.data?.message || error.message || "网络请求失败";
    notification.error({ message: msg });
    return Promise.reject(error);
  },
);

/** GET 请求，返回已解包的响应体 */
function get<T = unknown>(url: string, config?: AxiosRequestConfig) {
  return instance.get<T>(url, config).then((res) => res.data);
}

/** POST 请求，返回已解包的响应体 */
function post<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
) {
  return instance.post<T>(url, data, config).then((res) => res.data);
}

export const request = { get, post };
export default request;
