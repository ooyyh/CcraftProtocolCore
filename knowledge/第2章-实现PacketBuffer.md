# 第2章: 实现 PacketBuffer - 数据包缓冲区

## 2.1 为什么需要 PacketBuffer?

想象你要打包一个快递:
- 你需要一个箱子(缓冲区)
- 你往箱子里放东西(写入数据)
- 收件人从箱子里取东西(读取数据)

**PacketBuffer 就是这个"箱子"**,用来:
1. 临时存储数据
2. 提供方便的读写方法
3. 处理各种数据类型

## 2.2 PacketBuffer 的设计

### 核心功能

```java
public class PacketBuffer {
    // 写入功能
    writeVarInt(int value)      // 写入可变长整数
    writeString(String value)   // 写入字符串
    writeByte(int value)        // 写入字节
    writeShort(int value)       // 写入短整数
    writeInt(int value)         // 写入整数
    writeLong(long value)       // 写入长整数
    writeFloat(float value)     // 写入浮点数
    writeDouble(double value)   // 写入双精度浮点数
    writeBoolean(boolean value) // 写入布尔值

    // 读取功能
    readVarInt()                // 读取可变长整数
    readString()                // 读取字符串
    readByte()                  // 读取字节
    readShort()                 // 读取短整数
    readInt()                   // 读取整数
    readLong()                  // 读取长整数
    readFloat()                 // 读取浮点数
    readDouble()                // 读取双精度浮点数
    readBoolean()               // 读取布尔值

    // 工具功能
    toByteArray()               // 转换为字节数组
    getReadableBytes()          // 获取剩余可读字节数
}
```

## 2.3 开始编写代码

### 步骤1: 创建类和字段

```java
package protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PacketBuffer {
    // 用于写入的缓冲区
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream output;

    // 用于读取的缓冲区
    private byte[] readBuffer;
    private int readIndex;

    // 构造函数1: 用于写入
    public PacketBuffer() {
        this.buffer = new ByteArrayOutputStream();
        this.output = new DataOutputStream(buffer);
    }

    // 构造函数2: 用于读取
    public PacketBuffer(byte[] data) {
        this.buffer = null;
        this.output = null;
        this.readBuffer = data;
        this.readIndex = 0;
    }
}
```

**详细解释**:

1. **ByteArrayOutputStream**:
   - 这是一个可以自动扩展的字节数组
   - 就像一个可以无限放东西的箱子
   - 写入数据时自动增长

2. **DataOutputStream**:
   - 提供方便的方法写入各种数据类型
   - 例如: writeInt(), writeLong() 等

3. **readBuffer 和 readIndex**:
   - readBuffer: 存储要读取的数据
   - readIndex: 当前读取到哪个位置了

4. **两个构造函数**:
   - 无参构造: 创建一个空的缓冲区,用于写入
   - 有参构造: 传入字节数组,用于读取

### 步骤2: 实现 VarInt 写入

```java
public void writeVarInt(int value) throws IOException {
    // 循环处理,直到所有位都处理完
    while ((value & -128) != 0) {
        // value & 127: 取低7位
        // | 128: 设置最高位为1(表示还有后续字节)
        output.writeByte(value & 127 | 128);

        // 无符号右移7位,处理下一组
        value >>>= 7;
    }

    // 写入最后一个字节(最高位为0)
    output.writeByte(value);
}
```

**逐行解释**:

```java
while ((value & -128) != 0)
```
- `value & -128`: 检查第8位及以上是否有1
- `-128` 的二进制是 `10000000 00000000 00000000 00000000`
- 如果结果不为0,说明还有数据需要处理

```java
output.writeByte(value & 127 | 128);
```
- `value & 127`: 取低7位 (127 = 01111111)
- `| 128`: 设置最高位为1 (128 = 10000000)
- 写入这个字节

```java
value >>>= 7;
```
- `>>>`: 无符号右移
- 右移7位,准备处理下一组

**实际例子**:

