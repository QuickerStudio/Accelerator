# 核心算法

## 算法 1: 单词学习算法（间隔重复算法）

### 目的
根据复习次数和难度计算下次复习日期

### 伪代码

```kotlin
ALGORITHM calculateNextReviewDate(word: Word): LocalDate
INPUT: word - 包含学习历史的单词对象
OUTPUT: nextReviewDate - 下次复习日期

BEGIN
  ASSERT word != null
  ASSERT word.reviewCount >= 0
  
  // 步骤1: 根据复习次数计算间隔天数
  interval ← WHEN word.reviewCount IS
    0 → 1        // 第一次复习：1天后
    1 → 3        // 第二次复习：3天后
    2 → 7        // 第三次复习：7天后
    3 → 14       // 第四次复习：14天后
    4 → 30       // 第五次复习：30天后
    ELSE → 60    // 之后：60天后
  END WHEN
  
  // 步骤2: 根据难度调整间隔
  difficultyMultiplier ← WHEN word.difficulty IS
    EASY → 1.5
    MEDIUM → 1.0
    HARD → 0.7
    ADVANCED → 0.5
  END WHEN
  
  adjustedInterval ← interval * difficultyMultiplier
  
  // 步骤3: 根据用户表现调整
  IF word.status = DIFFICULT THEN
    adjustedInterval ← adjustedInterval * 0.5
  END IF
  
  // 步骤4: 计算下次复习日期
  baseDate ← word.lastReviewDate IF word.lastReviewDate != null ELSE LocalDate.now()
  nextReviewDate ← baseDate.plusDays(adjustedInterval.toInt())
  
  ASSERT nextReviewDate > baseDate
  
  RETURN nextReviewDate
END
```

### 前置条件
- word对象不为null
- word.reviewCount >= 0
- word.difficulty是有效的枚举值

### 后置条件
- 返回的日期晚于基准日期
- 间隔天数根据复习次数和难度合理调整
- 不修改输入word对象

---

## 算法 2: AI对话生成算法

### 目的
根据用户输入和对话历史生成AI回复

### 伪代码

```kotlin
ALGORITHM generateConversationResponse(
    userInput: String,
    conversationHistory: List<ConversationTurn>
): AIResponse
INPUT: userInput - 用户输入文本
       conversationHistory - 对话历史记录
OUTPUT: AIResponse - AI回复和反馈

BEGIN
  ASSERT userInput.isNotEmpty()
  ASSERT conversationHistory != null
  
  // 步骤1: 构建上下文窗口（最近N轮对话）
  contextWindowSize ← 10
  recentHistory ← conversationHistory.takeLast(contextWindowSize)
  
  // 步骤2: 分析用户输入
  grammarErrors ← analyzeGrammar(userInput)
  pronunciationHints ← extractPronunciationIssues(userInput, recentHistory)
  
  // 步骤3: 构建提示词
  systemPrompt ← """
    You are an English learning AI assistant.
    Provide natural, encouraging responses.
    Adapt difficulty to user's level.
  """
  
  messages ← [
    Message(role: SYSTEM, content: systemPrompt)
  ]
  
  // 添加历史对话到消息列表
  FOR EACH turn IN recentHistory DO
    ASSERT turn.content.isNotEmpty()
    messages.append(Message(role: turn.role, content: turn.content))
  END FOR
  
  messages.append(Message(role: USER, content: userInput))
  
  // 步骤4: 调用LLM生成回复
  aiText ← llmService.generateResponse(
    prompt: messages.last().content,
    context: messages,
    maxTokens: 256
  )
  
  // 步骤5: 生成反馈
  feedback ← SpeakingFeedback(
    pronunciation: calculatePronunciationScore(userInput, pronunciationHints),
    fluency: calculateFluencyScore(userInput),
    grammar: GrammarScore(
      score: IF grammarErrors.isEmpty() THEN 1.0 ELSE 0.8,
      errors: grammarErrors.map { it.message }
    ),
    suggestions: generateSuggestions(grammarErrors, pronunciationHints)
  )
  
  // 步骤6: 构建响应
  response ← AIResponse(
    text: aiText,
    feedback: feedback,
    shouldContinue: true
  )
  
  ASSERT response.text.isNotEmpty()
  ASSERT response.feedback != null
  
  RETURN response
END
```

### 前置条件
- userInput不为空
- conversationHistory已初始化（可以为空列表）
- LLM服务可用
- 所有历史turn的content不为空

### 后置条件
- 返回有效的AIResponse对象
- response.text不为空
- response.feedback包含有效的评分（0.0-1.0）
- 不修改输入参数

### 循环不变式
- 在遍历recentHistory时，所有已添加到messages的turn都有有效的content
- messages列表保持角色交替（USER/ASSISTANT）的顺序

---

## 算法 3: 语法检查算法

### 目的
使用LLM检查文本中的语法错误

### 伪代码

