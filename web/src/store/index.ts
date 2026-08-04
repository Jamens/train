import { defineStore } from "pinia";
import { ref, computed } from "vue";

/**
 * 认证 store：管理登录用户与登录态。
 * 组合式写法，与 <script setup> 风格一致，类型推导更优。
 */
export const useAuthStore = defineStore("auth", () => {
  const user = ref<string | null>(null);
  const isLoggedIn = computed(() => user.value !== null);

  function login(name: string) {
    user.value = name;
  }

  function logout() {
    user.value = null;
  }

  return { user, isLoggedIn, login, logout };
});
