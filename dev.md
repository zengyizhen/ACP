# ACP Assignment 1 开发文档模板
**项目名称**：ACP Assignment 1 - Java REST Service with Cloud Integration

## 一、 项目概述
### 1.1 项目背景
本项目为 ACP 课程的第一次编程作业，需开发一个 Java REST 服务。该服务需集成 S3、DynamoDB、Postgres 三种存储服务，同时对接第三方 ILP-REST-Service。服务需通过 Docker 容器化部署，并按照指定规范提交。

### 1.2 项目目标
1.  实现符合要求的 REST 接口，支持 GET/POST 方法，正确处理参数和返回状态码。
2.  完成与 localstack 模拟的 S3、DynamoDB 及独立容器化 Postgres 的交互。
3.  对接第三方 ILP 服务，实现数据读取、处理与存储功能。
4.  将服务打包为指定 Docker 镜像，并按要求整理提交文件。

### 1.3 核心技术栈
- 后端框架：Spring Boot
- 构建工具：Maven
- 容器化工具：Docker
- 云服务模拟：localstack
- 数据库：Postgres、DynamoDB
- 测试工具：Postman

## 二、 环境配置
### 2.1 开发环境要求
| 工具/组件 | 版本/规格要求 |
| --- | -- |
| JDK | 17 |
| IDE | IntelliJ IDEA 社区版 |
| Docker | Docker Desktop（支持多架构构建） |
| localstack |  |
| AWS SDK | V2 版本 |
| 测试工具 | Postman|

### 2.2 环境变量配置
需在 Spring Boot 配置文件中配置以下环境变量占位符，运行时由外部传入：

| 环境变量名 | 用途 | 示例值 |
| --- | --- | --- |
| ACP_POSTGRES | Postgres 连接字符串 | jdbc:postgresql://localhost:5432/acp?currentSchema=[SID] |
| ACP_S3 | S3 服务端点 | http://localhost:4566 |
| ACP_DYNAMODB | DynamoDB 服务端点 | http://localhost:4566 |
| ACP_URL_ENDPOINT | ILP 服务基础地址 | https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net |

### 2.3 基础服务初始化
1.  **localstack 启动命令**
    ```bash
    docker run -d -p 4566:4566 localstack/localstack
    ```
2.  **Postgres 容器启动命令**
    ```bash
    docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=acp postgres
    ```
3.  **AWS 基础配置**
    - Access Key: test
    - Secret Key: test
    - Region: US_EAST_1
    - S3 Bucket 名称：[学生SID]
    - DynamoDB 表名：[学生SID]

## 三、 功能模块设计
### 3.1 模块划分
| 模块名称 | 核心功能 | 对应接口 |
| --- | --- | --- |
| S3 操作模块 | 读取 S3 Bucket 中全部/单个对象 | GET /all/s3/{bucket}、GET /single/s3/{bucket}/{key} |
| DynamoDB 操作模块 | 读取 DynamoDB 表中全部/单个对象 | GET /all/dynamo/{table}、GET /single/dynamo/{table}/{key} |
| Postgres 操作模块 | 读取 Postgres 表数据、数据写入 | GET /all/postgres/{table}、POST 相关写入接口 |
| ILP 服务集成模块 | 读取第三方数据、处理并存储 | POST /process/dump |
| 数据复制模块 | Postgres 数据复制到 S3/DynamoDB | POST /copy-content/dynamo/{table}、POST /copy-content/S3/{table} |

### 3.2 核心业务逻辑
1.  S3/DynamoDB/Postgres 读取逻辑：调用对应 SDK 接口获取数据，序列化为 JSON 格式返回，未找到数据返回 404。
2.  ILP 数据处理逻辑：拼接 URL 读取无人机数据，计算 `costPer100Moves = costInitial + costFinal + costPerMove * 100`，并将数据写入指定存储。
3.  数据复制逻辑：读取 Postgres 表数据，生成 UUID 作为主键/对象 Key，分别写入 DynamoDB 或 S3 Bucket。

## 四、 接口开发规范
### 4.1 通用规则
1.  所有接口路径前缀为 `/api/v1/acp`。
2.  成功操作返回 HTTP 200，资源未找到返回 HTTP 404，404 无响应体。
3.  响应数据统一为 JSON 格式。

