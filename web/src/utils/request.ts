import axios from "axios";
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";
import { notification } from "ant-design-vue";

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

// 响应拦截器：解包后端结构，统一错误提示
instance.interceptors.response.use(
  (response: AxiosResponse<Result>) => response.data,
  (error) => {
    const msg =
      error.response?.data?.message || error.message || "网络请求失败";
    notification.error({ message: msg });
    return Promise.reject(error);
  }
);

/** GET 请求，返回已解包的 Result */
function get<T = unknown>(url: string, config?: AxiosRequestConfig) {
  return instance.get(url, config) as Promise<Result<T>>;
}

/** POST 请求，返回已解包的 Result */
function post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  return instance.post(url, data, config) as Promise<Result<T>>;
}

export const request = { get, post };
export default request;
