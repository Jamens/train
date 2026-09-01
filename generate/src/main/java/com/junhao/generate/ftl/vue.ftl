<template>
  <a-space>
    <a-button type="primary" @click="handleQuery({ page: 1, size: pagination.pageSize })"
      >刷新</a-button
    >
<#if !readOnly>
    <a-button type="primary" @click="showModal">新增</a-button>
</#if>
  </a-space>
  <a-table
    :dataSource="${domain}s"
    :columns="columns"
    :pagination="pagination"
    @change="handleTableChange"
    :loading="loading"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'operation'">
        <a-button type="link" @click="edit(record)">编辑</a-button>
        <a-popconfirm
          title="确认删除该${tableNameCn}？"
          ok-text="删除"
          cancel-text="取消"
          @confirm="handleDelete(record)"
        >
          <a-button type="link" danger>删除</a-button>
        </a-popconfirm>
      </template>
    </template>
  </a-table>
<#if !readOnly>
  <a-modal
    v-model:open="visible"
    title="${tableNameCn}"
    centered
    @ok="handleOk"
    ok-text="确认"
    cancel-text="取消"
  >
    <a-form
      ref="formRef"
      :model="${domain}"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
    >
  <#-- memberId 由登录上下文填充、id 由雪花生成、时间戳由后端维护，均不出现在表单中 -->
  <#list fieldList as field>
    <#if field.name!="id" && field.nameHump!="memberId" && field.nameHump!="createTime" && field.nameHump!="updateTime">
      <a-form-item
        label="${field.nameCn}"
        name="${field.nameHump}"
      <#if !field.nullAble>
        :rules="[{ required: true, message: '<#if field.enums>请选择<#else>请输入</#if>${field.nameCn}' }]"
      </#if>
      >
      <#if field.enums>
        <a-select v-model:value="${domain}.${field.nameHump}">
      <#-- 模板里无法直接使用 window，需先在 script 中定义为顶层常量 -->
          <a-select-option
            v-for="item in ${field.enumsConst}_ARRAY"
            :key="item.key"
            :value="item.key"
          >
            {{ item.value }}
          </a-select-option>
        </a-select>
      <#elseif field.javaType=='Date'>
        <#-- field.type 形如 datetime(3)/date/time(6)，需去掉精度后再比较 -->
        <#assign baseType = field.type?split('(')[0]>
        <#if baseType=='time'>
        <a-time-picker
          v-model:value="${domain}.${field.nameHump}"
          valueFormat="HH:mm:ss"
          placeholder="请选择时间"
        />
        <#elseif baseType=='date'>
        <a-date-picker
          v-model:value="${domain}.${field.nameHump}"
          valueFormat="YYYY-MM-DD"
          placeholder="请选择日期"
        />
        <#else>
        <a-date-picker
          v-model:value="${domain}.${field.nameHump}"
          valueFormat="YYYY-MM-DD HH:mm:ss"
          show-time
          placeholder="请选择日期"
        />
        </#if>
      <#else>
        <a-input v-model:value="${domain}.${field.nameHump}" />
      </#if>
      </a-form-item>
    </#if>
  </#list>
    </a-form>
  </a-modal>
</#if>
</template>
<script lang="ts" setup>
import { notification } from "ant-design-vue";
import { nextTick, onMounted, reactive, ref } from "vue";
import {
<#if !readOnly>
  delete${Domain},
  save${Domain},
</#if>
  get${Domain}getList,
} from "@/api";
import type { ${Domain}QueryResp, ${Domain}VO } from "@/api/type";

defineOptions({
  name: "${do_main}-view",
});

// ${tableNameCn}表单初始状态（新增/重置时复用）
const formRef = ref();
const ${domain?upper_case}_INITIAL = {
<#list fieldList as field>
  ${field.nameHump}: undefined,
</#list>
};

const visible = ref(false);
const ${domain} = reactive({ ...${domain?upper_case}_INITIAL });

// 一行重置所有字段
const reset${Domain} = () =>
  Object.assign(${domain}, ${domain?upper_case}_INITIAL);
