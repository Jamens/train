<template>
  <a-layout-header class="header" id="components-layout-demo-top-side-2">
    <div class="logo relative text-white">Train</div>
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
      v-model:selectedKeys="selectedKeys"
      theme="dark"
      mode="horizontal"
      :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome"
        ><router-link to="/welcome">
          <coffee-outlined /> &nbsp; 欢迎
        </router-link></a-menu-item
      >
      <a-menu-item key="/passenger"
        ><router-link to="/passenger">
          <user-outlined /> &nbsp; 乘车人管理
        </router-link></a-menu-item
      >
    </a-menu>
  </a-layout-header>
</template>
<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store";
import { CoffeeOutlined, UserOutlined } from "@ant-design/icons-vue";

const router = useRouter();
const selectedKeys = ref<string[]>(["/welcome"]);
const auth = useAuthStore();

function logout() {
  auth.logout();
  router.push({ path: "/Login" });
}

function goLogin() {
  router.push({ path: "/Login" });
}
watch(
  () => router.currentRoute.value.path,
  (path) => {
    selectedKeys.value = [path];
  },
  { immediate: true },
);
</script>
<style scoped>
#components-layout-demo-top-side-2 {
  position: relative;
}
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  line-height: 31px;
  font-size: 16px;
  font-weight: 500;
  margin: 16px 24px 16px 0;
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}
</style>
