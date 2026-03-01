# 通用组件库

## 1. 通用按钮 (AppButton)

```kotlin
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: ImageVector? = null
)

enum class ButtonVariant {
    Primary,    // 主按钮 - 渐变背景
    Secondary,  // 次要按钮 - 灰色背景
    Outline,    // 轮廓按钮 - 透明背景+边框
    Text        // 文本按钮 - 透明背景
}
```

---

## 2. 卡片组件 (AppCard)

```kotlin
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradient: Pair<Color, Color>? = null,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
)
```

---

## 3. 顶部标题栏 (AppTopBar)

```kotlin
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
)
```

---

## 4. 底部导航栏 (BottomNavigationBar)

```kotlin
@Composable
fun BottomNavigationBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
)

enum class NavigationTab {
    Vocabulary,  // 单词
    Writing,     // 写作
    Speaking,    // 对话
    Settings     // 设置
}
```

---

## 5. 进度指示器 (ProgressIndicator)

```kotlin
@Composable
fun ProgressIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    showText: Boolean = true
)
```

---

## 6. 对话气泡 (ChatBubble)

```kotlin
@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    timestamp: String,
    modifier: Modifier = Modifier
)
```
