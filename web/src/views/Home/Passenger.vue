<template>
  <a-space>
    <a-button
      type="primary"
      @click="handleQuery({ page: 1, size: pagination.pageSize })"
      >刷新</a-button
    >
    <a-button type="primary" @click="showModal">新增</a-button>
  </a-space>
  <a-table
    :dataSource="passengers"
    :columns="columns"
    :pagination="pagination"
    @change="handleTableChange"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'operation'">
        <a-button type="link" @click="edit(record)">编辑</a-button>
        <a-popconfirm
          title="确认删除该乘车人？"
          ok-text="删除"
          cancel-text="取消"
          @confirm="handleDelete(record)"
        >
          <a-button type="link" danger>删除</a-button>
        </a-popconfirm>
      </template>
    </template>
  </a-table>
  <a-modal
    v-model:open="visible"
    title="乘车人"
    centered
    @ok="handleOk"
    ok-text="确认"
    cancel-text="取消"
  >
    <a-form
      ref="formRef"
      :model="passenger"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
    >
      <a-form-item
        label="姓名"
        name="name"
        :rules="[{ required: true, message: '请输入姓名' }]"
      >
        <a-input v-model:value="passenger.name" />
      </a-form-item>
      <a-form-item
        label="身份证"
        name="idCard"
        :rules="[{ required: true, message: '请输入身份证' }]"
      >
        <a-input v-model:value="passenger.idCard" />
      </a-form-item>
      <a-form-item
        label="类型"
        name="type"
        :rules="[{ required: true, message: '请选择类型' }]"
      >
        <a-select v-model:value="passenger.type">
          <a-select-option value="1">成人</a-select-option>
          <a-select-option value="2">儿童</a-select-option>
          <a-select-option value="3">学生</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
import { notification } from "ant-design-vue";
import { nextTick, onMounted, reactive, ref } from "vue";
import { deletePassenger, getPassengergetList, savePassenger } from "@/api";
import type { PassengerQueryResp, PassengerVO } from "@/api/type";

// 乘车人表单初始状态（新增/重置时复用）
const formRef = ref();
const PASSENGER_INITIAL = {
  id: undefined,
  memberId: undefined,
  name: undefined,
  idCard: undefined,
  type: undefined,
  createTime: undefined,
  updateTime: undefined,
};

const visible = ref(false);
const passenger = reactive({ ...PASSENGER_INITIAL });

// 一行重置所有字段
const resetPassenger = () => Object.assign(passenger, PASSENGER_INITIAL);
const passengers = ref<PassengerVO[]>([]);
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
});
const handleTableChange = (paginationVal: { current: any; pageSize: any }) => {
  console.log("看看自带的分页参数都有啥：" + JSON.stringify(pagination));
  pagination.current = paginationVal.current || 1;
  pagination.pageSize = paginationVal.pageSize || 1;
  // console.log("看看自带的分页参数都有啥：" + paginationVal);
  handleQuery({
    page: paginationVal.current,
    size: paginationVal.pageSize,
  });
};
// 乘车人类型映射：存储值 -> 展示文案
const typeMap: Record<string, string> = {
  "1": "成人",
  "2": "儿童",
  "3": "学生",
};

const columns = [
  {
    title: "姓名",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "身份证",
    dataIndex: "idCard",
    key: "idCard",
  },
  {
    title: "类型",
    dataIndex: "type",
    key: "type",
    customRender: ({ value }: { value: string }) => typeMap[value] ?? value,
  },
  {
    title: "操作",
    key: "operation",
  },
];

const showModal = () => {
  resetPassenger();
  visible.value = true;
  // 清除上一次触发校验残留的红字（表单错误状态与字段值相互独立）
  nextTick(() => formRef.value?.clearValidate());
};

const edit = (record: PassengerVO) => {
  resetPassenger();
  const { createTime, updateTime, ...editable } = record;
  Object.assign(passenger, editable);
  visible.value = true;
  // 回填后清除可能残留的校验红字（与新增共用同一表单实例）
  nextTick(() => formRef.value?.clearValidate());
};

const handleDelete = (record: PassengerVO) => {
  if (record.id == null) return;
  deletePassenger(record.id).then((res) => {
    if (res.success) {
      notification.success({ description: "删除成功！", message: "" });
      handleQuery({ page: 1, size: pagination.pageSize });
    } else {
      notification.error({ description: res.message, message: "" });
    }
  });
};

const handleQuery = (param: { page: any; size: any }) => {
  getPassengergetList(param).then((res: PassengerQueryResp) => {
    if (res.success && res.content) {
      passengers.value = res.content.list;
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

const handleOk = () => {
  formRef.value
    .validate()
    .then(() => {
      savePassenger(passenger).then((res) => {
        if (res.success) {
          notification.success({
            description: passenger.id ? "编辑成功！" : "新增成功！",
            message: "",
          });
          visible.value = false;
          resetPassenger();
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
</script>
<style></style>
