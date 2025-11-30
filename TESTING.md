# 测试指南

## 编译项目

使用 IntelliJ IDEA 或命令行编译:

### 使用 IntelliJ IDEA
1. 打开项目
2. 右键点击 `src` 文件夹 -> Mark Directory as -> Sources Root
3. Build -> Build Project

### 使用命令行
```bash
build.bat
```

## 测试连接到专用服务器

### 准备测试服务器

1. **下载 Minecraft 1.8.9 服务器**
   - 访问 https://mcversions.net/
   - 下载 Minecraft 1.8.9 服务器 jar

2. **配置服务器**

   创建 `server.properties` 文件:
   ```properties
   online-mode=false
   server-port=25565
   max-players=20
   ```

3. **启动服务器**
   ```bash
   java -jar minecraft_server.1.8.9.jar nogui
   ```

4. **运行客户端**
   ```bash
   java -cp out examples.ConnectToDedicatedServer
   ```

### 预期输出

成功连接时,你应该看到:

```
=== Minecraft 1.8.9 Protocol Client ===
Connecting to dedicated server...
Host: localhost
Port: 25565
Username: TestBot
Note: This client only supports offline mode servers

Connecting to localhost:25565
Login successful! UUID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, Username: TestBot
Entered PLAY state, waiting for packets...
Received packet: PacketJoinGame (ID: 0x1)
Joined game! Entity ID: 123
Gamemode: 0
Dimension: 0
Difficulty: 1
Level Type: default
Received packet: PacketPlayerPositionAndLook (ID: 0x8)
Position updated: X=0.0, Y=64.0, Z=0.0
Rotation: Yaw=0.0, Pitch=0.0
Received packet: PacketKeepAlive (ID: 0x0)
...
```

服务器日志应该显示:
```
[INFO]: TestBot joined the game
```

## 测试连接到局域网服务器

### 准备局域网服务器

1. **启动 Minecraft 1.8.9 客户端**
2. **创建或打开单人世界**
3. **按 ESC,点击 "对局域网开放"**
4. **选择游戏模式和设置**
5. **点击 "开启局域网世界"**
6. **记下显示的端口号** (例如: "本地游戏已在端口 12345 上开启")

### 运行客户端

```bash
java -cp out examples.ConnectToLANServer
```

### 预期输出

```
=== Minecraft 1.8.9 Protocol Client - LAN Mode ===
Username: TestBot
Scanning for LAN servers...

Listening for LAN server broadcasts...
Found LAN server!
MOTD: My World
Host: 192.168.1.100
Port: 12345

Connecting...
Connecting to 192.168.1.100:12345
Login successful! UUID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, Username: TestBot
Entered PLAY state, waiting for packets...
...
```

## 常见问题排查

### 问题 1: "Bad packet id" 错误

**症状**: 服务器日志显示 "Bad packet id XX"

**原因**: 客户端发送的数据包格式不正确或 ID 错误

**解决方案**:
- 确保使用的是 Minecraft 1.8.9 服务器
- 检查数据包 ID 是否正确
- 查看调试输出确认发送的数据包

### 问题 2: "Connection refused"

**症状**: 无法连接到服务器

**解决方案**:
- 确认服务器正在运行
- 检查端口号是否正确
- 检查防火墙设置
- 确认服务器绑定到正确的地址

### 问题 3: "Failed to verify username"

**症状**: 服务器拒绝连接,提示验证失败

**解决方案**:
- 在 `server.properties` 中设置 `online-mode=false`
- 重启服务器

### 问题 4: 局域网模式找不到服务器

**症状**: 扫描超时,未找到服务器

**解决方案**:
- 确认 Minecraft 客户端已打开世界到局域网
- 检查是否在同一网络
- 检查防火墙是否允许多播 (224.0.2.60:4445)
- 在 Windows 上,可能需要允许 Java 通过防火墙

### 问题 5: EOFException 或连接突然断开

**症状**: 客户端连接后立即断开

**解决方案**:
- 检查是否正确响应了所有必需的数据包
- 确认 KeepAlive 数据包正确响应
- 查看服务器日志了解断开原因

## 调试技巧

### 启用详细日志

客户端已经包含了详细的数据包日志。每个接收到的数据包都会显示:
```
Received packet: PacketName (ID: 0xXX)
```

### 查看服务器日志

服务器日志通常在 `logs/latest.log`,可以提供有用的错误信息。

### 使用 Wireshark

如果需要深入调试,可以使用 Wireshark 捕获网络流量:
1. 启动 Wireshark
2. 捕获 loopback 接口 (本地连接) 或网络接口
3. 过滤器: `tcp.port == 25565`
4. 分析数据包内容

## 性能测试

### 测试 KeepAlive 响应

客户端应该自动响应所有 KeepAlive 数据包。观察日志确认:
```
Received packet: PacketKeepAlive (ID: 0x0)
```

### 测试长时间连接

让客户端运行几分钟,确认:
- 没有断开连接
- KeepAlive 正常响应
- 没有内存泄漏

### 测试多个客户端

可以同时运行多个客户端实例:
```bash
java -cp out Main dedicated localhost 25565 Bot1
java -cp out Main dedicated localhost 25565 Bot2
java -cp out Main dedicated localhost 25565 Bot3
```

## 下一步

成功测试后,可以:
1. 添加更多数据包处理
2. 实现机器人功能
3. 添加聊天命令响应
4. 实现自动移动
5. 添加区块加载支持

## 参考资料

- [Minecraft Protocol Wiki](https://wiki.vg/Protocol)
- [Protocol Version 47 (1.8.9)](https://wiki.vg/index.php?title=Protocol&oldid=7368)
- [Data Types](https://wiki.vg/Data_types)