```kotlin
ALGORITHM checkGrammarWithLLM(text: String): List<GrammarError>
INPUT: text - 待检查的文本
OUTPUT: errors - 语法错误列表

BEGIN
  ASSERT text != null
  ASSERT text.length <= 10000
  
  errors ← empty list
  
  // 步骤1: 文本预处理
  IF text.isEmpty() THEN
    RETURN errors
  END IF
  
  sentences ← splitIntoSentences(text)
  
  // 步骤2: 逐句分析（批处理优化）
  batchSize ← 5
  FOR i ← 0 TO sentences.length STEP batchSize DO
    ASSERT i >= 0 AND i < sentences.length
    
    batch ← sentences.slice(i, min(i + batchSize, sentences.length))
    batchText ← batch.join(" ")
    
    // 构建语法检查提示词
    prompt ← """
      Analyze the following text for grammar errors.
      Return JSON array with: type, start, end, message, suggestions.
      Text: ${batchText}
    """
    
    // 调用LLM
    llmResponse ← llmService.generateResponse(
      prompt: prompt,
      context: [],
      maxTokens: 512
    )
    
    // 解析LLM响应
    batchErrors ← parseGrammarErrors(llmResponse, i)
    
    // 添加到总错误列表
    FOR EACH error IN batchErrors DO
      ASSERT error.startIndex >= 0
      ASSERT error.endIndex <= text.length
      ASSERT error.startIndex < error.endIndex
      
      errors.append(error)
    END FOR
  END FOR
  
  // 步骤3: 去重和排序
  errors ← removeDuplicates(errors)
  errors ← sortByStartIndex(errors)
  
  ASSERT ALL error IN errors: error.startIndex < error.endIndex
  ASSERT errors is sorted by startIndex
  
  RETURN errors
END
```

### 前置条件
- text不为null
- text长度不超过10000字符
- LLM服务可用

### 后置条件
- 返回的错误列表按startIndex排序
- 所有错误的索引在text范围内
- 所有错误的startIndex < endIndex
- 无重复错误

### 循环不变式
- 在批处理循环中，i始终是有效的句子索引
- 所有已处理的错误都有有效的索引范围
- errors列表中的所有错误都来自已处理的批次

---

## 算法 4: 音频录制和转录

### 目的
录制音频并使用Whisper转录为文本

### 伪代码

```kotlin
ALGORITHM recordAndTranscribe(): String
INPUT: 无（从麦克风获取）
OUTPUT: transcribedText - 转录文本

BEGIN
  // 步骤1: 初始化音频录制
  audioRecorder ← AudioRecorder(
    sampleRate: 16000,
    channels: MONO,
    encoding: PCM_16BIT
  )
  
  audioBuffer ← empty ByteArray
  isRecording ← true
  
  // 步骤2: 开始录制
  audioRecorder.start()
  
  WHILE isRecording DO
    chunk ← audioRecorder.read(bufferSize: 4096)
    
    IF chunk != null THEN
      audioBuffer.append(chunk)
    END IF
    
    // 检查停止条件（外部触发）
    IF stopSignalReceived() THEN
      isRecording ← false
    END IF
  END WHILE
  
  // 步骤3: 停止录制
  audioRecorder.stop()
  audioRecorder.release()
  
  ASSERT audioBuffer.size > 0
  
  // 步骤4: 音频预处理
  processedAudio ← preprocessAudio(audioBuffer)
  
  // 步骤5: 调用Whisper模型转录
  transcribedText ← whisperService.transcribe(processedAudio)
  
  ASSERT transcribedText != null
  
  RETURN transcribedText
END
```

### 前置条件
- 麦克风权限已授予
- 音频录制器可用
- Whisper模型已加载

### 后置条件
- 返回转录文本（可能为空字符串）
- 音频录制器资源已释放
- audioBuffer包含有效的音频数据

### 循环不变式
- 在录制循环中，audioBuffer包含所有已录制的音频数据
- isRecording标志正确反映录制状态

---

## 算法 5: 本地LLM模型推理

### 目的
使用本地GGUF模型生成文本

### 伪代码

```kotlin
ALGORITHM runLLMInference(
    prompt: String,
    context: List<Message>,
    maxTokens: Int
): String
INPUT: prompt - 用户提示词
       context - 上下文消息列表
       maxTokens - 最大生成token数
OUTPUT: generatedText - 生成的文本

BEGIN
  ASSERT prompt.isNotEmpty()
  ASSERT maxTokens > 0 AND maxTokens <= 2048
  ASSERT modelLoaded = true
  
  // 步骤1: 构建完整输入
  fullPrompt ← buildPromptWithContext(context, prompt)
  
  // 步骤2: Tokenize输入
  inputTokens ← tokenizer.encode(fullPrompt)
  
  ASSERT inputTokens.size > 0
  
  // 步骤3: 准备模型输入张量
  inputTensor ← createTensor(inputTokens)
  
  // 步骤4: 执行推理（自回归生成）
  generatedTokens ← empty list
  currentInput ← inputTensor
  
  FOR i ← 0 TO maxTokens DO
    ASSERT generatedTokens.size = i
    
    // 前向传播
    logits ← model.forward(currentInput)
    
    // 采样下一个token
    nextToken ← sampleToken(
      logits: logits,
      temperature: 0.7,
      topK: 50,
      topP: 0.9
    )
    
    generatedTokens.append(nextToken)
    
    // 检查结束条件
    IF nextToken = EOS_TOKEN THEN
      BREAK
    END IF
    
    // 更新输入（添加新生成的token）
    currentInput ← appendToken(currentInput, nextToken)
  END FOR
  
  // 步骤5: Decode生成的tokens
  generatedText ← tokenizer.decode(generatedTokens)
  
  ASSERT generatedText.isNotEmpty()
  ASSERT generatedTokens.size <= maxTokens
  
  RETURN generatedText
END
```

### 前置条件
- prompt不为空
- maxTokens在有效范围内（1-2048）
- 模型已加载到内存
- tokenizer已初始化

### 后置条件
- 返回生成的文本
- 生成的token数不超过maxTokens
- 模型状态未被修改（无副作用）

### 循环不变式
- 在生成循环中，generatedTokens.size = i
- 所有已生成的tokens都是有效的词汇表索引
- currentInput始终包含原始输入加上所有已生成的tokens