```java
写入 300:

初始: value = 300
二进制: 00000000 00000000 00000001 00101100

第一次循环:
value & -128 = 256 (不为0,继续)
value & 127 = 44 (00101100)
value & 127 | 128 = 172 (10101100)
写入: 172
value >>>= 7 → value = 2

第二次循环:
value & -128 = 0 (退出循环)
写入: 2

结果: [172, 2]
```

### 步骤3: 实现 VarInt 读取

```java
public int readVarInt() throws IOException {
    int value = 0;       // 最终结果
    int position = 0;    // 当前处理的位位置
    byte currentByte;

    while (true) {
        // 读取一个字节
        currentByte = readByte();

        // 取低7位,放到对应位置
        value |= (currentByte & 127) << position;

        // 检查最高位
        if ((currentByte & 128) == 0) {
            // 最高位是0,结束
            break;
        }

        // 移动到下一组7位
        position += 7;

        // 防止无限循环
        if (position >= 32) {
            throw new RuntimeException("VarInt is too big");
        }
    }

    return value;
}
```

**逐行解释**:

```java
value |= (currentByte & 127) << position;
```
- `currentByte & 127`: 取低7位
- `<< position`: 左移到对应位置
- `|=`: 合并到结果中

```java
if ((currentByte & 128) == 0)
```
- `& 128`: 检查最高位
- 如果是0,说明这是最后一个字节

**实际例子**:

```java
读取 [172, 2]:

初始: value = 0, position = 0

第一次循环:
currentByte = 172 (10101100)
currentByte & 127 = 44 (00101100)
value |= 44 << 0 = 44
currentByte & 128 = 128 (不为0,继续)
position = 7

第二次循环:
currentByte = 2 (00000010)
currentByte & 127 = 2
value |= 2 << 7 = 44 | 256 = 300
currentByte & 128 = 0 (结束)

结果: 300
```

### 步骤4: 实现字符串写入

```java
public void writeString(String value) throws IOException {
    // 1. 将字符串转换为 UTF-8 字节数组
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

    // 2. 写入长度(使用 VarInt)
    writeVarInt(bytes.length);

    // 3. 写入字节数据
    output.write(bytes);
}
```

**为什么这样做?**

1. **UTF-8 编码**:
   - 支持所有 Unicode 字符
   - 英文字符占1字节,中文字符占3字节
   - 例如: "Hello" → [72, 101, 108, 108, 111]

2. **先写长度**:
   - 接收方需要知道要读取多少字节
   - 使用 VarInt 节省空间

3. **再写数据**:
   - 直接写入字节数组

**实际例子**:

```java
writeString("Hi");

步骤1: "Hi" → [72, 105]
步骤2: 长度 = 2, writeVarInt(2) → [2]
步骤3: 写入 [72, 105]

最终结果: [2, 72, 105]
```

### 步骤5: 实现字符串读取

```java
public String readString() throws IOException {
    // 1. 读取长度
    int length = readVarInt();

    // 2. 读取字节数据
    byte[] bytes = new byte[length];
    System.arraycopy(readBuffer, readIndex, bytes, 0, length);
    readIndex += length;

    // 3. 转换为字符串
    return new String(bytes, StandardCharsets.UTF_8);
}
```

**逐行解释**:

```java
int length = readVarInt();
```
- 先读取长度,知道要读多少字节

```java
System.arraycopy(readBuffer, readIndex, bytes, 0, length);
```
- `System.arraycopy`: 复制数组
- 从 `readBuffer[readIndex]` 开始
- 复制 `length` 个字节到 `bytes`

```java
readIndex += length;
```
- 移动读取位置

```java
return new String(bytes, StandardCharsets.UTF_8);
```
- 将字节数组转换为字符串

### 步骤6: 实现基本类型读写

