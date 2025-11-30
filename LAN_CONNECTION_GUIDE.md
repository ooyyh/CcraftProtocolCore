# 局域网连接指南

## 快速开始

### 步骤 1: 准备 Minecraft 客户端

1. 启动 **Minecraft 1.8.9** 客户端
2. 创建或打开一个单人世界
3. 按 **ESC** 键打开菜单
4. 点击 **"对局域网开放"** (Open to LAN)
5. 选择游戏模式 (创造/生存)
6. 点击 **"开启局域网世界"**
7. 记下显示的端口号,例如:
   ```
   本地游戏已在端口 38342 上开启
   ```

### 步骤 2: 编译项目

在 IntelliJ IDEA 中:
- Build -> Build Project

或使用命令行:
```bash
build.bat
```

### 步骤 3: 连接到局域网服务器

#### 方式 1: 直接连接 (推荐)

如果你知道端口号:
```bash
java -cp out examples.ConnectToDedicatedServer localhost 38342 TestBot
```

在 IntelliJ IDEA 中:
1. 打开 `ConnectToDedicatedServer.java`
2. 修改第 8 行的端口号:
   ```java
   int port = 38342;  // 改成你的端口号
   ```
3. 右键 -> Run 'ConnectToDedicatedServer.main()'

#### 方式 2: 自动扫描

使用自动扫描功能:
```bash
java -cp out examples.ConnectToLANServer TestBot
```

在 IntelliJ IDEA 中:
1. 打开 `ConnectToLANServer.java`
2. 右键 -> Run 'ConnectToLANServer.main()'

## 预期输出

### 成功连接

```
=== Minecraft 1.8.9 Protocol Client ===
Connecting to dedicated server...
Host: localhost
Port: 38342
Username: TestBot
Note: Supports both offline and encrypted connections

Connecting to localhost:38342
Server requested encryption
Setting up encryption...
Encryption enabled
Login successful! UUID: 30fecbe1-2271-3418-8553-d3ded0e95f56, Username: TestBot
Entered PLAY state, waiting for packets...
Received packet: PacketJoinGame (ID: 0x1)
Joined game! Entity ID: 123
Gamemode: 0
Dimension: 0
Difficulty: 1
Level Type: default
Received packet: PacketPlayerPositionAndLook (ID: 0x8)
Position updated: X=8.5, Y=64.0, Z=8.5
Rotation: Yaw=0.0, Pitch=0.0
Received packet: PacketKeepAlive (ID: 0x0)
Received packet: PacketKeepAlive (ID: 0x0)
...
```

### 在 Minecraft 客户端中

你应该看到:
```
TestBot 加入了游戏
```

## 常见问题

### Q1: "Connection refused" 错误

**原因**:
- 端口号不正确
- Minecraft 客户端没有打开局域网
- 防火墙阻止连接

**解决方案**:
1. 确认 Minecraft 客户端已打开局域网
2. 检查端口号是否正确
3. 临时关闭防火墙测试
4. 确保使用 `localhost` 或 `127.0.0.1`

### Q2: "Server requested encryption" 后断开

**原因**:
- 旧版本代码不支持加密
- 加密实现有问题

**解决方案**:
1. 确保使用最新版本的代码
2. 检查是否正确实现了 `enableEncryption()` 方法
3. 查看完整的错误堆栈

### Q3: 连接成功但立即断开

**原因**:
- 数据包响应不正确
- 没有正确响应 KeepAlive

**解决方案**:
1. 查看服务器日志 (Minecraft 客户端的日志)
2. 检查是否正确响应了所有必需的数据包
3. 确认 KeepAlive 数据包正确响应

### Q4: 自动扫描找不到服务器

**原因**:
- 多播被防火墙阻止
- 不在同一网络
- Minecraft 客户端没有正确广播

**解决方案**:
1. 使用直接连接方式 (方式 1)
2. 检查防火墙设置,允许多播 (224.0.2.60:4445)
3. 确认在同一网络中

## 调试技巧

### 查看详细日志

客户端会显示每个接收到的数据包:
```
Received packet: PacketJoinGame (ID: 0x1)
Received packet: PacketPlayerPositionAndLook (ID: 0x8)
Received packet: PacketKeepAlive (ID: 0x0)
```

### 查看 Minecraft 客户端日志

1. 在 Minecraft 启动器中,点击 "启动选项"
2. 勾选 "启动器可见性" -> "启动游戏时保持启动器打开"
3. 查看控制台输出

或查看日志文件:
```
%APPDATA%\.minecraft\logs\latest.log
```

### 使用 Wireshark 抓包

1. 下载并安装 Wireshark
2. 捕获 Loopback 接口
3. 过滤器: `tcp.port == 38342` (替换成你的端口)
4. 查看加密前后的数据包

## 测试清单

- [ ] Minecraft 1.8.9 客户端已启动
- [ ] 单人世界已打开到局域网
- [ ] 记下了正确的端口号
- [ ] 项目已编译成功
- [ ] 端口号已正确配置
- [ ] 运行客户端程序
- [ ] 看到 "Encryption enabled" 消息
- [ ] 看到 "Login successful" 消息
- [ ] 看到 "Joined game" 消息
- [ ] 在 Minecraft 中看到 "TestBot 加入了游戏"
- [ ] 客户端持续运行,没有断开

## 高级用法

### 修改用户名

在代码中修改:
```java
String username = "MyBot";  // 改成你想要的名字
```

或使用命令行参数:
```bash
java -cp out examples.ConnectToDedicatedServer localhost 38342 MyBot
```

### 连接到其他电脑的局域网服务器

如果局域网服务器在另一台电脑上:
```bash
java -cp out examples.ConnectToDedicatedServer 192.168.1.100 38342 TestBot
```

替换 `192.168.1.100` 为服务器电脑的 IP 地址。

### 同时运行多个客户端

可以同时连接多个机器人:
```bash
# 终端 1
java -cp out examples.ConnectToDedicatedServer localhost 38342 Bot1

# 终端 2
java -cp out examples.ConnectToDedicatedServer localhost 38342 Bot2

# 终端 3
java -cp out examples.ConnectToDedicatedServer localhost 38342 Bot3
```

## 下一步

成功连接后,你可以:
1. 查看接收到的数据包
2. 修改代码添加自定义功能
3. 实现聊天命令响应
4. 添加自动移动功能
5. 实现更多游戏交互

查看 `README.md` 和 `CHANGELOG.md` 了解更多信息。

## 技术支持

如果遇到问题:
1. 查看 `TESTING.md` 获取详细的排查指南
2. 查看 `CHANGELOG.md` 了解最新更新
3. 检查 GitHub Issues

---

**祝你玩得开心!** 🎮
