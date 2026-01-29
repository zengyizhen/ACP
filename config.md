### Architecture Overview
acp-submission-1/
├─ src/                      # 源代码目录
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ org/
│  │  │     └─ example.coursework1/          # 项目基础包名
│  │  │        ├─ Coursework1Application.java  # 项目启动类
│  │  │        ├─ configuration/    # 配置类包
│  │  │        ├─ controller/ # 控制器包（REST接口）
│  │  │        ├─ service/   # 服务层包（业务逻辑）
│  │  │        ├─ util/      # 工具类包（AWS、数据库操作）
│  │  │        ├─ model/     # 模型类包（请求、响应、实体）
│  │  │        └─ exception/ # 异常处理包
│  │  └─ resources/          # 资源文件目录
│  │     └─ application.yml  # 全局配置文件
│  └─ test/                  # 测试代码目录（可选）
│     └─ java/
│        └─
├─ Dockerfile                # Docker构建文件
├─ pom.xml                   # Maven依赖配置文件
├─ .gitignore                # Git忽略文件（可选）
└─ acp_submission_image.tar  # 打包后的Docker镜像（提交时放入）

### 创建Postgres数据库
1. docker启动Postgres容器

docker run --name acp-postgres \
-e POSTGRES_PASSWORD=postgres \
-e POSTGRES_DB=acp \
-p 5432:5432 \
-d postgres:15
2. 通过容器进入数据库
psql -h localhost -U postgres -d acp
3. 创建schema(SID

CREATE SCHEMA s2807348;
SET search_path To s2807348;
CREATE TABLE drones (
name VARCHAR PRIMARY KEY,
id VARCHAR,
cooling BOOLEAN,
heating BOOLEAN,
capacity INTEGER,
maxMoves INTEGER,
costPerMove DOUBLE PRECISION,
costInitial DOUBLE PRECISION,
costFinal DOUBLE PRECISION
);

4. 插入测试数据
INSERT INTO drones
(name, id, cooling, heating, capacity, maxMoves, costPerMove, costInitial, costFinal)
VALUES('Drone0', '0', true, true, 4, 2000, 0.01, 4.3, 6.5);

###  服务端连接Postgres数据库：
配置文件application.yml中添加：  
datasource:
   url: ${ACP_POSTGRES}
   username: postgres
   password: postgres
   driver-class-name: org.postgresql.Driver
jpa:
    show-sql: true
    hibernate:
        ddl-auto: none
