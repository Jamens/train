<script setup lang="ts">
import { reactive } from "vue";

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
};

const onFinishFailed = (errorInfo: any) => {
  console.log("Failed:", errorInfo);
};
const sendCode = () => {
  console.log("sendCode");
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
                <a @click="sendCode">获取验证码</a>
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
