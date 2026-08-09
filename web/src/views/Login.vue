<script setup lang="ts">
import { reactive } from "vue";
import { notification } from "ant-design-vue";
import { login, sendCode } from "@/api";
import { useAuthStore } from "@/store";
import router from "@/routes";

const auth = useAuthStore();

interface loginForm {
  mobile: string;
  code: string;
  remember: boolean;
}

const loginForm = reactive<loginForm>({
  mobile: "",
  code: "",
  remember: true,
});
const onFinish = (values: any) => {
  console.log("Success:", values);
  login({
    mobile: loginForm.mobile,
    code: loginForm.code,
  }).then((res) => {
    const { success, message } = res;
    if (success) {
      const content = res.content;
      if (content) {
        // id 转为字符串，避免大整数精度丢失
        auth.login({ ...content, id: String(content.id) });
      }
      notification.success({
        message: "登录成功",
      });
      router.push({ path: "/" });
    } else {
      notification.error({
        message: message,
      });
    }
  });
};

const onFinishFailed = (errorInfo: any) => {
  console.log("Failed:", errorInfo);
};

// 中国大陆手机号校验：1 开头的 11 位数字
const MOBILE_REGEX = /^1\d{10}$/;

const handleSendCode = () => {
  if (!loginForm.mobile) {
    notification.warning({ message: "请先输入手机号" });
    return;
  }
  if (!MOBILE_REGEX.test(loginForm.mobile)) {
    notification.warning({ message: "手机号格式不正确" });
    return;
  }
  sendCode({ mobile: loginForm.mobile }).then((res) => {
    if (res.success) {
      notification.success({ message: "发送成功" });
    }
  });
};
</script>

<template>
  <div>
    <a-row class="login-row">
      <a-col :span="8" :offset="8" class="login-main">
        <h1 style="text-align: center">Login系统</h1>
        <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
        >
          <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: '请输入手机号码' }]"
          >
            <a-input v-model:value="loginForm.mobile" />
          </a-form-item>

          <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: '请输入验证码' }]"
          >
            <a-input v-model:value="loginForm.code">
              <template #addonAfter>
                <a @click="handleSendCode">获取验证码</a>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button style="width: 100%" type="primary" html-type="submit"
              >Login</a-button
            >
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </div>
</template>
<style scoped>
.login-main h1 {
  font-size: 25px;
  font-weight: bold;
}
.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}
</style>
