<template>
  <a-button type="primary" @click="showModal">新增</a-button>
  <a-table
    :dataSource="passengers"
    :columns="columns"
    :pagination="pagination"
    @change="handleTableChange"
  />
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
      <a-form-item label="姓名" name="name" :rules="[{ required: true, message: '请输入姓名' }]">
        <a-input v-model:value="passenger.name" />
      </a-form-item>
      <a-form-item label="身份证" name="idCard" :rules="[{ required: true, message: '请输入身份证' }]">
        <a-input v-model:value="passenger.idCard" />
      </a-form-item>
      <a-form-item label="类型" name="type" :rules="[{ required: true, message: '请选择类型' }]">
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
import { onMounted, reactive, ref } from "vue";
import { getPassengergetList, savePassenger } from "@/api";
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
];

const showModal = () => {
  resetPassenger();
  visible.value = true;
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
            description: "保存成功！",
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
