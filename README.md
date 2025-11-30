# Minecraft 1.8.9 Protocol Core

完整的 Minecraft 1.8.9 协议实现,支持连接到专用服务器和局域网服务器。

## 功能特性

- 完整的 Minecraft 1.8.9 协议实现 (Protocol Version 47)
- 支持连接到专用服务器 (Dedicated Server)
- 支持连接到局域网服务器 (LAN Server)
- 支持数据包压缩
- 模块化的数据包系统
- 自动处理 KeepAlive 数据包
- 支持离线模式服务器

## 项目结构

```
src/
├── Main.java                          # 主入口
├── client/
│   └── MinecraftClient.java          # 客户端核心逻辑
├── network/
│   └── MinecraftConnection.java      # 网络连接处理
├── protocol/
│   ├── Packet.java                   # 数据包接口
│   ├── PacketBuffer.java             # 数据包缓冲区
│   ├── PacketRegistry.java           # 数据包注册表
│   ├── ProtocolState.java            # 协议状态枚举
│   └── packets/
│       ├── handshake/                # 握手协议包
│       │   └── PacketHandshake.java
│       ├── status/                   # 状态协议包
│       │   ├── PacketStatusRequest.java
│       │   ├── PacketStatusResponse.java
│       │   ├── PacketStatusPing.java
│       │   └── PacketStatusPong.java
│       ├── login/                    # 登录协议包
│       │   ├── PacketLoginStart.java
│       │   ├── PacketLoginSuccess.java
│       │   ├── PacketLoginDisconnect.java
│       │   ├── PacketEncryptionRequest.java
│       │   ├── PacketEncryptionResponse.java
│       │   └── PacketSetCompression.java
│       └── play/                     # 游戏协议包
│           ├── PacketJoinGame.java
│           ├── PacketKeepAlive.java
│           ├── PacketPlayerPosition.java
│           ├── PacketPlayerPositionAndLook.java
│           ├── PacketChatMessage.java
│           ├── PacketClientSettings.java
│           ├── PacketClientStatus.java
│           └── PacketDisconnect.java
├── crypto/
│   └── MinecraftEncryption.java      # 加密工具类
└── examples/
    ├── ConnectToDedicatedServer.java # 专用服务器连接示例
    └── ConnectToLANServer.java       # 局域网服务器连接示例
```

## 使用方法

### 编译项目

```bash
javac -d out src/**/*.java
```

### 运行示例

#### 1. 连接到专用服务器

```bash
# 使用默认参数 (localhost:25565, 用户名: TestBot)
java -cp out Main dedicated

# 指定服务器地址和端口
java -cp out Main dedicated 192.168.1.100 25565 MyBot

# 连接到本地服务器
java -cp out Main dedicated localhost 25565 TestBot
```

#### 2. 连接到局域网服务器

```bash
# 使用默认用户名 (TestBot)
java -cp out Main lan

# 指定用户名
java -cp out Main lan MyBot
```

局域网模式会自动扫描网络中的 Minecraft 局域网服务器并连接。

## 协议说明

### 协议版本
- Minecraft 版本: 1.8.9
- 协议版本号: 47

### 支持的协议状态

1. **HANDSHAKING (握手)**: 初始连接状态
2. **STATUS (状态)**: 服务器列表 ping
3. **LOGIN (登录)**: 身份验证和登录
4. **PLAY (游戏)**: 游戏内数据包

### 已实现的核心功能

- 握手协议
- 登录流程 (离线模式)
- 数据包压缩支持
- KeepAlive 心跳
- 玩家位置同步
- 聊天消息接收
- 客户端设置发送
- 局域网服务器发现 (多播)

## 注意事项

1. **离线模式**: 支持离线模式服务器 (online-mode=false)
2. **加密**: 支持加密连接,可以连接到局域网服务器(不需要 Mojang 账户验证)
3. **局域网连接**: 需要确保防火墙允许多播流量 (224.0.2.60:4445)
4. **协议版本**: 仅支持 Minecraft 1.8.9 (协议版本 47)
5. **在线模式**: 不支持 Mojang 账户验证,但支持加密通信

## 扩展开发

### 添加新的数据包

1. 在 `protocol/packets/` 对应的目录下创建新的数据包类
2. 实现 `Packet` 接口
3. 在 `PacketRegistry.java` 中注册数据包

示例:

```java
public class MyCustomPacket implements Packet {
    @Override
    public void write(PacketBuffer buffer) throws IOException {
        // 写入数据
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        // 读取数据
    }

    @Override
    public int getPacketId() {
        return 0x??; // 数据包 ID
    }
}
```

### 处理新的数据包

在 `MinecraftClient.java` 的 `handlePlay()` 方法中添加处理逻辑:

```java
else if (packet instanceof MyCustomPacket) {
    MyCustomPacket custom = (MyCustomPacket) packet;
    // 处理数据包
}
```

## 测试环境

推荐使用以下方式测试:

1. **本地服务器**: 运行 Minecraft 1.8.9 服务器,设置 `online-mode=false`
2. **局域网世界**: 在 Minecraft 客户端中打开单人世界到局域网
3. **测试服务器**: 使用公共的离线模式测试服务器

## 技术细节

### 数据包格式

```
[Length (VarInt)] [Packet ID (VarInt)] [Data]
```

### 压缩格式 (启用压缩后)

```
[Packet Length (VarInt)] [Data Length (VarInt)] [Compressed Data]
```

### VarInt 编码

使用 Minecraft 协议标准的 VarInt 编码,每个字节的最高位表示是否还有后续字节。

## 许可证

本项目仅供学习和研究使用。

## 参考资料

- [Minecraft Protocol Wiki](https://wiki.vg/Protocol)
- [Minecraft 1.8.9 Protocol Documentation](https://wiki.vg/index.php?title=Protocol&oldid=7368)
