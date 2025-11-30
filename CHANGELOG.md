# 更新日志

## [1.1.0] - 2025-11-30

### 新增功能
- ✅ **加密支持**: 实现了 AES/CFB8 加密,可以连接到局域网服务器
- ✅ **自动加密协商**: 自动处理服务器的加密请求
- ✅ **加密流**: 使用 CipherInputStream/CipherOutputStream 实现透明加密

### 改进
- 改进了数据包发送顺序,先接收 JoinGame 再发送 ClientSettings
- 添加了详细的数据包接收日志
- 改进了错误处理,避免显示无用的 EOFException
- 优化了连接关闭检测

### 修复
- 修复了 "Bad packet id 21" 错误
- 修复了位置响应数据包 ID 错误 (使用 0x06 而不是 0x08)
- 修复了连接到局域网服务器时的加密问题

### 技术细节
- 添加了 `PacketPlayerPositionAndLookClient` (0x06) 用于客户端发送
- 在 `MinecraftConnection` 中实现了 `enableEncryption()` 方法
- 使用 RSA 加密共享密钥和验证令牌
- 使用 AES/CFB8/NoPadding 进行流加密

## [1.0.0] - 2025-11-30

### 初始版本
- 完整的 Minecraft 1.8.9 协议实现
- 支持连接到专用服务器
- 支持连接到局域网服务器
- 数据包压缩支持
- 自动 KeepAlive 处理
- 20+ 个核心数据包实现

---

## 使用说明

### 连接到局域网服务器

现在可以直接连接到 Minecraft 客户端打开的局域网世界:

1. 在 Minecraft 1.8.9 客户端中打开世界到局域网
2. 记下端口号 (例如: 38342)
3. 运行客户端:
   ```bash
   java -cp out examples.ConnectToDedicatedServer localhost 38342 TestBot
   ```
   或使用自动扫描:
   ```bash
   java -cp out examples.ConnectToLANServer TestBot
   ```

### 预期输出

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
Login successful! UUID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, Username: TestBot
Entered PLAY state, waiting for packets...
Received packet: PacketJoinGame (ID: 0x1)
Joined game! Entity ID: xxx
Gamemode: 0
Dimension: 0
Difficulty: 1
Level Type: default
Received packet: PacketPlayerPositionAndLook (ID: 0x8)
Position updated: X=x.x, Y=y.y, Z=z.z
Rotation: Yaw=0.0, Pitch=0.0
Received packet: PacketKeepAlive (ID: 0x0)
...
```

### 支持的连接类型

| 连接类型 | 加密 | Mojang 验证 | 支持状态 |
|---------|------|------------|---------|
| 离线模式服务器 | ❌ | ❌ | ✅ 完全支持 |
| 离线模式服务器 (压缩) | ❌ | ❌ | ✅ 完全支持 |
| 局域网服务器 | ✅ | ❌ | ✅ 完全支持 |
| 在线模式服务器 | ✅ | ✅ | ❌ 不支持 |

### 技术说明

**加密流程**:
1. 客户端发送 Handshake 和 LoginStart
2. 服务器发送 EncryptionRequest (包含公钥和验证令牌)
3. 客户端生成 AES 共享密钥
4. 客户端使用服务器公钥加密共享密钥和验证令牌
5. 客户端发送 EncryptionResponse
6. 双方启用 AES/CFB8 加密
7. 服务器发送 LoginSuccess (加密)
8. 进入 PLAY 状态 (所有后续通信都加密)

**为什么不需要 Mojang 验证?**

局域网服务器虽然使用加密,但不会验证 Mojang 账户。这是因为:
- 局域网模式默认信任本地网络
- 不需要联系 Mojang 的会话服务器
- 只需要正确的加密握手即可

**与在线模式的区别**:

在线模式服务器会:
1. 发送 EncryptionRequest
2. 客户端需要联系 Mojang 会话服务器验证
3. 服务器验证客户端的 Mojang 账户
4. 只有验证通过才允许登录

局域网模式:
1. 发送 EncryptionRequest
2. 客户端直接响应 (不需要 Mojang 验证)
3. 服务器接受加密连接
4. 直接进入游戏

## 下一步计划

- [ ] 添加更多游戏数据包 (区块、实体等)
- [ ] 实现聊天命令响应
- [ ] 添加自动移动功能
- [ ] 支持物品栏操作
- [ ] 添加世界交互功能