### 4.2 接口详情
| 接口方法 | 接口路径 | 请求参数 | 响应数据 | 业务逻辑 |
| --- | --- | --- | --- | --- |
| GET | /all/s3/{bucket} | 路径参数：bucket（S3 桶名） | JSON 数组：桶内所有对象内容 | 读取指定 S3 桶内全部对象，返回内容列表 |
| GET | /single/s3/{bucket}/{key} | 路径参数：bucket、key（对象键） | JSON：指定对象内容 | 读取 S3 桶内指定 Key 的对象，返回内容 |
| GET | /all/dynamo/{table} | 路径参数：table（DynamoDB 表名） | JSON 数组：表内所有对象 | 读取 DynamoDB 表内全部数据，返回对象列表 |
| GET | /single/dynamo/{table}/{key} | 路径参数：table、key（主键值） | JSON：指定主键的对象 | 读取 DynamoDB 表内指定主键的数据 |
| GET | /all/postgres/{table} | 路径参数：table（Postgres 表名） | JSON 数组：表内所有行数据 | 读取 Postgres 表内全部行，转为 JSON 对象列表 |
| POST | /process/dump | 请求体：{"urlPath": "xxx"} | 处理后的 JSON 数据（含 costPer100Moves 字段） | 拼接 ILP 服务 URL，读取数据并计算，写入指定存储后返回结果 |
| POST | /copy-content/dynamo/{table} | 路径参数：table（Postgres 表名） | HTTP 200（无响应体） | 读取 Postgres 表数据，以 UUID 为键写入 DynamoDB |
| POST | /copy-content/S3/{table} | 路径参数：table（Postgres 表名） | HTTP 200（无响应体） | 读取 Postgres 表数据，以 UUID 为 Key 写入 S3 Bucket |

## 五、 测试方案
### 5.1 测试类型
1.  **单元测试**：测试各工具类（S3Util、DynamoDBUtil、PostgresUtil）的核心方法。
2.  **接口测试**：使用 Postman/curl 验证每个接口的请求参数、响应状态码和数据正确性。
3.  **容器化测试**：构建 Docker 镜像后，运行容器并测试接口可用性。

### 5.2 测试用例示例
| 测试接口 | 测试步骤 | 预期结果 |
| --- | --- | --- |
| GET /all/s3/[SID] | 1. 在 localstack 创建 S3 桶并上传测试文件<br>2. 调用接口 | 返回 200，响应体为测试文件内容的 JSON 数组 |
| POST /process/dump | 1. 构造包含 urlPath 的请求体<br>2. 调用接口 | 返回 200，响应体包含 costPer100Moves 字段；DynamoDB/S3 中存在对应数据 |

## 六、 部署流程
### 6.1 Jar 包打包
1.  在 IntelliJ 中执行 Maven 命令 `clean package`。
2.  打包后的 Jar 包位于 `target` 目录下。

### 6.2 Docker 镜像构建
1.  在项目根目录创建 `Dockerfile`，内容如下：
    ```dockerfile
    FROM openjdk:11-jre-slim
    COPY target/*.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "/app.jar"]
    ```
2.  执行镜像构建命令：
    ```bash
    docker buildx build --platform linux/amd64,linux/arm64 -t acp-submission-image --load .
    ```
3.  保存镜像为 tar 文件：
    ```bash
    docker image save -o acp_submission_image.tar acp-submission-image
    ```

### 6.3 部署验证
1.  加载 tar 镜像并运行容器：
    ```bash
    docker image load -i acp_submission_image.tar
    docker run -d -p 8080:8080 -e ACP_POSTGRES=[连接字符串] acp-submission-image
    ```
2.  调用接口验证服务是否正常运行。

## 七、 提交规范
### 7.1 目录结构要求
```
acp_submission_1/
├─ acp_submission_image.tar  # Docker 镜像文件
├─ src/                      # Java 源代码目录
└─ [IDE 项目文件]            # IntelliJ 项目文件（.idea、.iml 等）
```

### 7.2 提交步骤
1.  将上述目录压缩为 ZIP 文件。
2.  登录 Learn 平台，上传 ZIP 文件完成提交。

## 八、 注意事项
1.  必须使用 AWS SDK V2 版本，Region 配置为 US_EAST_1。
2.  S3 Bucket 名、DynamoDB 表名必须为学生 SID。
3.  环境变量不可硬编码在代码中，需通过 Spring Boot 配置文件占位符获取。
4.  提交前需验证 tar 镜像可正常加载、容器可正常运行、接口功能符合要求。
5.  Piazza 提问需在截止日期前 3 天完成，避免临时问题无法解答。

---

要不要我帮你基于这个模板，生成一份**可直接填充的空白开发文档**，把需要手动填写的部分用标记标出来？