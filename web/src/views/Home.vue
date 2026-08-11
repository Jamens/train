<template>
  <a-layout style="min-height: 100vh">
    <Header />
    <a-layout>
      <Sider />
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content
          :style="{
            background: '#fff',
            padding: '24px',
            margin: 0,
            minHeight: '280px',
          }"
        >
          会员数量: {{ memberCount }}
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script lang="ts" setup>
import Header from "@/components/Header.vue";
import Sider from "@/components/Sider.vue";
import { onMounted, ref } from "vue";
import { getMemberCount } from "@/api";

const memberCount = ref<number>(0);
onMounted(() => {
  getMemberCounts();
});

const getMemberCounts = async () => {
  const res = await getMemberCount();
  if (res.success) {
    memberCount.value = res.content || 0;
  }
  // memberCount.value = res.data.data;
};
</script>
<style scoped>
.site-layout-background {
  background: #fff;
}
</style>
