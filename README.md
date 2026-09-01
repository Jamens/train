# train（12306 仿练手项目）

前后端分离的会员 / 乘车人管理系统。后端 Spring Boot + Spring Cloud Gateway，前端 Vue 3 + TypeScript，
并附带一套基于 FreeMarker 的前后端代码生成器。

---

## 一、技术架构

### 后端

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| JDK | 17 | 运行环境 |
| Spring Boot | 4.1.0 | 基础框架 |
| Spring Cloud | 2025.1.2 | 微服务底座 |
| Spring Cloud Gateway | 同 Cloud 版本 | 网关、路由、CORS、登录拦截 |
| MyBatis | 4.0.1 | ORM |
| MySQL Connector/J | 9.7.0 | 数据库驱动 |
| PageHelper | 4.1.1 | 分页 |
| Hutool | 5.8.47 | Java 工具库 |
| FastJSON2 | 2.0.62 | JSON 处理 |
| JWT | 自定义 `JwtUtil` | 登录令牌（有效期 24 小时） |
| FreeMarker | 2.3.34 | 代码生成模板引擎 |
| dom4j + jaxen | 2.2.0 / 2.0.6 | 解析 `pom.xml`、生成器配置 |
| Lombok | — | 简化实体代码 |
| Maven | 3.x（自带 `mvnw`） | 构建 |

### 前端

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Vue | 3.5.40 | 前端框架（`<script setup>` 语法） |
| TypeScript | 6.0.2 | 类型系统 |
| Vite | 8.2.0 | 构建 / 开发服务器 |
| Ant Design Vue | 4.2.6 | UI 组件库 |
| Pinia | 4.0.2 | 状态管理（登录态） |
| Vue Router | 5.2.0 | 路由 |
| Axios | 1.19.0 | HTTP 客户端（统一封装） |
| Tailwind CSS | 4.3.3 | 样式 |
| @ant-design/icons-vue | 7.0.1 | 图标 |
| pnpm | — | **包管理器（必须用 pnpm，不要用 npm / yarn）** |

### 端口一览

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| `GatewayApplication` | **8000** | 网关，前端统一入口 |
| `MemberApplication` | **8001** | 会员业务，`context-path=/member` |
| 前端 dev server | **5173** | Vite 默认端口 |

前端接口基址（见 `web/.env.dev` / `web/.env.prod`，可按需修改）：

- 开发环境 `http://localhost:8000/member` —— 走网关
- 生产环境 `http://localhost:8001/member` —— 直连 member

---

## 二、目录结构

```
train/
├── common/                 # 公共模块（以 jar 形式被其它模块依赖，本身不单独启动）
│   └── src/main/java/com/junhao/common/
│       ├── aspect/         #   统一日志切面
│       ├── context/        #   LoginMemberContext：从 ThreadLocal 取当前登录会员
│       ├── controller/     #   公共 Controller
│       ├── exception/      #   全局异常处理 + 业务异常枚举
│       ├── interceptor/    #   拦截器
│       ├── req/            #   公共请求对象（PageReq 分页入参）
│       ├── resp/           #   公共响应对象（CommonResp、PageResp）
│       └── util/           #   JwtUtil、SnowUtil（雪花 ID）等
│
├── gateway/                # 网关模块（端口 8000）
│   ├── config/             #   GatewayApplication 启动类所在
│   ├── filter/             #   LoginMemberFilter：JWT 登录校验，
│   │                       #     放行 /admin、/hello、/member/member/login、/member/member/send-code
│   └── util/JwtUtil        #   令牌校验
│
├── member/                 # 会员业务模块（端口 8001）
│   └── src/main/java/com/junhao/member/
│       ├── config/         #   MemberApplication 启动类所在
│       ├── controller/     #   MemberController、PassengerController、TestController
│       ├── service/        #   业务逻辑
│       ├── mapper/         #   MyBatis Mapper 接口
│       ├── domain/         #   实体 + Example（MBG 生成）
│       ├── req/            #   请求对象（XxxSaveReq / XxxQueryReq）
│       ├── resp/           #   响应对象（XxxQueryResp）
│       ├── enums/          #   后端枚举（前端枚举的数据来源）
│       └── aspect/         #   模块内切面
│   └── src/main/resources/
│       ├── mapper/         #   MyBatis XML
│       └── application.properties
│
├── generate/               # 代码生成模块（不是 Web 服务，只运行 main 方法）
│   └── src/main/java/com/junhao/generate/
│       ├── gen/
│       │   ├── ServerGenerate   # 生成后端 + 前端文件
│       │   └── EnumGenerator    # 生成前端枚举（与后端自动同步）
│       ├── util/                # DbUtil（读表结构）、FreemarkerUtil、Field
│       ├── ftl/                 # FreeMarker 模板
│       │   ├── service.ftl      #   后端 Service
│       │   ├── controller.ftl   #   后端 Controller
│       │   ├── saveReq.ftl      #   保存请求对象
│       │   ├── queryReq.ftl     #   查询请求对象
│       │   ├── queryResp.ftl    #   查询响应对象
│       │   └── vue.ftl          #   前端页面
│       └── resources/generator-config-member.xml  # MBG + 生成器配置
│
├── web/                    # 前端项目
│   ├── src/api/            #   接口封装 + 类型定义
│   ├── src/assets/         #   静态资源
│   │   └── js/enums.ts     #     【自动生成】前端枚举，请勿手工修改
│   ├── src/components/     #   Header、Sider 布局组件
│   ├── src/routes/         #   路由（含登录守卫）
│   ├── src/store/          #   Pinia 状态（登录态）
│   ├── src/utils/request.ts#   Axios 封装（自动附加 token）
│   ├── src/views/          #   页面（Login、Home/Welcome、Home/Passenger…）
│   ├── .env.dev / .env.prod#   接口地址配置
│   └── package.json
│
├── sql/                    # 建表脚本
│   └── member.sql          #   member、passenger 两张表
│
├── http/                   # IDEA .http 接口调试文件
└── pom.xml                 # 父 POM（依赖版本统一管理）
```

