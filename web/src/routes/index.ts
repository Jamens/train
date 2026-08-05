import { createWebHistory, createRouter } from "vue-router";
import Home from "../views/Home.vue";
import Login from "../views/Login.vue";
import Main from "../views/Main.vue";

const routes = [
  { path: "/", component: Home },
  { path: "/Login", component: Login },
  {
    path: "/Main",
    component: Main,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
