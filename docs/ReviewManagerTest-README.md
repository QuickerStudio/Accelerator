# ReviewManager 单元测试

## 概述
本测试套件验证 `ReviewManager` 洗牌算法的正确性和边界条件处理。

## 测试覆盖

### 核心功能测试
1. **空列表处理**
   - 空新单词列表返回空结果
   - 没有未记住单词时返回原列表

2. **复习单词数量计算**
   - 正确按比例计算复习单词数量（默认 8:1）
   - 复习单词数量不超过未记住单词总数
   - 边界条件：新单词少于比例值时返回 0 个复习单词

3. **洗牌算法验证**
   - 复习单词被正确插入到新单词中
   - 复习单词不在前 2 个位置（避免开头插入）
   - 所有新单词都保留在结果中
   - 只过滤未记住的单词（已记住的不出现）

4. **辅助方法测试**
   - `hasReviewWords()` 正确判断是否有复习单词
   - `getUnmemorizedCount()` 正确返回未记住单词数量
   - `setReviewRatio()` 正确设置和限制复习比例（3-20）

## 运行测试

### 使用 Android Studio
1. 打开项目
2. 右键点击 `ReviewManagerTest.kt`
3. 选择 "Run 'ReviewManagerTest'"

### 使用命令行
```bash
# 运行所有单元测试
./gradlew test

# 只运行 ReviewManagerTest
./gradlew test --tests "com.english.accelerator.data.ReviewManagerTest"

# 运行特定测试方法
./gradlew test --tests "com.english.accelerator.data.ReviewManagerTest.shuffleWithReviewWords - 正确计算复习单词数量"
```

### 查看测试报告
测试完成后，报告位于：
```
app/build/reports/tests/testDebugUnitTest/index.html
```

## 测试依赖

```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
```

## 测试用例详解

### 1. 正确计算复习单词数量
```kotlin
// Given: 50 个新单词，比例 8:1
val newWords = (1..50).map { createWord(it, "word$it") }

// When: 执行洗牌
val result = ReviewManager.shuffleWithReviewWords(newWords)

// Then: 应该有 50 + 6 = 56 个单词
assertEquals(56, result.size)
```

**预期行为**：
- 50 ÷ 8 = 6.25，向下取整 = 6
- 从未记住池中随机选 6 个单词
- 随机插入到 50 个新单词中

### 2. 复习单词不在前 2 个位置
```kotlin
// When: 执行洗牌
val result = ReviewManager.shuffleWithReviewWords(newWords)

// Then: 复习单词索引 >= 2
val reviewWordIndex = result.indexOf(reviewWord)
assertTrue(reviewWordIndex >= 2)
```

**预期行为**：
- 避免复习单词出现在最开始
- 让用户先看到新单词，建立学习节奏

### 3. 边界条件：复习单词数量限制
```kotlin
// Given: 50 个新单词，但只有 3 个未记住单词
val newWords = (1..50).map { createWord(it, "word$it") }
val unmemorizedWords = (51..53).map { createWord(it, "review$it") }

// When: 执行洗牌
val result = ReviewManager.shuffleWithReviewWords(newWords)

// Then: 最多插入 3 个（不超过未记住单词总数）
assertEquals(53, result.size) // 50 + 3
```

**预期行为**：
- 计算值：50 ÷ 8 = 6
- 实际可用：3 个
- 取最小值：min(6, 3) = 3

## Mock 对象说明

### WordLearningManager
```kotlin
mockkObject(WordLearningManager)
every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
```
模拟学习记录管理器，返回测试用的学习记录。

### WordRepository
```kotlin
mockkObject(WordRepository)
every { WordRepository.getWordById(id) } returns word
```
模拟词库，根据 ID 返回测试用的单词对象。

### SharedPreferences
```kotlin
every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPreferences
```
模拟 Android 持久化存储，避免依赖真实文件系统。

## 测试数据

### Word 对象结构
```kotlin
Word(
    id = 1,
    word = "test",
    phonetic = "/test/",
    translation = "测试",
    example = "Example",
    frequency = 100,
    level = "CET4",
    pos = "n."
)
```

### WordLearningRecord 对象结构
```kotlin
WordLearningRecord(
    wordId = 1,
    word = "test",
    isMemorized = false,  // false = 未记住
    timestamp = System.currentTimeMillis(),
    isImportant = false
)
```

## 预期测试结果

所有 15 个测试用例应该全部通过：

```
✓ shuffleWithReviewWords - 空新单词列表返回空列表
✓ shuffleWithReviewWords - 没有未记住单词时返回原列表
✓ shuffleWithReviewWords - 正确计算复习单词数量
✓ shuffleWithReviewWords - 复习单词数量不超过未记住单词总数
✓ shuffleWithReviewWords - 复习单词被正确插入
✓ shuffleWithReviewWords - 所有新单词都保留在结果中
✓ shuffleWithReviewWords - 边界条件：新单词少于 2 个
✓ shuffleWithReviewWords - 只过滤未记住的单词
✓ hasReviewWords - 有未记住单词时返回 true
✓ hasReviewWords - 没有未记住单词时返回 false
✓ getUnmemorizedCount - 正确返回未记住单词数量
✓ setReviewRatio - 正确设置复习比例
✓ setReviewRatio - 限制最小值为 3
✓ setReviewRatio - 限制最大值为 20

Total: 15 tests, 15 passed
```

## 故障排查

### 常见问题

1. **MockK 初始化失败**
   ```
   解决：确保 build.gradle.kts 中添加了 mockk 依赖
   ```

2. **Android 依赖找不到**
   ```
   解决：使用 @RunWith(AndroidJUnit4::class) 或 Robolectric
   本测试使用 MockK 模拟所有 Android 依赖，无需真实环境
   ```

3. **测试超时**
   ```
   解决：检查是否有死循环或无限递归
   ```

## 维护指南

### 添加新测试
1. 在 `ReviewManagerTest` 类中添加新的 `@Test` 方法
2. 使用描述性的测试名称（中文或英文）
3. 遵循 Given-When-Then 模式
4. 添加清晰的注释说明测试目的

### 更新测试
当 `ReviewManager` 算法改变时：
1. 更新相关测试用例的预期结果
2. 添加新的边界条件测试
3. 更新本文档的说明

## 参考资料

- [JUnit 4 文档](https://junit.org/junit4/)
- [MockK 文档](https://mockk.io/)
- [Android 单元测试指南](https://developer.android.com/training/testing/unit-testing)