---

## 三、环境准备

| 依赖 | 要求 |
| --- | --- |
| JDK | 17 及以上 |
| Maven | 可直接使用项目自带的 `mvnw.cmd` |
| Node.js | 建议 20+（需支持 Vite 8） |
| pnpm | **必装**：`npm i -g pnpm` |
| MySQL | 8.0+ |

### 数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE train DEFAULT CHARACTER SET utf8mb4;

-- 2. 执行建表脚本（会建 member、passenger 两张表）
source sql/member.sql;
```

### 数据库连接配置（按需修改）

配置文件：`member/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/train?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=1234560
```

> 密码 `1234560` 为当前仓库中的值。**如果你的本机 MySQL 密码不同，请自行修改这一行**，
> 其余配置无需改动。

代码生成器读取的是另一处配置：`generate/src/main/resources/generator-config-member.xml`
中的 `jdbcConnection`，**改数据库密码时这里也要同步修改**：

```xml
<jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                connectionURL="jdbc:mysql://localhost:3306/train?serverTimezone=Asia/Shanghai"
                userId="root"
                password="1234560">
```

---

## 四、启动步骤

### 1. 启动后端（顺序不能反）

**先启动 member 业务服务，再启动 gateway 网关。**

```powershell
# 方式 A：IDEA 中依次运行 main 方法
#   ① member  模块：com.junhao.member.config.MemberApplication
#   ② gateway 模块：com.junhao.gateway.config.GatewayApplication

# 方式 B：命令行
.\mvnw -o -pl member  spring-boot:run
.\mvnw -o -pl gateway spring-boot:run
```

> ⚠️ **必须先 member（8001）后 gateway（8000）**。网关会把请求转发给 member，
> member 未就绪会导致接口 503。
>
> ⚠️ member 的可执行 jar 带 `exec` classifier，用 jar 启动时注意文件名：
> `java -jar member/target/member-0.0.1-SNAPSHOT-exec.jar`

### 2. 启动前端

```powershell
cd web

