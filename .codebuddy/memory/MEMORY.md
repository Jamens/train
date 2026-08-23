# 长期记忆 (MEMORY.md)

## 项目技术栈与约定 (train 项目)
- **Jackson 3.x**：包名 `tools.jackson.*`（非 `com.fasterxml.jackson.*`）。用到注解（如 `@JsonSerialize`/`@JsonDeserialize`）时需用新包路径。反序列化字符串→Long 可用 `tools.jackson.databind.deser.jdk.NumberDeserializers.LongDeserializer`（注意包是 `deser.jdk`，不是 `deser.std`）。
- **登录 token**：有效期 24 小时，`JwtUtil` 中 `DateField.HOUR, 24`；common 与 gateway 两处 `JwtUtil` 的 key(`"junhao"`) 与有效期保持一致。
- **雪花 ID**：前后端以字符串传递（Resp 用 `@JsonSerialize(ToStringSerializer)`），避免 JS 53 位精度丢失。
- **member 模块 Passenger 保存逻辑**：`PassengerService.save` 按 `req.getId()` 是否为空决定 insert / updateByPrimaryKey。

## FreeMarker 代码生成 (generate 模块)
- FreeMarker 依赖：`freemarker 2.3.34`（由 Spring Boot 4.1.0 管理版本，已下载到 .m2）。
- **模板必须放在 `src/main/resources/ftl/`**（不是 `src/main/java/`），否则 Maven 不打包、classpath 加载不到。
- 加载方式：`new Configuration(VERSION_2_3_34)` + `setClassLoaderForTemplateLoading(classLoader, "ftl")` + `getTemplate("test.ftl")`。
- 已有可运行示例 `generate/.../FreemarkerDemo.java`（`main` 方法，传入 `domain` 变量渲染出 `public class Xxx {...}`）。
- IDEA Ultimate 内置 FreeMarker 支持（无需额外插件）；若 `.ftl` 被当纯文本：项目树右键 `Override File Type → FreeMarker Template`，或 File Types 里从 Plain Text 移除 `*.ftl` 并加到 FreeMarker Template。IDE 不识别不影响运行。

## 用户偏好
- 用户使用中文交流；回答需简洁直接。
- 习惯用 HTTP 手动测试文件 `http/member-test.http` 调试接口。
- 用户使用 **IntelliJ IDEA Ultimate** 版本。
