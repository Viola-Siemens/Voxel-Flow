# VoxelFlow 项目上下文

## 文档基线

本文档最后同步于 commit `c696208332e626ee834b093f4f788f49f498fd67`。在此之后的代码变更可通过以下命令快速了解，无需重新全量阅读代码库喵~

```bash
# 查看基线之后的所有变更
git log c696208..HEAD --oneline

# 查看具体文件改动
git diff c696208..HEAD
```

## 项目概述

VoxelFlow 是一个为 Minecraft 模组/插件开发团队设计的**项目管理平台**，实现从需求提交到发布的完整产研流程管理喵~

**核心业务流程**：需求创建 → 多方会签 → 产品分析 → 需求评审 → 技术设计 → 排期 → 开发 → 测试 → 验收 → 发布

**技术栈**：
- **后端框架**：Spring Boot 2.6.7
- **ORM 框架**：MyBatis Plus 3.5.14 + JPA
- **数据库**：MySQL + Redis
- **对象映射**：MapStruct 1.6.3
- **代码简化**：Lombok 1.18.42
- **构建工具**：Gradle
- **Java 版本**：Java 8（sourceCompatibility = '8'）

## 项目结构

```
VoxelFlow/
├── src/main/java/org/ecnumc/voxelflow/
│   ├── controller/          # REST API 控制器
│   ├── service/             # 业务逻辑层
│   ├── repository/          # 数据访问层（Query/Command/Validation 分离）
│   ├── mapper/              # MyBatis Plus Mapper 接口
│   ├── po/                  # Persistent Object（数据库实体）
│   ├── bo/                  # Business Object（业务对象）
│   ├── req/                 # Request DTO（请求参数）
│   ├── resp/                # Response DTO（响应数据）
│   ├── converter/           # MapStruct 转换器（PO ↔ Resp）
│   ├── enumeration/         # 枚举类（状态、角色、类型等）
│   ├── util/                # 工具类
│   ├── config/              # Spring 配置类
│   ├── interceptor/         # 拦截器（TokenInterceptor）
│   ├── job/                 # 定时任务
│   ├── lock/                # 分布式锁（Redis）
│   └── annotation/          # 自定义注解（JSR-305 空值安全）
├── src/test/java/           # 单元测试
├── sql/                     # 数据库建表脚本
└── src/main/resources/      # 配置文件
```

## 核心领域模型

### Requirement（需求）
- 编号格式：`REQ-{序号}`
- 11 个状态流转：REVIEWING → COUNTERSIGNING → REQUIREMENT_ANALYSIS → REQUIREMENT_REVIEWING → DESIGNING → SCHEDULING → DEVELOPING → TESTING → CHECKING → RELEASED
- 支持多方会签（waitingForAllApprovals 标志）

### Story（故事）
- 编号格式：`{部门前缀}-{序号}`（如 ORG-1, DEV-9527）
- 状态：DRAFT → PROGRESSING → TESTING → FINISHED（或 REJECTED/CANCELED）
- 通过 reqCode 关联到 Requirement

### Issue（缺陷）
- 编号格式：`BUG-{序号}`
- 状态流转类似 Story，可关联到 Story

### Retrospective（复盘）
- 编号格式：`RTS-{序号}`
- 状态：DRAFT → PROGRESSING → FINISHED（或 REJECTED/CANCELED）

### Commit（提交）
- 代码提交记录，通过 Webhook 从 GitHub 同步
- 关联到 Story 或 Issue

### User（用户）
- 12 种角色：BUSINESS, PRODUCT, SECURITY, ARCHITECTURE, DEVELOPMENT, TEST, OPERATION, ART, MODEL, BUILDING, DIAGNOSIS, SUPER_ADMIN
- SUPER_ADMIN 拥有所有权限

### Group（团队）
- 用户组/团队管理
- 用户只能加入一个团队，但可以退出团队后加入新的团队
- 加入团队的历史会被保存（逻辑删除）

## 构建与运行命令

### 构建项目
```bash
# 使用 Gradle Wrapper 构建
./gradlew build

# 清理并重新构建
./gradlew clean build

# 跳过测试构建
./gradlew build -x test
```

### 运行应用
```bash
# 运行 Spring Boot 应用
./gradlew bootRun

# 或构建 jar 后运行
java -jar build/libs/voxelflow-0.0.1.jar
```

**默认端口**：8078  
**数据库**：MySQL (localhost:3306/voxelflow，用户名 root，密码 123456)

