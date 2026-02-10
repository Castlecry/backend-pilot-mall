# Pilot-Mall

基于 Spring Cloud 微服务架构的电商秒杀系统，支持高并发秒杀和 AI 智能导购功能。

## 项目简介

Pilot-Mall 是一个完整的微服务电商系统，采用 Spring Boot 3.3 + Spring Cloud Alibaba 技术栈，实现了高并发秒杀、异步订单处理和 AI 智能导购等核心功能。

### 核心功能

- **用户认证**：基于 JWT 的用户登录和权限验证
- **高并发秒杀**：使用 Redis + Lua 脚本实现原子性库存扣减
- **异步订单处理**：通过 RabbitMQ 消息队列异步处理订单
- **AI 智能导购**：集成 LangChain4j + Ollama 实现智能商品推荐
- **服务注册与发现**：基于 Nacos 的微服务注册中心
- **API 网关**：统一入口，支持路由转发和跨域处理

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|--------|------|
| Spring Boot | 3.3 | 基础框架 |
| Spring Cloud Gateway | 2023.0.3 | API 网关 |
| Spring Cloud Alibaba | 2023.0.1.2 | 微服务组件 |
| Nacos | 2.3.2 | 服务注册与发现 |
| MyBatis | 3.0.3 | 持久层框架 |
| Redis | 7.x | 缓存与库存管理 |
| RabbitMQ | 3.12.x | 消息队列 |
| LangChain4j | 0.36.x | AI 框架 |
| Ollama | latest | 本地 AI 模型 |

### 前端技术（建议）

- Vue 3 / React 18
- Ant Design / Element Plus
- Axios
- Pinia / Redux

## 项目结构

```
pilot-mall/
├── mall-common/              # 公共模块
│   └── src/main/java/com/pilot/common/
│       ├── api/             # 统一返回类
│       ├── exception/       # 全局异常处理
│       └── utils/          # 工具类（JWT 等）
│
├── mall-user/              # 用户服务
│   └── src/main/java/com/pilot/user/
│       ├── controller/      # 用户控制器
│       ├── mapper/         # 数据访问层
│       └── domain/         # 实体类
│
├── mall-order/             # 订单服务
│   └── src/main/java/com/pilot/order/
│       ├── controller/      # 订单、商品、AI 控制器
│       ├── service/        # 业务逻辑层
│       ├── mapper/         # 数据访问层
│       ├── domain/         # 实体类
│       ├── dto/           # 数据传输对象
│       └── runner/         # 启动任务（库存预热）
│
├── mall-gateway/           # 网关服务
│   └── src/main/java/com/pilot/gateway/
│       ├── filter/         # 全局过滤器（认证）
│       └── config/        # 配置类（CORS）
│
└── 接口文档.md            # API 接口文档
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x
- RabbitMQ 3.12+
- Nacos 2.3.2+
- Ollama（用于 AI 功能）

### 安装步骤

1. **克隆项目**

```bash
git clone https://github.com/your-username/pilot-mall.git
cd pilot-mall
```

2. **初始化数据库**

创建数据库 `pilot_mall`，执行以下 SQL：

```sql
-- 用户表
CREATE TABLE ums_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE pms_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE oms_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_sn VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO ums_user (username, password) 
VALUES ('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');

INSERT INTO pms_product (name, price, stock, description) 
VALUES ('智能手表', 299.00, 100, '这款智能手表支持心率监测、睡眠分析、运动模式等多种功能，续航长达7天，防水等级IP68，适合日常运动和商务场合。');
```

3. **启动中间件**

```bash
# 启动 Nacos
cd nacos/bin
./startup.sh -m standalone

# 启动 Redis
redis-server

# 启动 RabbitMQ
rabbitmq-server

# 启动 Ollama（AI 功能）
ollama serve
```

4. **拉取 AI 模型**

```bash
ollama pull gpt-oss:20b
# 或
ollama pull deepseek-r1:8b
```

5. **启动服务**

按以下顺序启动各个微服务：

```bash
# 1. 启动用户服务（端口 8081）
cd mall-user
mvn spring-boot:run

# 2. 启动订单服务（端口 8082）
cd mall-order
mvn spring-boot:run

# 3. 启动网关服务（端口 8084）
cd mall-gateway
mvn spring-boot:run
```

6. **访问服务**

- **Nacos 控制台**：http://127.0.0.1:8848/nacos
- **网关入口**：http://localhost:8084
- **RabbitMQ 管理界面**：http://localhost:15672 (guest/guest)

## 接口文档

详细的 API 接口文档请查看：[接口文档.md](./接口文档.md)

### 主要接口

| 接口 | 路径 | 说明 |
|--------|--------|------|
| 用户登录 | POST /api/user/login | 用户登录，返回 JWT Token |
| 商品列表 | GET /api/product/list | 获取所有商品列表 |
| 商品库存 | GET /api/product/stock | 查询商品库存（Redis） |
| 创建订单 | POST /api/order/create | 创建秒杀订单（异步） |
| 订单列表 | GET /api/order/list | 查询用户订单列表 |
| AI 导购 | POST /api/ai/chat | 智能商品咨询 |

## 核心功能说明

### 1. 高并发秒杀

使用 Redis + Lua 脚本实现原子性库存扣减，避免超卖问题：

```lua
-- decrease_stock.lua
local key = KEYS[1]
local stock = tonumber(redis.call('get', key))

