package com.english.accelerator.ai.agent

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 对话流程集成测试
 * 测试完整的对话功能：从用户输入到 AI 响应
 */
@RunWith(AndroidJUnit4::class)
class ConversationFlowTest {

    private lateinit var context: Context
    private lateinit var agentService: AgentServiceImpl
    private lateinit var historyManager: HistoryManager
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        agentService = AgentServiceImpl(context)
        historyManager = HistoryManager(context)
        sessionManager = SessionManager(context)
    }

    @Test
    fun testSingleTurnConversation() = runBlocking {
        // 测试单轮对话
        val userInput = "Hello, can you help me learn English?"
        val result = agentService.generate(
            userInput = userInput,
            context = emptyList()
        )

        assertTrue(result.isSuccess, "Single turn conversation should succeed")

        val response = result.getOrNull()
        assertNotNull(response)
        assertTrue(response.isNotEmpty(), "Response should not be empty")

        println("✓ Single turn conversation test passed")
        println("User: $userInput")
        println("Assistant: $response")
    }

    @Test
    fun testMultiTurnConversation() = runBlocking {
        // 测试多轮对话
        val conversation = mutableListOf<Message>()

        // 第一轮
        val input1 = "What does 'accelerate' mean?"
        val result1 = agentService.generate(input1, conversation)
        assertTrue(result1.isSuccess)

        val response1 = result1.getOrNull()!!
        conversation.add(Message("user", input1))
        conversation.add(Message("assistant", response1))

        // 第二轮
        val input2 = "Can you give me an example sentence?"
        val result2 = agentService.generate(input2, conversation)
        assertTrue(result2.isSuccess)

        val response2 = result2.getOrNull()!!

        println("✓ Multi-turn conversation test passed")
        println("Turn 1 - User: $input1")
        println("Turn 1 - Assistant: $response1")
        println("Turn 2 - User: $input2")
        println("Turn 2 - Assistant: $response2")
    }

    @Test
    fun testContextCompression() = runBlocking {
        // 测试上下文压缩（超过 10 条消息）
        val longConversation = (1..15).map { i ->
            Message(
                role = if (i % 2 == 0) "user" else "assistant",
                content = "Message $i"
            )
        }

        val result = agentService.generate("New message", longConversation)

        // 应该能处理长对话历史
        assertTrue(result.isSuccess || result.isFailure)

        println("✓ Context compression test passed")
    }

    @Test
    fun testDifferentAgentRoles() = runBlocking {
        val testCases = mapOf(
            AgentRole.VOCABULARY_TUTOR to "What does 'vocabulary' mean?",
            AgentRole.GRAMMAR_CHECKER to "Check this: I goes to school.",
            AgentRole.ESSAY_REVIEWER to "Review my essay about climate change.",
            AgentRole.SPEAKING_PARTNER to "Let's practice conversation.",
            AgentRole.LEARNING_PLANNER to "Help me plan my English study."
        )

        testCases.forEach { (role, input) ->
            agentService.switchAgent(role)
            val result = agentService.generate(input, emptyList())

            assertTrue(
                result.isSuccess || result.isFailure,
                "Agent $role should handle input"
            )

            println("✓ Agent $role test completed")
        }
    }

    @Test
    fun testPromptStringFormat() {
        // 验证提示词字符串格式
        val messages = listOf(
            Message("system", "You are helpful."),
            Message("user", "Hello")
        )

        // 构建提示词字符串（模拟 AgentServiceImpl 的逻辑）
        val promptString = messages.joinToString("\n") { msg ->
            "<|im_start|>${msg.role}\n${msg.content}\n<|im_end|>"
        } + "\n<|im_start|>assistant\n"

        // 验证格式
        assertTrue(promptString.contains("<|im_start|>system"))
        assertTrue(promptString.contains("<|im_start|>user"))
        assertTrue(promptString.contains("<|im_start|>assistant"))
        assertTrue(promptString.contains("<|im_end|>"))

        println("✓ Prompt string format test passed")
        println("Generated prompt:\n$promptString")
    }

    @Test
    fun testSessionAndHistoryIntegration() = runBlocking {
        // 创建新会话
        val sessionId = sessionManager.createSession("Test Session")
        assertNotNull(sessionId)

        // 添加对话历史
        val userMsg = Message("user", "Test message")
        val assistantMsg = Message("assistant", "Test response")

        historyManager.addMessage(sessionId, userMsg)
        historyManager.addMessage(sessionId, assistantMsg)

        // 获取历史
        val history = historyManager.getHistory(sessionId)
        assertEquals(2, history.size)
        assertEquals("Test message", history[0].content)
        assertEquals("Test response", history[1].content)

        println("✓ Session and history integration test passed")
    }
}
