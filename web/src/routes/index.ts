import { createWebHistory, createRouter } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import Login from "../views/Login.vue";
import { useAuthStore } from "@/store";

// 增强 RouteMeta 类型，支持 requiresAuth
declare module "vue-router" {
  interface RouteMeta {
    requiresAuth?: boolean;
  }
}

const routes: RouteRecordRaw[] = [
  { path: "/", component: Home, meta: { requiresAuth: true } },
  { path: "/Login", component: Login },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全局前置守卫：未登录（无 token）访问受保护页面时跳转到登录页
router.beforeEach((to) => {
  if (to.meta.requiresAuth && !useAuthStore().isLoggedIn) {
    return { path: "/Login" };
  }
});

export default router;
