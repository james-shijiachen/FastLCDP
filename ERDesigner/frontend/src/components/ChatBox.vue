<template>
  <div class="chatbox-root">
    <div class="chatbox-header">
      <span class="chatbox-title">∞ Agent</span>
    </div>
    <div class="chatbox-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="msg-avatar">
          <span v-if="msg.role === 'user'">🧑</span>
          <span v-else>🤖</span>
        </div>
        <div class="msg-bubble">
          <span class="msg-content">{{ msg.content }}</span>
        </div>
      </div>
    </div>
    <div class="chatbox-input-row">
      <input v-model="input" class="chatbox-input" type="text" placeholder="Type a message..." @keydown.enter="send" />
      <button class="chatbox-send-btn" @click="send">
        <svg width="20" height="20" viewBox="0 0 20 20"><path d="M2 10l15-7-4 7 4 7z" fill="#fff"/></svg>
      </button>
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
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f8fafc;
  border-radius: 0 0 12px 12px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.04);
}
.chatbox-header {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #6366f1;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
.chatbox-title {
  letter-spacing: 1px;
}
.chatbox-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px 16px 8px 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.chat-msg {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg .msg-avatar {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  background: #e0e7ff;
  border-radius: 50%;
}
.chat-msg.agent .msg-avatar {
  background: #bae6fd;
}
.msg-bubble {
  max-width: 70%;
  background: #fff;
  border-radius: 12px;
  padding: 8px 14px;
  font-size: 15px;
  color: #222;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  word-break: break-all;
}
.chat-msg.user .msg-bubble {
  background: #6366f1;
  color: #fff;
  border-bottom-right-radius: 4px;
  border-bottom-left-radius: 12px;
  border-top-right-radius: 12px;
  border-top-left-radius: 12px;
}
.chat-msg.agent .msg-bubble {
  background: #fff;
  color: #222;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 12px;
  border-top-right-radius: 12px;
  border-top-left-radius: 12px;
}
.chatbox-input-row {
  display: flex;
  align-items: center;
  padding: 12px 16px 12px 20px;
  border-top: 1px solid #e4e7ed;
  background: #f8fafc;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}
.chatbox-input {
  flex: 1;
  background: #fff;
  border: 1px solid #e4e7ed;
  color: #222;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 15px;
  outline: none;
  margin-right: 8px;
}
.chatbox-send-btn {
  background: #6366f1;
  border: none;
  border-radius: 8px;
  width: 36px;
  height: 36px;
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