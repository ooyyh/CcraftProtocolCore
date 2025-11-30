# 快速开始指南

## 编译项目

### Windows
```bash
compile.bat
```

### Linux/Mac
```bash
chmod +x compile.sh
./compile.sh
```

## 运行示例

### 1. 连接到专用服务器

#### 方式一: 使用批处理文件 (Windows)
```bash
run-dedicated.bat
run-dedicated.bat localhost 25565 MyBot
```

#### 方式二: 直接运行
```bash
java -cp out Main dedicated
java -cp out Main dedicated localhost 25565 MyBot
java -cp out Main dedicated 192.168.1.100 25565 TestBot
```

### 2. 连接到局域网服务器

#### 方式一: 使用批处理文件 (Windows)
```bash
run-lan.bat
run-lan.bat MyBot
```

#### 方式二: 直接运行
```bash
java -cp out Main lan
java -cp out Main lan MyBot
```

## 测试环境设置

### 选项 1: 本地 Minecraft 服务器

1. 下载 Minecraft 1.8.9 服务器 jar
2. 创建 `server.properties` 文件并设置:
   ```properties
   online-mode=false
   server-port=25565
   ```
3. 运行服务器: `java -jar minecraft_server.1.8.9.jar`
4. 运行客户端: `java -cp out Main dedicated localhost 25565 TestBot`

### 选项 2: 局域网世界

1. 启动 Minecraft 1.8.9 客户端
2. 创建或打开一个单人世界
3. 按 ESC,点击 "对局域网开放"
4. 记下显示的端口号 (例如: 12345)
5. 运行客户端: `java -cp out Main lan`

客户端会自动扫描并连接到局域网服务器。

## 常见问题

### Q: 编译失败,提示找不到 javac
A: 确保已安装 JDK 并配置了环境变量。运行 `java -version` 和 `javac -version` 检查。

### Q: 连接失败,提示 "Connection refused"
A: 检查服务器是否运行,端口是否正确,防火墙是否允许连接。

### Q: 局域网模式找不到服务器
A: 确保:
- Minecraft 客户端已打开世界到局域网
- 在同一网络中
- 防火墙允许多播流量 (224.0.2.60:4445)

### Q: 服务器提示 "Failed to verify username"
A: 这是因为服务器开启了在线模式。将 `server.properties` 中的 `online-mode` 设置为 `false`。

### Q: 连接后立即断开
A: 检查服务器日志,可能是协议版本不匹配。确保服务器是 1.8.9 版本。

## 代码示例

### 自定义客户端

```java
import client.MinecraftClient;

public class MyBot {
    public static void main(String[] args) throws Exception {
        MinecraftClient client = new MinecraftClient("MyBot");

        client.connect("localhost", 25565);

        while (client.isRunning()) {
            Thread.sleep(100);
        }

        client.disconnect();
    }
}
```

### 处理自定义数据包

在 `MinecraftClient.java` 的 `handlePlay()` 方法中添加:

```java
else if (packet instanceof PacketChatMessage) {
    PacketChatMessage chat = (PacketChatMessage) packet;
    System.out.println("Chat: " + chat.getMessage());

    // 自定义处理逻辑
    if (chat.getMessage().contains("hello")) {
        // 发送响应
    }
}
```

## 下一步

- 查看 `README.md` 了解完整的项目文档
- 查看 `src/examples/` 目录了解更多示例
- 查看 `src/protocol/packets/` 目录了解所有支持的数据包
- 访问 https://wiki.vg/Protocol 了解 Minecraft 协议详情
