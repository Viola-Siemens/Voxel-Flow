# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码仓库中工作的指导喵~

## 项目概述

VoxelFlow 是一个为 Minecraft 模组/插件开发团队设计的项目管理平台，实现从需求提交到发布的完整产研流程管理喵~

**核心业务流程**：需求创建 → 多方会签 → 产品分析 → 需求评审 → 技术设计 → 排期 → 开发 → 测试 → 验收 → 发布

**技术栈**：Spring Boot 2.6.7 + MyBatis Plus 3.5.14 + MySQL + Redis + MapStruct + Lombok

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

# 或直接运行 Application 类
java -jar build/libs/voxelflow-0.0.1.jar
```

**默认端口**：8078
**数据库**：MySQL (localhost:3306/voxelflow)

### 测试命令
```bash
# 运行所有测试
./gradlew test

# 运行单个测试类
./gradlew test --tests org.ecnumc.voxelflow.test.RequirementServiceTest

# 运行单个测试方法
./gradlew test --tests org.ecnumc.voxelflow.test.RequirementServiceTest.testApprove

# 运行测试并生成报告
./gradlew test --tests org.ecnumc.voxelflow.test.*

# 查看测试报告
# 报告位于：build/reports/tests/test/index.html
```

### 调试与日志
```bash
# 查看应用日志（运行时）
# 日志会输出到控制台

# 查看 MyBatis SQL 日志
# 已在 application.properties 中配置：
# mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# 检查应用健康状态
curl http://localhost:8078/actuator/health

# 查看应用信息
curl http://localhost:8078/actuator/info
```

### 数据库初始化
```bash
# SQL 脚本位于 sql/create_table/ 目录
# 按顺序执行以下脚本（先创建主表，再创建关系表）：

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
Service (业务逻辑)
    ↓
Repository (数据访问抽象层)
    ↓
Mapper (MyBatis Plus)
    ↓
