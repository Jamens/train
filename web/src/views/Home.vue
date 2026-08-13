<template>
  <a-layout style="min-height: 100vh">
    <Header />
    <a-layout>
      <Sider />
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item v-for="(item, i) in crumbs" :key="i">
            {{ item }}
          </a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content
          :style="{
            background: '#fff',
            padding: '24px',
            margin: 0,
            minHeight: '280px',
          }"
        >
          <!-- 会员数量: {{ memberCount }} -->
          <router-view></router-view>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script lang="ts" setup>
import Header from "@/components/Header.vue";
import Sider from "@/components/Sider.vue";
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { getMemberCount } from "@/api";

const route = useRoute();

// 由当前路由路径动态生成面包屑（按 / 分段并首字母大写）
const crumbs = computed(() =>
  route.path
    .split("/")
    .filter(Boolean)
    .map((s) => s.charAt(0).toUpperCase() + s.slice(1)),
);

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
