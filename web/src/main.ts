import { createApp } from "vue";

import App from "./App.vue";
import { createPinia } from "pinia";
import router from "@/routes/index";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import "./style.css";
const pinia = createPinia();
const app = createApp(App);
app.use(Antd).use(router).use(pinia).mount("#app");