pnpm install     # 首次运行需安装依赖
pnpm dev         # 开发模式（--mode dev）
```

其它常用命令：

```powershell
pnpm build       # 类型检查 + 生产构建
pnpm preview     # 预览构建产物
```

### 3. 访问

浏览器打开 <http://localhost:5173>，会自动跳转登录页。

---

## 五、登录说明

系统采用**手机号 + 验证码**方式登录，**没有密码**。

- **验证码固定为 `1234`**（后端硬编码，见 `MemberService.sendLogin` 中的 `if (!"1234".equals(code))`）
- 登录流程：输入手机号 → 点「获取验证码」→ 输入 `1234` → 登录
- 首次登录的手机号会自动注册为会员
- 登录成功后前端保存 JWT token，后续请求由 `request.ts` 自动附加到 `token` 请求头
- token 有效期 **24 小时**，过期后需重新登录

> 若想修改验证码，改 `MemberService.sendLogin` 中的 `1234` 即可。

---

## 六、代码生成器

生成器位于 `generate` 模块，通过运行 `main` 方法触发，**不是 Web 服务**。

运行前请确保**工作目录为项目根目录**（`d:\Project\train`），因为代码中使用的是相对路径。

### 6.1 ServerGenerate —— 生成前后端文件

`generate/src/main/java/com/junhao/generate/gen/ServerGenerate.java`

**作用**：读取数据库表结构 → 生成后端 Service / Controller / Req / Resp + 前端 Vue 页面。

**步骤**：

1. 配置要生成的表 —— 编辑 `generate/src/main/resources/generator-config-member.xml`：

   ```xml
   <table tableName="passenger" domainObjectName="Passenger"/>
   ```

2. 确认 `generate/pom.xml` 中启用了对应配置（生成器会读取它来确定用哪份配置）：

   ```xml
   <configurationFile>src/main/resources/generator-config-member.xml</configurationFile>
   ```

3. 运行 `ServerGenerate` 的 `main` 方法（IDEA 中点击绿色三角即可）

**产出**：

| 文件 | 路径 |
| --- | --- |
| Service | `member/src/main/java/com/junhao/member/service/XxxService.java` |
| Controller | `member/src/main/java/com/junhao/member/controller/XxxController.java` |
| 保存请求对象 | `member/src/main/java/com/junhao/member/req/XxxSaveReq.java` |
| 查询请求对象 | `member/src/main/java/com/junhao/member/req/XxxQueryReq.java` |
| 查询响应对象 | `member/src/main/java/com/junhao/member/resp/XxxQueryResp.java` |
| 前端页面 | `web/src/views/Home/Xxx.vue` |

> 生成时会连数据库读取字段名、类型、注释，因此**表结构要先落库**。
> 控制台会打印每个生成文件的绝对路径，方便定位。

### 6.2 EnumGenerator —— 生成前端枚举（与后端自动同步）

`generate/src/main/java/com/junhao/generate/gen/EnumGenerator.java`

**作用**：反射读取后端 Java 枚举类 → 生成 `web/src/assets/js/enums.ts`。

**自动同步原理**：

```
后端 Java 枚举（member/.../enums/PassengerTypeEnum.java）
        ↓  反射读取 code / desc
EnumGenerator
        ↓
web/src/assets/js/enums.ts（带 export 的 TS 常量）
        ↓  import
前端页面下拉框 / 表格展示
```

后端新增或修改枚举项后，只需重新运行一次生成器，前端即自动同步，**无需手工维护前端枚举**。

**步骤**：

```powershell
# ① 改完后端枚举后，必须先把 member 装到本地仓库
#    （生成器读的是 jar，不是源码，不 install 会读到旧枚举）
.\mvnw -o -pl member install -DskipTests

# ② 运行 EnumGenerator 的 main 方法（IDEA 点击绿色三角）
```

**产出** `web/src/assets/js/enums.ts`：

```ts
export interface EnumItem {
  code: string;
  desc: string;
}
export const PASSENGER_TYPE: Record<string, EnumItem> = {ADULT: {code:"1", desc:"成人"}, ...};
export const PASSENGER_TYPE_ARRAY: EnumItem[] = [{code:"1", desc:"成人"}, ...];
```

**新增枚举类**：在 `EnumGenerator` 的 `ENUM_CLASSES` 列表中登记即可：

```java
private static final List<Class<? extends Enum<?>>> ENUM_CLASSES =
        List.<Class<? extends Enum<?>>>of(PassengerTypeEnum.class);
