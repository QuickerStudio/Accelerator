# 词库系统测试指南

## 问题修复

✅ **已修复**: "Method too large" 编译错误

**原因**: 5000 个单词在一个 `listOf()` 中会生成过大的静态初始化方法

**解决方案**:
- 将词库分成 10 个块，每块 500 个单词
- 使用 `lazy` 初始化，避免启动时性能问题
- 使用 spread operator (`*`) 合并所有块

## 在 Android Studio 中测试

### 1. 同步项目
```
File → Sync Project with Gradle Files
```
或点击工具栏的 🐘 图标

### 2. 清理并重新构建
```
Build → Clean Project
Build → Rebuild Project
```

### 3. 检查编译错误
- 查看 Build 窗口，确保没有错误
- 如果有错误，检查错误信息

### 4. 运行应用
- 点击绿色运行按钮 ▶️
- 选择模拟器或真机
- 等待应用启动

### 5. 测试词库功能

#### 测试点 1: 单词显示
- 进入单词学习界面
- 检查显示的单词是否是真实英语单词
- 预期结果: 应该看到 "as", "at", "be", "by", "do" 等高频词

#### 测试点 2: 单词信息
- 检查每个单词是否有：
  - ✅ 英文单词
  - ✅ 音标（如 /æz/）
  - ✅ 中文释义
  - ✅ 例句（目前是占位符）

#### 测试点 3: 滑动功能
- 向左滑动卡片 → 标记为"未记住"
- 向右滑动卡片 → 标记为"已记住"
- 检查 Toast 提示是否正常显示

#### 测试点 4: 学习记录
- 打开侧边栏
- 查看"学习日志"部分
- 检查是否记录了刚才学习的单词
- 预期结果: 应该看到单词出现在"今天"列表中

#### 测试点 5: 重点单词
- 长按单词卡片
- 检查是否标记为重点单词
- 打开侧边栏，查看"重点单词"列表
- 预期结果: 长按的单词应该出现在重点单词列表中

### 6. 性能测试

#### 启动时间
- 冷启动应用
- 观察启动速度
- 预期结果: 由于使用了 `lazy` 初始化，启动应该很快

#### 内存占用
- 在 Android Studio 的 Profiler 中查看内存使用
- 预期结果: 词库数据只在首次访问时加载

## 词库统计信息

- **总单词数**: 5000
- **CET4**: 2031 个
- **CET6**: 677 个
- **TOEFL**: 1309 个
- **GRE**: 983 个

## 前 50 个高频词

```
as, at, be, by, do, act, add, age, ago, aid
air, all, and, any, arm, art, ask, bad, big, bit
boy, but, buy, car, cup, cut, day, die, able, also
area, army, away, back, base, beat, bite, blue, body, book
both, call, care, club, come, cost, door, draw, drug, each
```

## 如果遇到问题

### 问题 1: 编译错误 "Unresolved reference: ecdictWords"
**解决方案**:
1. File → Invalidate Caches → Invalidate and Restart
2. 重新同步 Gradle

### 问题 2: 应用崩溃
**解决方案**:
1. 查看 Logcat 中的错误信息
2. 检查 WordRepository.init() 是否在 MainActivity.onCreate() 中被调用

### 问题 3: 显示的还是旧的测试数据
**解决方案**:
1. 卸载应用
2. 重新安装并运行

### 问题 4: 网络推送失败
**当前状态**: 本地 commit 已成功，但 push 到 GitHub 失败（网络问题）
**解决方案**: 稍后网络恢复后执行：
```bash
cd C:\Users\Quick\AndroidStudioProjects\Accelerator
git push origin master
```

## 技术细节

### 文件结构
```kotlin
// EcdictWords.kt

// 10 个私有分块
private val ecdictWordsChunk0 = listOf(...)  // 500 个单词
private val ecdictWordsChunk1 = listOf(...)  // 500 个单词
...
private val ecdictWordsChunk9 = listOf(...)  // 500 个单词

// 公开的 lazy 属性
val ecdictWords: List<Word> by lazy {
    listOf(
        *ecdictWordsChunk0.toTypedArray(),
        *ecdictWordsChunk1.toTypedArray(),
        ...
        *ecdictWordsChunk9.toTypedArray()
    )
}
```

### 为什么使用 lazy?
- 延迟初始化，只在首次访问时加载
- 避免应用启动时的性能开销
- 线程安全（默认使用 synchronized）

### 为什么分块?
- 避免单个方法过大（JVM 限制：64KB）
- 每个分块独立编译，不会触发 "Method too large" 错误
- 使用 spread operator 合并时性能损失很小

## 下一步计划

1. ✅ 修复编译错误
2. ⏳ 在 Android Studio 中测试
3. ⏳ 添加真实的例句（可选）
4. ⏳ 实现按等级筛选单词功能
5. ⏳ 添加单词搜索功能
