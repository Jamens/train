<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
      v-model:selectedKeys="selectedKeys2"
      v-model:openKeys="openKeys"
      mode="inline"
      :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; 欢迎
        </router-link></a-menu-item
      >
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined /> &nbsp; 乘车人管理
        </router-link></a-menu-item
      >
    </a-menu>
  </a-layout-sider>
</template>
<script lang="ts" setup>
import { ref, watch } from "vue";
const router = useRouter();
import { UserOutlined, CoffeeOutlined } from "@ant-design/icons-vue";
import { useRouter } from "vue-router";
const selectedKeys2 = ref<string[]>(["/welcome"]);
const openKeys = ref<string[]>([]);
watch(
  () => router.currentRoute.value.path,
  (path) => {
    selectedKeys2.value = [path];
  },
  { immediate: true },
);
</script>
<style scoped></style>