```

> ⚠️ `enums.ts` 会被生成器覆盖，**请勿手工修改**。要改枚举请改后端 Java 类后重新生成。

### 6.3 让字段使用枚举

在数据库字段注释中声明枚举类名，生成器与前端会自动建立关联：

```sql
`type` char(1) NOT NULL COMMENT '旅客类型|枚举[PassengerTypeEnum]'
```

注释中的 `枚举[XxxEnum]` 会被解析，常量名转换规则：

```
PassengerTypeEnum  →  PASSENGER_TYPE  →  前端 PASSENGER_TYPE_ARRAY
```

### 6.4 注意：`.ftl` 文件的编辑器识别

模板文件位于 `generate/src/main/java/com/junhao/generate/ftl/`，是 **FreeMarker 模板**（`.ftl`）。

> ⚠️ **如果编辑器不识别 `.ftl` 格式，请把它设置为 Markdown 格式**，否则会出现警告、报错或语法高亮异常。

设置方法（IntelliJ IDEA）：

1. 打开 `Settings` → `Editor` → `File Types`，找到 `Markdown`
2. 在 `File name patterns` 中新增 `*.ftl`
3. 若 `.ftl` 原本关联到了 `Plain Text` / 其它类型，先从那边移除

> 说明：这**只影响编辑器的语法高亮与提示，不影响代码运行**。模板在运行时由
> `FreemarkerUtil` 按目录读取（`generate\src\main\java\com\junhao\generate\ftl\`），
> 与 IDE 的文件类型关联无关。
>
> 另外注意：该路径是**相对路径**，因此运行生成器时工作目录必须是项目根目录（见第六节开头）。

---

## 七、开发约定

### 1. 雪花 ID 必须用字符串传递

雪花 ID（19 位）超过 JavaScript 的 53 位安全整数范围，直接返回 Long 会导致前端精度丢失。

- 请求对象（Req）中的 ID 字段统一声明为 `String`
- 响应对象（Resp）中为 `Long`，但需加 `@JsonSerialize(using = ToStringSerializer.class)`

### 2. Jackson 3.x 的包名（易踩坑）

本项目使用 Jackson 3.x，**只有 `core` / `databind` 迁移到了 `tools.jackson.*`，注解包没有迁移**：

| 注解 | 正确 import |
| --- | --- |
| `@JsonFormat` | `com.fasterxml.jackson.annotation.JsonFormat` |
| `@JsonSerialize` | `tools.jackson.databind.annotation.JsonSerialize` |

### 3. 统一响应格式

后端统一返回 `{ success, message, content }`，前端 `request.ts` 已封装解包与错误提示。

### 4. Maven 父子模块约定

- 根 POM `train` 用 `dependencyManagement` 统一管理依赖版本
- **新增子模块时，`<parent>` 必须指向 `com.junhao:train`**，否则继承不到版本管理，
  会报 `dependencies.dependency.version is missing`
- 子模块不要重复声明 `groupId` / `version`（继承自父 POM）

### 5. 日志

统一使用 SLF4J，字段名 `LOG`：

```java
private static final Logger LOG = LoggerFactory.getLogger(Xxx.class);
```

禁止使用 `printStackTrace()`。

### 6. 文件编码

读写文件一律显式指定 `StandardCharsets.UTF_8`，**不要用 `FileWriter` / `FileReader`**
（它们使用平台默认编码，Windows 下是 GBK，会导致中文乱码）。

---

## 八、常见问题

**Q：前端报 `does not provide an export named 'PASSENGER_TYPE_ARRAY'`**
A：`web/src/assets/js/` 下存在无 `export` 的旧 `enums.js`。删除它，并运行 `EnumGenerator` 重新生成 `enums.ts`。
模板中 import 已显式写为 `@/assets/js/enums.ts`，可避免被同名 `.js` 抢占解析。

**Q：生成的前端页面报 `Cannot read properties of undefined (reading 'reduce')`**
A：枚举文件未生成。运行 `EnumGenerator` 产出 `enums.ts` 即可。

**Q：接口返回 401**
A：网关 `LoginMemberFilter` 校验 token 失败。请重新登录，或确认该路径是否在放行白名单中。

**Q：`generate` 模块编译报 `程序包 com.junhao.member.enums 不存在`**
A：先执行 `.\mvnw -o -pl member install -DskipTests` 把 member 装到本地仓库。

**Q：改了后端枚举但前端没变化**
A：漏了 install 步骤。`EnumGenerator` 读的是本地仓库中的 member jar，必须重新 install。

**Q：Maven 报 `'dependencies.dependency.version' is missing`**
A：子模块的 `<parent>` 没有指向 `com.junhao:train`，请改为：

```xml
<parent>
    <groupId>com.junhao</groupId>
    <artifactId>train</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

**Q：前端 `pnpm build` 报 `TS5101: Option 'baseUrl' is deprecated`**
A：`web/tsconfig.app.json` 中的 `baseUrl` 在 TypeScript 6 下已弃用，会导致 `vue-tsc` 直接退出、
跳过类型检查。删除 `baseUrl`，并把 `paths` 改为 `"@/*": ["./src/*"]` 即可。
