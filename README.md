<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
[![MySQL](https://img.shields.io/badge/MySQL-5.7+-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## 🍟 如果您觉得有帮助，请点右上角 "Star" 支持一下谢谢


</div>

## 🌟 项目介绍

Aegis是一个功能完整的企业级RBAC(Role-Based Access Control)权限管理系统，采用前后端分离架构设计。系统提供了完整的用户管理、角色管理、权限控制、部门管理等核心功能，可作为企业应用开发的基础框架。

### ✨ 核心特性

- 🔐 **多样化认证**: 支持密码、邮箱、短信多种登录方式
- 🔑 **JWT无状态认证**: 基于JWT的Token认证机制，支持Token刷新
- 🛡️ **细粒度权限控制**: URL级、按钮级、数据级多层权限控制
- 📊 **数据权限**: 支持按用户、部门、自定义等多种数据权限范围
- 📁 **多存储支持**: 支持本地、MinIO、阿里云OSS、腾讯云COS多种文件存储
- 📝 **操作审计**: 完整的操作日志记录和审计功能
- 🚫 **防重复提交**: 内置防重复提交机制
- 🌍 **IP地理位置**: 基于ip2region的IP地理位置识别
- 📋 **Excel导入导出**: 支持Excel文件的导入导出功能
- 📧 **邮件服务**: 内置邮件发送功能
- 🔍 **接口文档**: 集成Knife4j提供完整的API文档

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Security | - | 安全框架 |
| JWT | 0.13.0 | JWT Token |
| MyBatis-Plus | 3.5.12 | ORM框架 |
| Redis | - | 缓存中间件 |
| MySQL | 5.7+ | 关系型数据库 |
| HikariCP | - | 数据库连接池 |
| MapStruct | 1.6.3 | 对象映射 |
| Knife4j | 4.5.0 | API文档 |
| Hutool | 5.8.39 | 工具类库 |

### 前端技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Vue | 2.6.14 | 前端框架 |
| Element UI | - | UI组件库 |
| Vuex | - | 状态管理 |
| Vue Router | - | 路由管理 |
| Axios | - | HTTP客户端 |

## 🚀 快速开始

### 环境要求

- ☕ JDK 1.8+
- 🗄️ MySQL 5.7+
- 📦 Redis 6.0+
- 🔨 Maven 3.6+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/OOMEcho/aegis.git
cd aegis
```

2. **数据库初始化**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE aegis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构和初始数据
mysql -u root -p aegis < src/main/resources/script/aegis.sql
```

3. **配置修改**

编辑 `src/main/resources/application.yml` 配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/aegis?serverTimezone=Asia/Shanghai&useSSL=false
    username: your_mysql_username
    password: your_mysql_password

  redis:
    host: 127.0.0.1
    port: 6379
    password: your_redis_password

  mail:
    host: smtp.163.com
    username: your_email@163.com
    password: your_email_password
```

4. **启动应用**
```bash
# 编译项目
./mvnw clean compile

# 启动应用
./mvnw spring-boot:run

# 或指定环境
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

5. **访问应用**
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/doc.html (用户名/密码: aegis/aegis)

### Docker部署

```bash
# 构建镜像
docker build -t aegis .

# 运行容器
docker run -p 8080:8080 -d aegis
```

## 📦 功能模块

### 🎯 核心模块

| 模块 | 功能描述 |
|------|----------|
| **用户管理** | 用户增删改查、状态管理、密码重置 |
| **角色管理** | 角色配置、权限分配、数据权限设置 |
| **菜单管理** | 菜单配置、权限控制、路由管理 |
| **部门管理** | 组织架构、层级管理、部门权限 |
| **字典管理** | 系统字典、配置管理 |
| **日志管理** | 操作日志、登录日志、系统监控 |
| **文件管理** | 文件上传、存储管理、访问控制 |
| **通知公告** | 系统通知、公告发布、消息推送 |
| **IP白名单** | IP访问控制、安全防护 |

### 🔒 安全特性

#### 认证机制
- **密码认证**: 传统用户名密码登录
- **邮箱认证**: 邮箱验证码登录
- **短信认证**: 手机短信验证码登录
- **RSA加密**: 密码传输加密保护

#### 权限控制
- **URL级权限**: 接口访问权限控制
- **菜单级权限**: 页面访问权限控制
- **按钮级权限**: 操作按钮权限控制
- **数据级权限**: 行级数据访问控制

#### 数据权限类型
- **全部数据权限**: 无限制访问
- **自定义数据权限**: 按指定部门范围
- **部门数据权限**: 按所属部门
- **部门及以下数据权限**: 按部门层级
- **仅本人数据权限**: 仅访问本人数据

### 📁 文件存储

支持多种存储后端，通过配置切换：

```yaml
file:
  upload:
    platform: LOCAL  # LOCAL, MINIO, ALIYUN_OSS, TENCENT_COS
```

- **本地存储**: 适用于开发和小规模部署
- **MinIO**: 兼容S3的开源对象存储
- **阿里云OSS**: 阿里云对象存储服务
- **腾讯云COS**: 腾讯云对象存储服务

## 🏛️ 系统架构

### 包结构
```
com.aegis/
├── AegisApplication.java          # 应用启动类
├── common/                        # 公共组件
│   ├── constant/                  # 系统常量
│   ├── datascope/                # 数据权限
│   ├── domain/                   # 通用DTO/VO
│   ├── exception/                # 全局异常处理
│   ├── file/                     # 文件存储抽象
│   ├── log/                      # 操作日志
│   ├── result/                   # 统一响应封装
│   └── validator/                # 自定义验证器
├── config/                       # 配置类
│   ├── security/                 # Spring Security配置
│   ├── redis/                    # Redis配置
│   ├── mp/                       # MyBatis-Plus配置
│   └── jackson/                  # JSON序列化配置
├── modules/                      # 业务模块
│   ├── user/                     # 用户管理
│   ├── role/                     # 角色管理
│   ├── menu/                     # 菜单权限管理
│   ├── dept/                     # 部门管理
│   ├── dict/                     # 数据字典
│   ├── log/                      # 系统日志
│   ├── file/                     # 文件管理
│   ├── notice/                   # 通知公告
│   └── whitelist/                # IP白名单
└── utils/                        # 工具类
```

### 数据库设计

系统采用规范化的数据库设计，所有表都包含以下审计字段：

- `created_by`, `created_time` - 创建审计
- `updated_by`, `updated_time` - 更新审计
- `deleted` - 逻辑删除标记 (0=正常, 1=删除)
- `version` - 乐观锁版本号

核心表结构：

- `t_user` - 用户信息表
- `t_role` - 角色信息表
- `t_menu` - 菜单权限表
- `t_dept` - 部门信息表
- `t_user_role` - 用户角色关联表
- `t_role_menu` - 角色菜单关联表
- `t_role_dept` - 角色部门关联表

## 🔧 开发指南

### API接口规范

系统采用RESTful API设计规范：

- **GET** - 查询操作
- **POST** - 创建操作
- **PUT** - 更新操作
- **DELETE** - 删除操作

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T12:00:00"
}
```

### 权限注解使用

```java
// 数据权限控制
@DataPermission(deptField = "dept_id", userField = "create_by", tableAlias="alias")
public List<User> selectUserList(UserQuery query);

// 操作日志记录
@OperationLog(moduleTitle = "用户管理", businessType = BusinessType.INSERT)
public String createUser(UserDTO dto);

// 防重复提交
@PreventDuplicateSubmit
public String submitForm(FormDTO dto);
```

### 自定义验证器

```java
@EnumString(value = {"0", "1"}, message = "状态只允许为0或1")
private String status;
```

## 📊 API文档

系统集成了Knife4j，提供完整的API文档和在线测试功能。

- **文档地址**: http://localhost:8080/doc.html
- **访问认证**: 用户名和密码均为 `aegis`

API文档包含：
- 完整的接口列表
- 请求参数说明
- 响应数据结构
- 在线测试功能
- 示例代码生成

## 🧪 测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=UserServiceTest

# 生成测试报告
./mvnw test jacoco:report
```

## 📚 部署

### 生产环境部署

1. **构建应用**
```bash
./mvnw clean package -Dmaven.test.skip=true
```

2. **配置生产环境**
```yaml
spring:
  profiles:
    active: prod

knife4j:
  enable: false  # 生产环境关闭API文档
  production: true

logging:
  level:
    com.aegis: INFO  # 生产环境日志级别
```

3. **启动应用**
```bash
java -jar target/aegis-1.0.0.jar
```

### Docker Compose部署

```yaml
version: '3.8'
services:
  aegis:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/aegis
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: aegis
      MYSQL_DATABASE: aegis
    ports:
      - "3306:3306"

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"
```

## 🤝 贡献指南

我们欢迎社区贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 提交 Pull Request

### 开发规范

- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 编写必要的单元测试
- 更新相关文档

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证开源。

## 👥 团队

- **南常** - 项目负责人 - [228389787@qq.com](mailto:228389787@qq.com)

## 🙏 致谢

感谢以下开源项目：

- 🔥 [JetBrains](https://www.jetbrains.com/)- 世界最好的IDE
- 🔥 [Spring Boot](https://spring.io/projects/spring-boot) - 基础框架
- [Spring Security](https://spring.io/projects/spring-security)- 安全框架
- [MyBatis-Plus](https://baomidou.com/) - ORM增强工具
- [MySQL](https://www.mysql.com/) - 关系型数据库
- [Redis](https://redis.io/) - 高性能缓存数据库
- [Lombok](https://projectlombok.org/)- Java简化工具
- [MapStruct](https://mapstruct.org/) - Java对象映射工具
- [ip2region](https://github.com/lionsoul2014/ip2region) - IP地理位置库
- [Knife4j](https://doc.xiaominfo.com/) - API文档工具
- [Hutool](https://hutool.cn/) - Java工具类库

## ❓ 常见问题

<details>
<summary>如何修改默认的管理员账号？</summary>

在数据库中修改 `t_user` 表的默认管理员记录，或通过管理界面创建新的管理员账号。
</details>

<details>
<summary>如何配置邮件服务？</summary>

修改 `application.yml` 中的邮件配置，确保SMTP服务器地址、端口、用户名和密码正确。
</details>

<details>
<summary>如何切换文件存储方式？</summary>

修改 `application.yml` 中的 `file.upload.platform` 配置，并配置对应的存储服务参数。
</details>

<details>
<summary>如何自定义数据权限？</summary>

在Service方法上使用 `@DataPermission` 注解，并指定相应的别名参数。
</details>

## 📞 支持

如果您在使用过程中遇到问题，可以通过以下方式寻求帮助：

- 📧 邮件: [228389787@qq.com](mailto:228389787@qq.com)
- 🐛 Issue: [提交Issue](https://github.com/OOMEcho/aegis/issues)
- 📖 文档: [项目Wiki](https://github.com/OOMEcho/aegis/wiki)

---

<div align="center">

**如果这个项目对您有帮助，请给它一个 ⭐ Star！**

Made with ❤️ by [南常](https://github.com/OOMEcho)

</div>