Database (MySQL)
```

### 目录结构说明
- `/controller` - REST API 控制器，处理 HTTP 请求
- `/service` - 业务逻辑层，实现工作流编排
- `/repository` - 数据访问层，分为 Query/Command/Validation 三类
- `/mapper` - MyBatis Plus Mapper 接口（继承 BaseMapper）
- `/po` - Persistent Object，数据库实体类
- `/bo` - Business Object，业务对象（如 UserBo）
- `/req` - Request DTO，接收前端请求参数
- `/resp` - Response DTO，返回给前端的数据结构
- `/converter` - MapStruct 转换器（PO ↔ Resp）
- `/enumeration` - 枚举类（状态、角色、类型等）
- `/util` - 工具类
- `/config` - Spring 配置类
- `/interceptor` - 拦截器（如 TokenInterceptor）
- `/job` - 定时任务
- `/lock` - 分布式锁相关
- `/annotation` - 自定义注解（JSR-305 空值安全注解）

### 核心领域模型

**Requirement（需求）**：顶层需求实体，编号格式 REQ-{序号}
- 11 个状态：REVIEWING → COUNTERSIGNING → REQUIREMENT_ANALYSIS → REQUIREMENT_REVIEWING → DESIGNING → SCHEDULING → DEVELOPING → TESTING → CHECKING → RELEASED
- 支持多方会签（waitingForAllApprovals 标志）
- 可在任意阶段被 REJECTED 或 CANCELED

**Story（故事）**：从需求派生的实现任务，编号格式 {部门前缀}-{序号}（如 ORG-1, DEV-9527）
- 6 个状态：DRAFT → PROGRESSING → TESTING → FINISHED（或 REJECTED/CANCELED）
- 通过 reqCode 关联到 Requirement

**Issue（缺陷）**：问题和缺陷，编号格式 BUG-{序号}
- 状态流转类似 Story
- 可关联到 Story

**Retrospective（复盘）**：项目复盘，编号格式 RTS-{序号}
- 状态：DRAFT → PROGRESSING → FINISHED（或 REJECTED/CANCELED）
- 用于团队回顾和总结项目经验教训

**Commit（提交）**：代码提交记录，通过 Webhook 从 GitHub 同步
- 关联到 Story 或 Issue

**Group（团队）**：用户组/团队管理
- 用户可以加入多个团队

**User（用户）**：平台用户
- 12 种角色：BUSINESS, PRODUCT, SECURITY, ARCHITECTURE, DEVELOPMENT, TEST, OPERATION, ART, MODEL, BUILDING, DIAGNOSIS, SUPER_ADMIN
- SUPER_ADMIN 拥有所有权限

**关系实体**：UserRequirementRelation, UserStoryRelation, UserIssueRelation, UserRetrospectiveRelation
- 关系类型：HANDLING（处理中）、APPROVED（已批准）、REJECTED（已拒绝）、WITHDRAWN（已撤回）、IGNORED（已忽略）

### 关键设计模式

**1. Repository 分离模式**
- QueryRepository：只读查询操作
- CommandRepository：写入操作（增删改）
- ValidationRepository：验证逻辑

**2. 可审批接口（Approvable）**
```java
public interface Approvable {
	ClientErrorCode approve(String code, List<String> nextOperators, String description, String uid);
	ClientErrorCode reject(String code, List<String> nextOperators, String description, String uid);
}
```
由 RequirementService、StoryService、IssueService、RetrospectiveService 实现喵~

**3. 可分配接口（Assignable）**
```java
public interface Assignable {
	ClientErrorCode assign(String code, String assignee, String uid);
	ClientErrorCode unassign(String code, String assignee, String uid);
}
```

**4. 可操作状态接口（IOperableStatus）**
```java
public interface IOperableStatus {
	Set<UserRole> getOperableRoles();
	static boolean hasPermissionToModify(IOperableStatus status, List<UserRole> userRoles);
}
```
所有状态枚举（RequirementStatus、StoryStatus 等）都实现此接口，定义每个状态下哪些角色可以操作喵~

**5. 通用 Repository 接口**
- `PendingRelationQueryable<R, S>`：查询待处理的用户关系
- `OperatingRelationAssignable<S>`：管理用户关系分配

**6. 转换器模式**
使用 MapStruct 实现 PO 到 Resp 的自动转换，解耦内部实体和 API 响应喵~
MapStruct 在编译期生成实现代码，位于 `build/generated/sources/annotationProcessor/java/main/` 目录喵~

**7. 状态枚举模式**
每个状态枚举都实现了状态转换方法：
- `approved()`：返回批准后的下一状态
- `rejected()`：返回拒绝后的状态
- `canceled()`：返回取消后的状态
- `waitingForAllApprovals()`：是否需要所有人批准（默认 false）

### 权限控制机制

**基于角色和状态的权限控制**：
- 每个状态枚举定义 `getOperableRoles()` 方法，返回可操作该状态的角色集合
- TokenInterceptor 拦截请求，验证 token 并提取 uid
- Service 层通过 `IOperableStatus.hasPermissionToModify()` 检查用户角色是否有权限
- SUPER_ADMIN 绕过所有角色检查

**Token 验证**：
- 请求头：`p_t`（token）、`p_u`（uid）
- 排除路径：/user/sign-up, /user/log-in, /webhook, /error

### 错误处理

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

### 编号生成机制

使用 Counter 实体管理自增编号：
- REQ-{序号}：需求编号
- ARCH-{序号}、DEV-{序号}、TEST-{序号} 等：Story 编号（按角色前缀）
- BUG-{序号}：Issue 编号
- RTS-{序号}：Retrospective 编号

CounterRepository 提供原子性的编号生成喵~

## 开发注意事项

### 代码风格要求
- **缩进**：使用 Tab，不使用空格
- **大括号**：K&R 风格（起始大括号不换行）
- **行长度**：每行不超过 150 字符
- **方法长度**：每个方法不超过 300 行
- **命名**：类名 PascalCase，方法/变量 camelCase，常量 UPPER_SNAKE_CASE
- **注释**：所有 public 类和方法必须有 Javadoc，方法体内适当添加注释
- **空值安全**：对可能为空的字段/参数/返回值使用 `@Nullable` 注解

### 添加新功能的典型步骤

**1. 创建数据库表**（如果需要）
- 在 `sql/create_table/` 目录添加建表脚本
- 在 `sql/alter/` 目录添加表结构变更脚本

**2. 创建 PO 实体类**
```java
@Data
@TableName("table_name")
public class EntityPo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// 其他字段...
	private Date createdAt;
	private Date updatedAt;
}
```

**3. 创建 Mapper 接口**
```java
@Mapper
public interface EntityMapper extends BaseMapper<EntityPo> {
}
```

**4. 创建 Repository**
- EntityQueryRepository：查询操作
- EntityCommandRepository：写入操作
- EntityValidationRepository：验证逻辑（如果需要）

**5. 创建 Req/Resp DTO**
- EntityCreateReq、EntityUpdateReq：接收前端参数
- EntityResp：返回给前端的数据

**6. 创建 Converter**
```java
@Mapper(componentModel = "spring")
public interface EntityConverter {
	EntityResp toResp(EntityPo po);
	List<EntityResp> toRespList(List<EntityPo> poList);
}
```

**7. 创建 Service**
```java
@Service
public class EntityService {
	// 注入 Repository 和 Converter
	// 实现业务逻辑
}
```

**8. 创建 Controller**
```java
@RestController
@RequestMapping("/entity")
public class EntityController {
	// 注入 Service
	// 实现 REST API
}
```

**9. 编写单元测试**
在 `src/test/java/org/ecnumc/voxelflow/test/` 目录创建测试类喵~

### 状态流转实现要点

**1. 定义状态枚举**
```java
public enum EntityStatus implements IOperableStatus {
	DRAFT(Set.of(UserRole.DEVELOPMENT)),
	PROGRESSING(Set.of(UserRole.DEVELOPMENT, UserRole.TEST)),
	FINISHED(Set.of());

