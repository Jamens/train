# 长期记忆 (MEMORY.md)

## 项目技术栈与约定 (train 项目)
- **Jackson 3.x**：包名 `tools.jackson.*`（非 `com.fasterxml.jackson.*`）。用到注解（如 `@JsonSerialize`/`@JsonDeserialize`）时需用新包路径。反序列化字符串→Long 可用 `tools.jackson.databind.deser.jdk.NumberDeserializers.LongDeserializer`（注意包是 `deser.jdk`，不是 `deser.std`）。
- **登录 token**：有效期 24 小时，`JwtUtil` 中 `DateField.HOUR, 24`；common 与 gateway 两处 `JwtUtil` 的 key(`"junhao"`) 与有效期保持一致。
- **雪花 ID**：前后端以字符串传递（Resp 用 `@JsonSerialize(ToStringSerializer)`），避免 JS 53 位精度丢失。
- **member 模块 Passenger 保存逻辑**：`PassengerService.save` 按 `req.getId()` 是否为空决定 insert / updateByPrimaryKey。

## 用户偏好
- 用户使用中文交流；回答需简洁直接。
- 习惯用 HTTP 手动测试文件 `http/member-test.http` 调试接口。