### 测试命令
```bash
# 运行所有测试
./gradlew test

# 运行单个测试类
./gradlew test --tests org.ecnumc.voxelflow.test.RequirementServiceTest

# 运行单个测试方法
./gradlew test --tests org.ecnumc.voxelflow.test.RequirementServiceTest.testApprove

# 查看测试报告
# 报告位于：build/reports/tests/test/index.html
```

### 数据库初始化
```bash
# SQL 脚本位于 sql/create_table/ 目录
# 按顺序执行（先创建主表，再创建关系表）：

# 主表
mysql -u root -p voxelflow < sql/create_table/user.sql
mysql -u root -p voxelflow < sql/create_table/counter.sql
mysql -u root -p voxelflow < sql/create_table/requirement.sql
mysql -u root -p voxelflow < sql/create_table/story.sql
mysql -u root -p voxelflow < sql/create_table/issue.sql
mysql -u root -p voxelflow < sql/create_table/retrospective.sql
mysql -u root -p voxelflow < sql/create_table/commit.sql
mysql -u root -p voxelflow < sql/create_table/group.sql

# 关系表
mysql -u root -p voxelflow < sql/create_table/user_role_rel.sql
mysql -u root -p voxelflow < sql/create_table/user_group_rel.sql
mysql -u root -p voxelflow < sql/create_table/user_requirement_rel.sql
mysql -u root -p voxelflow < sql/create_table/user_story_rel.sql
mysql -u root -p voxelflow < sql/create_table/user_issue_rel.sql
mysql -u root -p voxelflow < sql/create_table/user_retrospective_rel.sql
```

## 架构设计

### 分层架构
```
Controller (HTTP 端点)
    ↓
Service (业务逻辑层)
    ↓
Repository (数据访问抽象层)
    ↓
Mapper (MyBatis Plus)
    ↓
Database (MySQL)
```

### 关键设计模式

**1. Repository 分离模式**
- `QueryRepository`：只读查询操作
- `CommandRepository`：写入操作（增删改）
- `ValidationRepository`：验证逻辑

**2. 可审批接口（Approvable）**
由 RequirementService、StoryService、IssueService、RetrospectiveService 实现喵~

**3. 可分配接口（Assignable）**
管理实体的分配和取消分配喵~

**4. 可操作状态接口（IOperableStatus）**
所有状态枚举都实现此接口，定义每个状态下哪些角色可以操作喵~

**5. 转换器模式**
使用 MapStruct 实现 PO 到 Resp 的自动转换，解耦内部实体和 API 响应喵~

## 权限控制机制

**基于角色和状态的权限控制**：
- 每个状态枚举定义 `getOperableRoles()` 方法，返回可操作该状态的角色集合
- TokenInterceptor 拦截请求，验证 token 并提取 uid
- Service 层通过 `IOperableStatus.hasPermissionToModify()` 检查用户角色是否有权限
- SUPER_ADMIN 绕过所有角色检查

**Token 验证**：
- 请求头：`p_t`（token）、`p_u`（uid）
- 排除路径：/user/sign-up, /user/log-in, /webhook, /error

## 错误处理

**ClientErrorCode 枚举**（1400-1492 范围）：
- 1400: 用户名已存在
- 1410: 登录失败
- 1420-1422: Requirement 相关错误
- 1430-1432: Story 相关错误
- 1440-1442: Issue 相关错误
- 1450-1452: Retrospective 相关错误
- 1490-1492: 通用错误（无效 token、权限不足、无效用户）

**响应格式**：
```java
BaseResp.success(data)           // 200 OK
BaseResp.error(ClientErrorCode)  // 错误响应
PagedResp<T>                     // 分页响应
```

## 代码规范

**缩进与格式**：
- 使用 **Tab** 进行缩进，不要使用空格
- 大括号使用 **K&R 风格**（起始大括号不换行）
- 每行代码不超过 150 字符
- 每个方法不超过 300 行

**命名约定**：
- 类名：PascalCase（如 `ModelServiceQueryRepository`）
- 方法名：camelCase（如 `queryModelServiceByCode`）
- 变量名：camelCase（如 `modelServiceCode`）
- 常量名：UPPER_SNAKE_CASE（如 `MAX_TREE_LEVEL`）

**注释规范**：
- 所有 public 类和方法必须编写 Javadoc
- 方法体内适当添加注释说明复杂逻辑
- 对可能为空的字段/参数/返回值使用 `@Nullable` 注解

**异常处理**：
- 优先使用特定异常而非通用的 Exception
- 资源管理优先使用 try-with-resources
- 记录异常时包含上下文信息

## 开发约定

### 添加新功能的典型步骤

