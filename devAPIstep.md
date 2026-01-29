# 接口开发记录文档
| 接口方法 | 接口路径 | 完成状态 |
|----------|----------|----------|
| GET | /all/s3/{bucket} | ❌ 未完成 |
| GET | /single/s3/{bucket}/{key} | ❌ 未完成 |
| GET | /all/dynamo/{table} | ❌ 未完成 |
| GET | /single/dynamo/{table}/{key} | ❌ 未完成 |
| GET | /all/postgres/{table} | ✅ 已完成 |
| POST | /process/dump | ✅ 已完成 |
| POST | /copy-content/dynamo/{table} | ❌ 未完成 |
| POST | /copy-content/S3/{table} | ❌ 未完成 |
## 一、项目整体架构
| 层级          | 作用                                                                 | 对应示例代码文件                                  |
|---------------|----------------------------------------------------------------------|-------------------------------------------|
| Controller层  | 接口入口：接收前端请求、返回响应            | PostgresController、ProcessController      |
| Service层     | 业务逻辑：处理核心业务（比如数据校验、调用工具类、整合数据）         | PostgresService、ProcessService、IlpService |
| Util层        | 工具类：封装通用功能（比如数据库操作、数值计算），避免重复代码       | PostgresTableUtil、CostCalculator          |
| Config层      | 配置类：定制框架行为（比如数据库连接、第三方服务配置）               | ————                                      |
| 启动类        | 项目入口：启动Spring Boot应用                                       | Coursework1Application                    |
| 配置文件      | 全局配置：数据库连接、端口、日志等       | application.yml                           |

### 核心依赖
- `Spring Web`：实现HTTP接口（GET/POST）
- `PostgreSQL Driver`：连接PostgreSQL数据库
- `Spring Data JPA/JdbcTemplate`：操作数据库
- `RestTemplate`：发送HTTP请求

# 接口开发逻辑流程概述

## GET /api/v1/acp/all/postgres/{table} 接口逻辑

### 整体流程
`Controller层` → `Service层` → `Util层` → 数据库查询 → 返回结果

### 具体调用链路
1. **[PostgresController.getAllPostgresData()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/controller/PostgresController.java#L15-L18)** 接收URL路径参数 `{table}`
2. 调用 **[PostgresService.getAllDataFromTableResponse()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/PostgresService.java#L12-L24)** 传入表名
3. [PostgresService](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/PostgresService.java#L7-L34) 内部：
   - 首先调用 **[PostgresTableUtil.isValidTableName()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/util/PostgresTableUtil.java#L30-L32)** 校验表名格式
   - 再调用 **[PostgresTableUtil.isTableExists()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/util/PostgresTableUtil.java#L16-L20)** 检查表是否存在
   - 如验证通过，调用 **[PostgresTableUtil.getAllTableData()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/util/PostgresTableUtil.java#L23-L26)** 查询表数据
4. 返回 `ResponseEntity` 响应对象

## POST /api/v1/acp/process/dump 接口逻辑

### 整体流程
`Controller层` → `Service层` → `IlpService` → `CostCalculator` → 返回处理结果

### 具体调用链路
1. **[ProcessController.dumpProcessData()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/controller/ProcessController.java#L13-L16)** 接收JSON请求体
2. 提取请求体中的 `urlPath` 参数，传入 **[ProcessService.dumpProcessData()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/ProcessService.java#L15-L31)**
3. [ProcessService](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/ProcessService.java#L7-L28) 内部：
   - 调用 **[IlpService.fetchDrones()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/IlpService.java#L17-L25)** 从远程URL获取无人机数据
   - 再调用 **[CostCalculator.costPer100Moves()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/util/CostCalculator.java#L12-L41)** 计算100次移动成本
4. 返回包含 `costPer100Moves` 字段的处理后数据

### 核心处理逻辑
- **[IlpService.fetchDrones()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/service/IlpService.java#L17-L25)**: 通过 [RestTemplate](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/config/RestTemplateConfig.java#L10-L10) 发送GET请求获取远程数据
- **[CostCalculator.costPer100Moves()](file:///Users/olivia/IdeaProjects/coursework1/src/main/java/org/example/coursework1/util/CostCalculator.java#L12-L41)**: 遍历数据计算 `costPer100Moves = costInitial + costFinal + (costPerMove * 100)`