```java
// ========== 写入方法 ==========

public void writeByte(int value) throws IOException {
    output.writeByte(value);
}

public void writeShort(int value) throws IOException {
    output.writeShort(value);
}

public void writeInt(int value) throws IOException {
    output.writeInt(value);
}

public void writeLong(long value) throws IOException {
    output.writeLong(value);
}

public void writeFloat(float value) throws IOException {
    output.writeFloat(value);
}

public void writeDouble(double value) throws IOException {
    output.writeDouble(value);
}

public void writeBoolean(boolean value) throws IOException {
    output.writeBoolean(value);
}

public void writeBytes(byte[] bytes) throws IOException {
    output.write(bytes);
}
```

**为什么这么简单?**

因为 `DataOutputStream` 已经提供了这些方法,我们只是封装一下。

### 步骤7: 实现基本类型读取

```java
// ========== 读取方法 ==========

public byte readByte() throws IOException {
    return readBuffer[readIndex++];
}

public short readShort() throws IOException {
    // 读取2个字节,组合成 short
    short value = (short) (
        ((readBuffer[readIndex] & 0xFF) << 8) |
        (readBuffer[readIndex + 1] & 0xFF)
    );
    readIndex += 2;
    return value;
}

public int readInt() throws IOException {
    // 读取4个字节,组合成 int
    int value = ((readBuffer[readIndex] & 0xFF) << 24) |
                ((readBuffer[readIndex + 1] & 0xFF) << 16) |
                ((readBuffer[readIndex + 2] & 0xFF) << 8) |
                (readBuffer[readIndex + 3] & 0xFF);
    readIndex += 4;
    return value;
}

public long readLong() throws IOException {
    // 读取8个字节,组合成 long
    long value = 0;
    for (int i = 0; i < 8; i++) {
        value = (value << 8) | (readBuffer[readIndex++] & 0xFF);
    }
    return value;
}

public float readFloat() throws IOException {
    // 先读取 int,再转换为 float
    return Float.intBitsToFloat(readInt());
}

public double readDouble() throws IOException {
    // 先读取 long,再转换为 double
    return Double.longBitsToDouble(readLong());
}

public boolean readBoolean() throws IOException {
    return readByte() != 0;
}

public byte[] readBytes(int length) throws IOException {
    byte[] bytes = new byte[length];
    System.arraycopy(readBuffer, readIndex, bytes, 0, length);
    readIndex += length;
    return bytes;
}
```

**详细解释 readShort**:

```java
short value = (short) (
    ((readBuffer[readIndex] & 0xFF) << 8) |
    (readBuffer[readIndex + 1] & 0xFF)
);
```

假设读取 [0x12, 0x34]:

```
第一个字节: 0x12
& 0xFF: 确保是正数 (0x12)
<< 8: 左移8位 (0x1200)

第二个字节: 0x34
& 0xFF: 确保是正数 (0x34)

合并: 0x1200 | 0x34 = 0x1234
```

### 步骤8: 实现工具方法

```java
// 转换为字节数组
public byte[] toByteArray() {
    return buffer.toByteArray();
}

// 获取剩余可读字节数
public int getReadableBytes() {
    return readBuffer.length - readIndex;
}
```

## 2.4 完整代码

