import { defineStore } from "pinia";
import { ref, computed } from "vue";

/** 登录用户完整信息（对应后端返回的 content 字段） */
export interface UserInfo {
  id: string;
  mobile: string;
  token: string;
}

const STORAGE_KEY = "auth-user";

/** token 有效期（毫秒）：与后端约定一致，24 小时 */
const TOKEN_TTL = 24 * 60 * 60 * 1000;

/** 持久化结构：用户信息 + 过期时间戳 */
interface StoredAuth {
  user: UserInfo;
  expireAt: number;
}

/** 从 localStorage 恢复登录态（校验是否过期，刷新页面后保持登录） */
function loadUser(): UserInfo | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    const stored = JSON.parse(raw) as StoredAuth;
    if (!stored.user?.token) return null;
    // 已过期则视为未登录，并清理存储
    if (Date.now() > stored.expireAt) {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
    return stored.user;
  } catch {
    return null;
  }
}

/**
 * 认证 store：管理登录用户与登录态。
 * 组合式写法，与 <script setup> 风格一致，类型推导更优。
 */
export const useAuthStore = defineStore("auth", () => {
  // 初始化时从本地存储恢复，刷新页面后仍保持登录态
  const user = ref<UserInfo | null>(loadUser());

  const isLoggedIn = computed(() => !!user.value?.token);

  function login(info: UserInfo) {
    user.value = info;
    const stored: StoredAuth = {
      user: info,
      expireAt: Date.now() + TOKEN_TTL,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(stored));
  }

  function logout() {
    user.value = null;
    localStorage.removeItem(STORAGE_KEY);
  }

  return { user, isLoggedIn, login, logout };
});
