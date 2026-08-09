import { defineStore } from "pinia";
import { ref, computed } from "vue";

/** 登录用户完整信息（对应后端返回的 content 字段） */
export interface UserInfo {
  id: string;
  mobile: string;
  token: string;
}

const STORAGE_KEY = "auth-user";

/** 从 localStorage 恢复登录态（刷新页面后保持登录） */
function loadUser(): UserInfo | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserInfo;
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
    localStorage.setItem(STORAGE_KEY, JSON.stringify(info));
  }

  function logout() {
    user.value = null;
    localStorage.removeItem(STORAGE_KEY);
  }

  return { user, isLoggedIn, login, logout };
});