	private final Set<UserRole> operableRoles;

	@Override
	public Set<UserRole> getOperableRoles() {
		return operableRoles;
	}
}
```

**2. 在 Service 中实现状态转换**
- 检查当前状态是否允许转换
- 检查用户角色是否有权限
- 更新实体状态
- 创建/更新用户关系记录
- 处理 nextOperators（下一步操作人）

**3. 处理多方审批**
- 使用 `waitingForAllApprovals` 标志区分"任一批准"和"全部批准"
- 在 approve/reject 时更新所有相关用户的关系状态
- 当不需要某用户审批时，将其关系状态设为 IGNORED

### MyBatis Plus 使用技巧

**1. 动态查询**
```java
QueryWrapper<EntityPo> wrapper = new QueryWrapper<>();
wrapper.eq("field", value)
	.like("name", keyword)
	.orderByDesc("created_at");
List<EntityPo> list = mapper.selectList(wrapper);
```

**2. 分页查询**
```java
Page<EntityPo> page = new Page<>(offset / limit + 1, limit);
Page<EntityPo> result = mapper.selectPage(page, wrapper);
```

**3. 批量操作**
```java
mapper.insertBatch(list);
mapper.updateBatchById(list);
```

### 常见陷阱

**1. 状态转换权限检查**
在修改实体状态前，必须调用 `IOperableStatus.hasPermissionToModify()` 检查权限喵~

**2. 用户关系管理**
- 创建实体时，为初始操作人创建 HANDLING 关系
- 审批/拒绝时，更新关系状态为 APPROVED/REJECTED
- 分配/取消分配时，创建/删除 HANDLING 关系
- 状态流转时，为 nextOperators 创建新的 HANDLING 关系

**3. 编号生成**
使用 CounterRepository 生成编号，确保原子性和唯一性喵~

**4. 空值处理**
- 使用 `@Nullable` 注解标记可空字段
- 使用 Guava 的 `Preconditions.checkNotNull()` 进行参数校验
- 使用 Optional 处理可能为空的返回值

**5. 事务管理**
Service 层方法使用 `@Transactional` 注解确保数据一致性喵~

**6. 多方会签逻辑**
- 当 `waitingForAllApprovals()` 返回 true 时，必须所有人都批准才能流转到下一状态
- 检查 `getPendingRelationCount()` 是否为 0 来判断是否所有人都已批准
- 使用 `skipRemainingRelations()` 将其他待处理关系设为 IGNORED（仅单人批准模式）

**7. Lombok 与 MapStruct 配置**
- build.gradle 中必须先配置 Lombok annotationProcessor，再配置 MapStruct
- MapStruct 需要使用 Lombok 生成的 getter/setter，因此顺序很重要

## 测试策略

**单元测试位置**：`src/test/java/org/ecnumc/voxelflow/test/`

**测试类命名**：{ClassName}Test（如 RequirementServiceTest）

**测试框架**：Spring Boot Test + JUnit 5

**测试覆盖重点**：
- Service 层业务逻辑（状态流转、权限检查、审批流程）
- Repository 层数据访问
- Controller 层 API 端点
- Webhook 集成

## 提交规范

**Commit Message 格式**：`type(scope): subject`

**Type 类型**：
- feat：新功能
- fix：Bug 修复
- docs：文档更新
- chores：杂项（依赖更新、配置变更等）
- test：测试相关
- style：代码格式调整

**Scope**：需求或故事编号（如 REQ-1, LDY-9527, ARCH-1）

**示例**：
```
feat(REQ-33133): 实现需求多方会签功能
fix(BUG-1024): 修复状态流转权限检查问题
docs(DEV-520): 更新 API 文档
test(REQ-1): 添加需求服务单元测试
```

## 配置说明

**application.properties**：
- 数据库连接：localhost:3306/voxelflow（用户名 root，密码 123456）
- 服务端口：8078
- MyBatis 配置：驼峰命名转换、SQL 日志输出

**applicationContext.xml**：Spring Bean 配置（如果存在）

**注意**：不要将敏感信息（数据库密码、API 密钥等）提交到版本控制系统喵~

## API 端点概览

所有 API 端点（除了 /user/sign-up, /user/log-in, /webhook 和 /error）都需要在请求头中携带 Token 信息喵~

**请求头格式**：
- `p_t`：用户 token
- `p_u`：用户 uid

### 核心端点

**用户管理 (`/user`)**：
- POST `/sign-up` - 用户注册（无需 token）
- POST `/log-in` - 用户登录（无需 token）
- GET `/query` - 查询用户信息
- GET `/list` - 用户列表查询

**需求管理 (`/requirement`)**：
- POST `/create` - 创建需求
- POST `/update` - 更新需求
- GET `/query?code={code}` - 查询需求
- GET `/list?title={title}&status={status}&priority={priority}&pageNum={pageNum}&pageSize={pageSize}` - 需求列表
- POST `/approve` - 批准需求
- POST `/reject` - 拒绝需求
- POST `/assign` - 分配需求
- POST `/unassign` - 取消分配需求

**故事管理 (`/story`)**：
- POST `/create` - 创建故事
- POST `/update` - 更新故事
- GET `/query?code={code}` - 查询故事
- GET `/list` - 故事列表
- POST `/approve` - 批准故事
- POST `/reject` - 拒绝故事
- POST `/assign` - 分配故事
- POST `/unassign` - 取消分配故事

**缺陷管理 (`/issue`)**：
- POST `/create` - 创建缺陷
- POST `/update` - 更新缺陷
- GET `/query?code={code}` - 查询缺陷
- GET `/list` - 缺陷列表
- POST `/approve` - 批准缺陷
- POST `/reject` - 拒绝缺陷
- POST `/assign` - 分配缺陷
- POST `/unassign` - 取消分配缺陷

**复盘管理 (`/retrospective`)**：
- POST `/create` - 创建复盘
- POST `/update` - 更新复盘
- GET `/query?code={code}` - 查询复盘
- GET `/list` - 复盘列表
- POST `/approve` - 批准复盘
- POST `/reject` - 拒绝复盘
- POST `/assign` - 分配复盘
- POST `/unassign` - 取消分配复盘

**Webhook (`/webhook`)**：
- POST `/github` - 接收 GitHub Webhook 回调（无需 token）

**首页 (`/index`)**：
- GET `/pending` - 查询当前用户待处理的任务列表

## 项目特色

**1. 完整的产研流程管理**：从需求提交到发布的全生命周期管理

**2. 灵活的审批机制**：支持单人审批和多人会签两种模式

**3. 基于角色的权限控制**：12 种角色精细化权限管理

**4. 完整的审计追踪**：通过关系实体记录所有用户操作历史

**5. 类型安全的状态机**：枚举定义状态转换和权限，防止非法状态变更

**6. Repository 分离模式**：Query/Command/Validation 职责分离，提高代码可维护性

**7. Webhook 集成**：支持外部系统（如 GitHub）回调通知

**8. 面向 Minecraft 社区**：专为 Minecraft 模组/插件开发团队设计的领域模型喵~
