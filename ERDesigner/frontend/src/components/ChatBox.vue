<template>
  <div class="chatbox-root" @wheel.prevent="handleModalWheel">
    <div class="chatbox-header">
      <h3>Agent</h3>
      <button class="chatbox-send-btn" @click="send">
        <svg width="20" height="20" viewBox="0 0 20 20"><path d="M2 10l15-7-4 7 4 7z" fill="#fff"/></svg>
      </button>
    </div>
    <div class="chatbox-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="msg-bubble">
          <span class="msg-content">{{ msg.content }}</span>
        </div>
      </div>
    </div>
    <div class="chatbox-input-row">
      <textarea v-model="input" class="chatbox-input" type="text" placeholder="Type a message..."/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
const input = ref('')
const messages = ref([
  { role: 'agent', content: 'Hi! How can I help you?' }
])
const messagesRef = ref<HTMLElement | null>(null)

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = messagesRef.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
    container.scrollTop += event.deltaY; // 纵向滚动
  }
}

function send() {
  if (!input.value.trim()) return
  messages.value.push({ role: 'user', content: input.value })
  input.value = ''
  // 模拟 AI 回复
  setTimeout(() => {
    messages.value.push({ role: 'agent', content: 'This is a demo reply.' })
    scrollToBottom()
  }, 600)
  scrollToBottom()
}
function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  })
}
</script>

<style scoped>
.chatbox-root {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}
.chatbox-root, .chatbox-root * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
}
.dark-theme .chatbox-root {
  background: #1a1a1a;
  color: #ffffff;
  height: 100%;
}
.chatbox-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  border-bottom: 1px solid #e1e4e8;
  background: #f6f8fa;
}
.dark-theme .chatbox-header {
  background: #2a2a2a;
  border-bottom: 1px solid #404040;
}
.chatbox-header h3 {
  margin: 0;
  font-size: 20px;
  color: #24292e;
}
.dark-theme .chatbox-header h3 {
  color: #ffffff;
}

/* 消息气泡 */
.chatbox-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px 10px 0 10px;
  display: flex;
  flex-direction: column;
}
.chat-msg {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.msg-bubble {
  width: 100%;
  border-radius: 6px;
  padding: 8px;
  font-size: 13px;
  word-break: break-all;
}
/* 用户消息气泡 */
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg.user .msg-bubble {
  color: #080808;
  border-radius: 4px;
  background:#ebebec;
}
.dark-theme .chat-msg.user .msg-bubble {
  background: #2e2e2e;
  color: #9e9d9d;
}
/* 机器人消息气泡 */
.chat-msg.agent .msg-bubble {
  color: #464647;
}
.dark-theme .chat-msg.agent .msg-bubble {
  color: #fff;
}
/* 输入框 */
.chatbox-input-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: #fff;
}
.dark-theme .chatbox-input-row {
  background: #1a1a1a;
}
/* 发送按钮 */
.chatbox-send-btn {
  background: #6366f1;
  border: none;
  border-radius: 8px;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}
.chatbox-send-btn:hover {
  background: #818cf8;
}
</style> 