if stock == nil then
    return -1
end

if stock <= 0 then
    return 0
end

redis.call('decrby', key, 1)
return 1
```

### 2. 异步订单处理

订单创建后通过 RabbitMQ 异步处理，提高系统响应速度：

```
用户下单 → Redis 扣减库存 → 发送消息到队列 → 消费者异步入库
```

### 3. AI 智能导购

基于商品描述和用户问题，使用 LangChain4j + Ollama 生成智能回复：

```java
String context = buildContext(product);
String prompt = buildPrompt(context, userMessage);
String response = chatModel.generate(prompt);
```

### 4. 库存预热

服务启动时自动将商品库存加载到 Redis，提高查询性能：

```java
@Component
public class StockPreheatRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        List<PmsProduct> products = productMapper.selectAll();
        for (PmsProduct product : products) {
            redisTemplate.opsForValue().set("product:stock:" + product.getId(), 
                String.valueOf(product.getStock()));
        }
    }
}
```

## 配置说明

### 端口分配

| 服务 | 端口 |
|--------|--------|
| mall-user | 8081 |
| mall-order | 8082 |
| mall-gateway | 8084 |
| Nacos | 8848 |
| MySQL | 3306 |
| Redis | 6379 |
| RabbitMQ | 5672 / 15672 (管理界面) |
| Ollama | 11434 |

### 关键配置

**Nacos 服务注册**：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        ip: 127.0.0.1  # 指定注册 IP
```

**Redis 库存管理**：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

**RabbitMQ 消息队列**：

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    deserialization:
      trust-all: true  # 允许反序列化所有类
```

**AI 模型配置**：

```yaml
langchain4j:
  ollama:
    chat-model:
      base-url: http://localhost:11434
      model-name: gpt-oss:20b
      log-requests: true
      log-responses: true
```

## 常见问题

### 1. Redis 中没有新添加的商品库存？

**原因**：库存预热只在服务启动时执行一次。

**解决**：重启 mall-order 服务，或手动调用库存预热接口（待开发）。

### 2. 网关无法连接到微服务？

**原因**：Nacos 注册的 IP 地址不可达。

**解决**：在配置文件中添加 `ip: 127.0.0.1` 指定注册 IP。

### 3. RabbitMQ 反序列化错误？

**原因**：Spring AMQP 3.1+ 的安全限制。

**解决**：在配置中添加 `spring.rabbitmq.deserialization.trust-all: true`。

### 4. AI 接口响应慢？

**原因**：Ollama 模型加载和推理需要时间。

**解决**：使用更小的模型（如 deepseek-r1:8b），或增加 Ollama 资源。

## 开发指南

### 添加新接口

1. 在对应的 Controller 中添加接口方法
2. 在 Service 层实现业务逻辑
3. 在 Mapper 层添加数据访问方法（如需要）
4. 更新接口文档

### 添加新微服务

1. 创建新的 Spring Boot 模块
2. 配置 Nacos 服务注册
3. 在 Gateway 中添加路由配置
4. 更新接口文档

## 测试

### 单元测试

```bash
mvn test
```

### 接口测试

使用 Postman 或 curl 测试接口：

```bash
# 登录
curl -X POST http://localhost:8084/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 获取商品列表
curl http://localhost:8084/api/product/list

# 创建订单
curl -X POST http://localhost:8084/api/order/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"productId":1}'
```

## 性能优化建议

1. **Redis 缓存**：热点数据缓存到 Redis
2. **数据库索引**：为常用查询字段添加索引
3. **消息队列**：异步处理耗时操作
4. **连接池**：优化数据库和 Redis 连接池配置
5. **负载均衡**：多实例部署，使用 Nacos 负载均衡

## 安全建议

1. **密码加密**：使用 BCrypt 加密用户密码
2. **JWT 过期**：设置合理的 Token 过期时间
3. **输入验证**：对所有用户输入进行校验
4. **SQL 注入防护**：使用 MyBatis 参数化查询
5. **XSS 防护**：对用户输入进行转义

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

- 项目地址：https://github.com/your-username/pilot-mall
- 问题反馈：https://github.com/your-username/pilot-mall/issues

## 致谢

感谢以下开源项目：

- Spring Boot
- Spring Cloud Alibaba
- Nacos
- MyBatis
- Redis
- RabbitMQ
- LangChain4j
- Ollama