const ${domain}s = ref<${Domain}VO[]>([]);
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
});
const loading = ref(false);

const handleTableChange = (paginationVal: { current: any; pageSize: any }) => {
  pagination.current = paginationVal.current || 1;
  pagination.pageSize = paginationVal.pageSize || 1;
  handleQuery({
    page: paginationVal.current,
    size: paginationVal.pageSize,
  });
};

<#list fieldList as field>
<#if field.enums>
// ${field.nameCn}可选项，生成自后端枚举类 ${field.enumName!}，枚举变更后需重新生成本文件
const ${field.enumsConst}_ARRAY: { key: string; value: string }[] = [
<#list field.enumList! as item>
  { key: "${item.key}", value: "${item.value}" },
</#list>
];
// ${field.nameCn}映射：存储值 -> 展示文案
const ${field.enumsConst}_MAP: Record<string, string> = ${field.enumsConst}_ARRAY.reduce(
  (map: Record<string, string>, item) => {
    map[item.key] = item.value;
    return map;
  },
  {} as Record<string, string>
);

</#if>
</#list>
const columns = [
<#-- 与表单保持同一套过滤规则：id / memberId / 时间戳不展示 -->
<#list fieldList as field>
<#if field.name!="id" && field.nameHump!="memberId" && field.nameHump!="createTime" && field.nameHump!="updateTime">
  {
    title: "${field.nameCn}",
    dataIndex: "${field.nameHump}",
    key: "${field.nameHump}",
  <#if field.enums>
    customRender: ({ value }: { value: string }) =>
      ${field.enumsConst}_MAP[value] ?? value,
  </#if>
  },
</#if>
</#list>
<#if !readOnly>
  {
    title: "操作",
    key: "operation",
  },
</#if>
];

<#if !readOnly>
const showModal = () => {
  reset${Domain}();
  visible.value = true;
  // 清除上一次触发校验残留的红字（表单错误状态与字段值相互独立）
  nextTick(() => formRef.value?.clearValidate());
};

const edit = (record: ${Domain}VO) => {
  reset${Domain}();
  <#-- 剔除时间戳字段，避免把后端时间回填进表单 -->
  const { <#list fieldList as f><#if f.nameHump=='createTime' || f.nameHump=='updateTime'>${f.nameHump}, </#if></#list>...editable } = record;
  Object.assign(${domain}, editable);
  visible.value = true;
  // 回填后清除可能残留的校验红字（与新增共用同一表单实例）
  nextTick(() => formRef.value?.clearValidate());
};

const handleDelete = (record: ${Domain}VO) => {
  if (record.id == null) return;
  delete${Domain}(record.id).then((res) => {
    if (res.success) {
      notification.success({ description: "删除成功！", message: "" });
      handleQuery({ page: 1, size: pagination.pageSize });
    } else {
      notification.error({ description: res.message, message: "" });
    }
  });
};

const handleOk = () => {
  formRef.value
    .validate()
    .then(() => {
      save${Domain}(${domain}).then((res) => {
        if (res.success) {
          notification.success({
            description: ${domain}.id ? "编辑成功！" : "新增成功！",
            message: "",
          });
          visible.value = false;
          reset${Domain}();
          handleQuery({ page: 1, size: pagination.pageSize });
        } else {
          notification.error({
            description: res.message,
            message: "",
          });
        }
      });
    })
    .catch(() => {
      // 校验未通过，不关闭弹窗、不提交
    });
};
</#if>

const handleQuery = (param: { page: any; size: any }) => {
  loading.value = true;
  get${Domain}getList(param).then((res: ${Domain}QueryResp) => {
    loading.value = false;
    if (res.success && res.content) {
      ${domain}s.value = res.content.list;
      pagination.current = param.page;
      pagination.total = res.content.total;
    } else {
      notification.error({
        description: res.message,
        message: "",
      });
    }
  });
};

onMounted(() => {
  handleQuery({ page: 1, size: pagination.pageSize });
});
</script>
<style></style>