1. **创建数据库表**（如需要）：在 `sql/create_table/` 或 `sql/alter/` 目录添加脚本
2. **创建 PO 实体类**：使用 `@Data` 和 `@TableName` 注解
3. **创建 Mapper 接口**：继承 `BaseMapper<EntityPo>`
4. **创建 Repository**：分离 Query/Command/Validation
5. **创建 Req/Resp DTO**：接收参数和返回数据
6. **创建 Converter**：使用 MapStruct 实现 PO ↔ Resp 转换
7. **创建 Service**：实现业务逻辑
8. **创建 Controller**：实现 REST API
9. **编写单元测试**：放在 `src/test/java/org/ecnumc/voxelflow/test/`

### 状态流转实现要点

1. 定义状态枚举，实现 `IOperableStatus` 接口
2. 在 Service 中检查当前状态是否允许转换
3. 检查用户角色是否有权限（`IOperableStatus.hasPermissionToModify()`）
4. 更新实体状态和用户关系记录
5. 处理 nextOperators（下一步操作人）

### 常见陷阱

1. **状态转换权限检查**：修改实体状态前必须检查权限喵~
2. **用户关系管理**：创建/审批/分配实体时需要正确管理关系记录
3. **编号生成**：使用 CounterRepository 确保原子性和唯一性
4. **空值处理**：使用 `@Nullable` 和 Guava 的 `Preconditions.checkNotNull()`
5. **事务管理**：Service 层方法使用 `@Transactional` 确保数据一致性
6. **多方会签逻辑**：检查 `waitingForAllApprovals()` 和 `getPendingRelationCount()`
7. **Lombok 与 MapStruct 配置**：build.gradle 中必须先配置 Lombok annotationProcessor，再配置 MapStruct

## 测试策略

**测试位置**：`src/test/java/org/ecnumc/voxelflow/test/`

**测试类命名**：`{ClassName}Test`（如 `RequirementServiceTest`）

**测试框架**：Spring Boot Test + JUnit 5

**测试覆盖重点**：
- Service 层业务逻辑（状态流转、权限检查、审批流程）
- Repository 层数据访问
- Controller 层 API 端点
- Webhook 集成

## 提交规范

**Commit Message 格式**：`type(scope): subject`

**Type 类型**：feat, fix, docs, chores, test, style, revert, ci

**Scope**：需求或故事编号（如 REQ-1, LDY-9527, ARCH-1）

**示例**：
```
feat(REQ-33133): 实现需求多方会签功能
fix(BUG-1024): 修复状态流转权限检查问题
docs(DEV-520): 更新 API 文档
test(REQ-1): 添加需求服务单元测试
```

## API 端点概览

所有 API 端点（除了 /user/sign-up, /user/log-in, /webhook 和 /error）都需要在请求头中携带 Token 信息喵~

**请求头格式**：
- `p_t`：用户 token
- `p_u`：用户 uid

### 核心端点

**用户管理 (`/user`)**：
- POST `/sign-up` - 用户注册（无需 token）
- POST `/log-in` - 用户登录（无需 token）
- GET `/get` - 查询用户信息
- GET `/list` - 用户列表查询
- POST `/ban` - 封禁用户
- POST `/delete` - 注销用户
- GET `/roles` - 查询用户所有角色
- POST `/grant-role` - 授予用户角色（仅超级管理员可用）
- POST `/revoke-role` - 移除用户角色（仅超级管理员可用）

**需求管理 (`/requirement`)**：
- POST `/create` - 创建需求
- POST `/update` - 更新需求
- GET `/query?code={code}` - 查询需求
- GET `/list` - 需求列表（支持 title/status/priority 筛选和分页）
- POST `/approve` - 批准需求
- POST `/reject` - 拒绝需求
- POST `/assign` - 分配需求
- POST `/unassign` - 取消分配需求

**故事管理 (`/story`)**、**缺陷管理 (`/issue`)**、**复盘管理 (`/retrospective`)** 端点类似喵~

**Webhook (`/webhook`)**：
- POST `/github` - 接收 GitHub Webhook 回调（无需 token）

**首页 (`/index`)**：
- GET `/pending` - 查询当前用户待处理的任务列表

## 项目特色

1. **完整的产研流程管理**：从需求提交到发布的全生命周期管理
2. **灵活的审批机制**：支持单人审批和多人会签两种模式
3. **基于角色的权限控制**：12 种角色精细化权限管理
4. **完整的审计追踪**：通过关系实体记录所有用户操作历史
5. **类型安全的状态机**：枚举定义状态转换和权限，防止非法状态变更
6. **Repository 分离模式**：Query/Command/Validation 职责分离
7. **Webhook 集成**：支持外部系统（如 GitHub）回调通知
8. **面向 Minecraft 社区**：专为 Minecraft 模组/插件开发团队设计的领域模型喵~
