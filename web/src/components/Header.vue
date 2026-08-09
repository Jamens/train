<template>
  <a-layout-header class="header" id="components-layout-demo-top-side-2">
    <div class="logo relative" />
    <div class="ml-auto flex items-center pr-10 absolute right-0">
      <a-dropdown
        v-if="auth.isLoggedIn"
        :trigger="['click']"
        placement="bottomRight"
        :get-popup-container="
          (triggerNode: HTMLElement) => triggerNode.parentNode as HTMLElement
        "
      >
        <div class="flex justify-end text-white cursor-pointer">
          你好,{{ auth.user?.mobile }}
        </div>
        <template #overlay>
          <a-menu :style="{ width: '160px' }">
            <a-menu-item key="logout" @click="logout">退出登录</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
      <div
        v-else
        class="flex justify-end text-white cursor-pointer"
        @click="goLogin"
      >
        登录
      </div>
    </div>
    <a-menu
      v-model:selectedKeys="selectedKeys1"
      theme="dark"
      mode="horizontal"
      :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="1">nav 1</a-menu-item>
      <a-menu-item key="2">nav 2</a-menu-item>
      <a-menu-item key="3">nav 3</a-menu-item>
    </a-menu>
  </a-layout-header>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store";

const router = useRouter();
const selectedKeys1 = ref<string[]>(["2"]);
const auth = useAuthStore();

function logout() {
  auth.logout();
  router.push({ path: "/Login" });
}

function goLogin() {
  router.push({ path: "/Login" });
}
</script>
<style scoped>
#components-layout-demo-top-side-2 {
  position: relative;
}
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}
</style>