```java
package protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PacketBuffer {
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream output;
    private byte[] readBuffer;
    private int readIndex;

    public PacketBuffer() {
        this.buffer = new ByteArrayOutputStream();
        this.output = new DataOutputStream(buffer);
    }

    public PacketBuffer(byte[] data) {
        this.buffer = null;
        this.output = null;
        this.readBuffer = data;
        this.readIndex = 0;
    }

    public void writeVarInt(int value) throws IOException {
        while ((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        output.writeByte(value);
    }

    public int readVarInt() throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & 127) << position;

            if ((currentByte & 128) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        output.write(bytes);
    }

    public String readString() throws IOException {
        int length = readVarInt();
        byte[] bytes = new byte[length];
        System.arraycopy(readBuffer, readIndex, bytes, 0, length);
        readIndex += length;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeShort(int value) throws IOException {
        output.writeShort(value);
    }

    public short readShort() throws IOException {
        short value = (short) (((readBuffer[readIndex] & 0xFF) << 8) | (readBuffer[readIndex + 1] & 0xFF));
        readIndex += 2;
        return value;
    }

    public void writeByte(int value) throws IOException {
        output.writeByte(value);
    }

    public byte readByte() throws IOException {
        return readBuffer[readIndex++];
    }

    public void writeBytes(byte[] bytes) throws IOException {
        output.write(bytes);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        System.arraycopy(readBuffer, readIndex, bytes, 0, length);
        readIndex += length;
        return bytes;
    }

    public void writeLong(long value) throws IOException {
        output.writeLong(value);
    }

    public long readLong() throws IOException {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (readBuffer[readIndex++] & 0xFF);
        }
        return value;
    }

    public void writeInt(int value) throws IOException {
        output.writeInt(value);
    }

    public int readInt() throws IOException {
        int value = ((readBuffer[readIndex] & 0xFF) << 24) |
                    ((readBuffer[readIndex + 1] & 0xFF) << 16) |
                    ((readBuffer[readIndex + 2] & 0xFF) << 8) |
                    (readBuffer[readIndex + 3] & 0xFF);
        readIndex += 4;
        return value;
    }

    public void writeBoolean(boolean value) throws IOException {
        output.writeBoolean(value);
    }

    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    public void writeFloat(float value) throws IOException {
        output.writeFloat(value);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void writeDouble(double value) throws IOException {
        output.writeDouble(value);
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

    public int getReadableBytes() {
        return readBuffer.length - readIndex;
    }
}
```

## 2.5 测试 PacketBuffer

让我们写一个测试程序:

```java
public class TestPacketBuffer {
    public static void main(String[] args) throws IOException {
        // 测试写入
        PacketBuffer writeBuffer = new PacketBuffer();

        writeBuffer.writeVarInt(300);
        writeBuffer.writeString("Hello");
        writeBuffer.writeInt(12345);
        writeBuffer.writeBoolean(true);

        byte[] data = writeBuffer.toByteArray();

        System.out.println("写入的数据:");
        for (byte b : data) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println();

        // 测试读取
        PacketBuffer readBuffer = new PacketBuffer(data);

        int varInt = readBuffer.readVarInt();
        String string = readBuffer.readString();
        int integer = readBuffer.readInt();
        boolean bool = readBuffer.readBoolean();

        System.out.println("读取的数据:");
        System.out.println("VarInt: " + varInt);
        System.out.println("String: " + string);
        System.out.println("Int: " + integer);
        System.out.println("Boolean: " + bool);
    }
}
```

**预期输出**:

```
写入的数据:
172 2 5 72 101 108 108 111 0 0 48 57 1

读取的数据:
VarInt: 300
String: Hello
Int: 12345
Boolean: true
```

**数据解析**:
- `172 2`: VarInt(300)
- `5`: 字符串长度
- `72 101 108 108 111`: "Hello" 的 UTF-8 编码
- `0 0 48 57`: int(12345)
- `1`: boolean(true)

## 2.6 小结

**我们实现了什么?**

1. ✅ VarInt 编码/解码
2. ✅ 字符串编码/解码
3. ✅ 所有基本数据类型的读写
4. ✅ 字节数组操作

**PacketBuffer 的作用**:
- 提供统一的数据读写接口
- 处理字节序(大端序)
- 支持 Minecraft 协议的特殊编码

**下一章预告**:
我们将实现数据包接口和具体的数据包类!

---

## 练习题

1. 写一个程序,使用 PacketBuffer 写入你的名字和年龄,然后读取出来
2. VarInt 编码数字 1000 的结果是什么?
3. 为什么读取 int 时要用 `& 0xFF`?

**答案**:
1. 参考测试程序,替换数据即可
2. 1000 = 0x3E8, VarInt 编码是 [232, 7]
3. 因为 Java 的 byte 是有符号的(-128~127),`& 0xFF` 可以转换为无符号(0~255)
