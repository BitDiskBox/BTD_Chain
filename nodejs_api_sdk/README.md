# 服务相关
> 安装依赖
```
npm install
```
> 配置文件
* 测试 ./config/default.json
* 正式 ./config/production.json

>配置文件字段
* port -- 启动服务端口
* chain-url -- 链websocket地址
* chain-base-url -- 链http地址
* issuer address -- 网关地址
* activation-account -- 激活账号和私钥
 
> 启动
* 测试
```
npm run dev
```
* 正式
```
npm run start
```

> 测试
* 地址 http://localhost:8081/
