# 华时融合平台 

## 融合主体：[短信，流量，语音]

### 架构项目

1. hsapi -> 内部SDK服务包：主要提供 接口和网络通讯的DOMAIN实体
2. hscommon -> 公用服务微服务：主要包含账户体系，系统设置，财务管理等
3. hs-developer -> 开发者微服务 : 主要是针对接口服务（Rest API），目前融合子项目会放在一起对外，主要用于和用户前置验证、交互
4. hs-producer -> 数据中心异步生产者微服务: 接口开发者微服务调度，产生异步队列数据
5. hs-consumer -> 数据中心异步消费者微服务: 监听生产者异步队列数据，实时分发处理
6. hssms -> 短信微服务
7. hsfs -> 流量微服务
8. hsvs -> 语音微服务
9. hsweb -> 华时融合平台Web
10. hsboss -> 华时融合平台运营、支撑系统

### 使用框架

- 配置中心: Zookeeper
- SOA: Dubbox, Spring Boot
- 项目管理: Maven, SVN
- 消息队列: RabbitMQ
- 数据库: Redis, MySQL
- 对象存储: MongoDB
- Rest框架：Jersey
- 前端技术：Jquery,Freemarker

### 使用需注意

1. 打成jar包之后,启动项目:

```
java -jar xxx.jar
```

2. Maven打包使用

```
mvn install -Dmaven.test.skip=true
```

跳过测试代码。
