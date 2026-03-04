# 正确性属性

*属性是在系统所有有效执行中都应成立的特征或行为——即关于系统应做什么的形式化陈述。属性是人类可读规范与机器可验证正确性保证之间的桥梁。*

## 单词学习模块

### 属性 1: 单词列表加载一致性
*对于任意* WordRepository 中存储的单词列表，调用 loadTodayWords() 后，VocabularyViewModel 的 UI 状态应包含与仓库中完全相同的单词集合。

**Validates: Requirements 1.1**

### 属性 2: WordCard 内容完整性
*对于任意* Word 对象，渲染出的 WordCard 正面应包含该单词的 word 字段和 phonetic 字段，背面应包含 translation 字段和 exampleSentence 字段。

**Validates: Requirements 1.4**

### 属性 3: WordCard 双击翻转回正面（Round-Trip）
*对于任意* 处于背面状态的 WordCard，再次点击后应回到正面状态，即翻转操作是自逆的。

**Validates: Requirements 1.6**

### 属性 4: 标记已学更新仓库状态
*对于任意* 有效的 wordId，调用 markWordAsLearned(wordId) 后，WordRepository 中对应单词的 status 应变为 LEARNED。

**Validates: Requirements 3.1**

### 属性 5: 学习进度计数正确性
*对于任意* 单词列表，VocabularyScreen 显示的已学数量应等于列表中 status 为 LEARNED 的单词数量，总数应等于列表长度。

**Validates: Requirements 3.4**

---

## 口语训练模块

### 属性 6: 语音识别触发完整性
*对于任意* 非空音频数据，停止录音后 ASRService.transcribe() 应被调用一次，且返回 Result 对象（成功或失败）。

**Validates: Requirements 5.3**

### 属性 7: 用户发言添加到对话列表
*对于任意* 非空转录文本，processUserInput() 执行后，对话列表中应新增一条 role 为 USER、content 等于该转录文本的 ConversationTurn。

**Validates: Requirements 5.4**

### 属性 8: AI 回复非空性
*对于任意* 非空用户输入和对话历史，LLMService.generateConversationResponse() 在模型已加载时应返回 Result.Success，且 text 字段非空。

**Validates: Requirements 6.1**

### 属性 9: AI 回复添加到对话列表
*对于任意* 成功的 LLM 回复文本，对话列表中应新增一条 role 为 ASSISTANT、content 等于该回复文本的 ConversationTurn。

**Validates: Requirements 6.2**

### 属性 10: 对话历史渲染完整性
*对于任意* 对话历史列表，SpeakingScreen 渲染后应展示所有 ConversationTurn，且每条消息的角色标签（用户/AI）与 turn.role 一致。

**Validates: Requirements 7.1**

### 属性 11: 对话历史持久化 Round-Trip
*对于任意* ConversationTurn，将其保存到 ConversationRepository 后，通过 conversationId 查询应能取回该 turn 的完整内容。

**Validates: Requirements 7.3**

---

## 写作练习模块

### 属性 12: 文本状态同步
*对于任意* 字符串输入，调用 WritingViewModel.onTextChanged(text) 后，uiState.content 应等于该输入字符串。

**Validates: Requirements 8.2**

### 属性 13: 作文保存 Round-Trip
*对于任意* 非空作文内容，调用 saveEssay() 后，通过 EssayRepository.getEssayById() 取回的 content 应与保存时的内容相同。

**Validates: Requirements 8.3**

### 属性 14: 语法错误索引有效性
*对于任意* 文本字符串，GrammarChecker.checkGrammar(text) 返回的所有 GrammarError 必须满足：0 ≤ startIndex < endIndex ≤ text.length，且 confidence 值在 [0.0, 1.0] 范围内。

**Validates: Requirements 9.3**

### 属性 15: 语法错误列表有序性
*对于任意* 文本字符串，GrammarChecker.checkGrammar(text) 返回的错误列表应按 startIndex 升序排列。

**Validates: Requirements 9.4**

### 属性 16: 采纳建议后文本替换正确性
*对于任意* 文本和其中一个 GrammarError 及其建议，调用 acceptSuggestion(suggestion) 后，新文本中 [startIndex, endIndex) 范围的内容应被替换为 suggestion.text，其余部分保持不变。

**Validates: Requirements 10.2**

### 属性 17: 采纳建议后错误从列表移除
*对于任意* GrammarError 列表，采纳某个错误的建议后，该错误不应再出现在 uiState.grammarErrors 中。

**Validates: Requirements 10.3**

---

## AI模型服务

### 属性 18: LLM 推理 maxTokens 约束
*对于任意* 有效 prompt，LLMService 在模型已加载时应接受推理请求，且生成的 token 数量不超过 maxTokens（最大值 2048）。

**Validates: Requirements 11.4**
