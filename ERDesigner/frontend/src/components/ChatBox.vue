<template>
  <div class="chatbox-root" @wheel.prevent="handleModalWheel">
    <div class="chatbox-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="msg-bubble">
          <div class="msg-content" v-html="msg.content"></div>
        </div>
      </div>
    </div>
    <!-- 上方工具栏 -->
    <div class="chatbox-top-toolbar">
      <button 
        class="toolbar-btn" 
        @click="insertImage">
        <Icon name="picture" style="width: 20px; height: 20px;"/>
        <span>图片</span>
      </button>
      <button 
        class="toolbar-btn" 
        @click="triggerMention">
        <Icon name="at" style="width: 20px; height: 20px;"/>
        <span>@提及</span>
      </button>
    </div>

    <!-- 输入框容器 -->
    <div class="chatbox-input-container">
      <div class="chatbox-input-wrapper">
        <editor-content 
          :editor="editor" 
          class="chatbox-editor"
          @keydown.enter.exact.prevent="send"
          @keydown.shift.enter.exact="insertLineBreak"
        />
        <button class="chatbox-send-btn" @click="send" :disabled="!canSend">
          <Icon name="send-prompt" style="width: 20px; height: 20px;"/>
        </button>
      </div>
    </div>

    <!-- 下方工具栏 -->
    <div class="chatbox-bottom-toolbar">
      <div class="model-selector">
        <label>模型:</label>
        <select v-model="selectedModel" class="model-dropdown">
          <option value="gpt-4">GPT-4</option>
          <option value="gpt-3.5-turbo">GPT-3.5 Turbo</option>
          <option value="claude-3">Claude-3</option>
          <option value="gemini-pro">Gemini Pro</option>
        </select>
      </div>
      <button 
        class="voice-btn" 
        @mousedown="startVoiceInput"
        @mouseup="stopVoiceInput"
        @mouseleave="stopVoiceInput"
        :class="{ recording: isRecording }"
        title="按住说话">
        <Icon name="microphone" style="width: 20px; height: 20px;"/>
        <span>{{ isRecording ? '松开结束' : '按住说话' }}</span>
      </button>
    </div>
    
    <!-- Mention 建议列表 -->
    <div 
      v-if="mentionSuggestions.length > 0" 
      class="mention-suggestions"
      :style="mentionPosition">
      <div 
        v-for="(suggestion, index) in mentionSuggestions" 
        :key="suggestion.id"
        :class="['mention-item', { active: index === selectedMentionIndex }]"
        @click="selectMention(suggestion)">
        <div class="mention-avatar">{{ suggestion.name.charAt(0) }}</div>
        <div class="mention-info">
          <div class="mention-name">{{ suggestion.name }}</div>
          <div class="mention-type">{{ suggestion.type }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Editor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Mention from '@tiptap/extension-mention'
import Placeholder from '@tiptap/extension-placeholder'
import Icon from '@/components/Icon.vue'

const { t: $t } = useI18n()
const messages = ref([
  { role: 'agent', content: 'Hi! How can I help you?' }
])
const messagesRef = ref<HTMLElement | null>(null)
const editor = ref<Editor>()
const mentionSuggestions = ref<any[]>([])
const selectedMentionIndex = ref(0)
const mentionPosition = ref({ top: '0px', left: '0px' })
const selectedModel = ref('gpt-4')
const isRecording = ref(false)
const recognition = ref<any>(null)

// 计算是否可以发送
const canSend = computed(() => {
  return editor.value && !editor.value.isEmpty
})

// 模拟的用户和文件数据
const mockData = [
  { id: 1, name: 'Alice Johnson', type: '用户', avatar: 'A' },
  { id: 2, name: 'Bob Smith', type: '用户', avatar: 'B' },
  { id: 3, name: 'Charlie Brown', type: '用户', avatar: 'C' },
  { id: 4, name: 'project.json', type: '文件', avatar: 'P' },
  { id: 5, name: 'config.ts', type: '文件', avatar: 'C' },
  { id: 6, name: 'README.md', type: '文件', avatar: 'R' },
]

// 初始化编辑器
onMounted(() => {
  initSpeechRecognition()
  editor.value = new Editor({
    extensions: [
      StarterKit.configure({
        link: {
          openOnClick: false,
        },
      }),
      Image.configure({
        inline: true,
        allowBase64: true,
      }),
      Placeholder.configure({
        placeholder: $t('chat.placeholder'),
      }),
      Mention.configure({
        HTMLAttributes: {
          class: 'mention',
        },
        suggestion: {
          items: ({ query }: { query: string }) => {
            return mockData.filter(item => 
              item.name.toLowerCase().includes(query.toLowerCase())
            ).slice(0, 5)
          },
          render: () => {
            let component: any
            let popup: any

            return {
              onStart: (props: any) => {
                mentionSuggestions.value = props.items
                selectedMentionIndex.value = 0
                
                // 计算弹出位置
                 const { range } = props
                 const editorElement = document.querySelector('.chatbox-editor')
                 if (editorElement) {
                   const rect = editorElement.getBoundingClientRect()
                   mentionPosition.value = {
                     top: `${rect.bottom + 5}px`,
                     left: `${rect.left}px`
                   }
                 }
              },
              onUpdate: (props: any) => {
                mentionSuggestions.value = props.items
                selectedMentionIndex.value = 0
              },
              onKeyDown: (props: any) => {
                if (props.event.key === 'ArrowUp') {
                  selectedMentionIndex.value = Math.max(0, selectedMentionIndex.value - 1)
                  return true
                }
                if (props.event.key === 'ArrowDown') {
                  selectedMentionIndex.value = Math.min(
                    mentionSuggestions.value.length - 1, 
                    selectedMentionIndex.value + 1
                  )
                  return true
                }
                if (props.event.key === 'Enter') {
                  const item = mentionSuggestions.value[selectedMentionIndex.value]
                  if (item) {
                    props.command({ id: item.id, label: item.name })
                  }
                  return true
                }
                return false
              },
              onExit: () => {
                mentionSuggestions.value = []
              },
            }
          },
        },
      }),
    ],
    content: '',
    editorProps: {
      attributes: {
        class: 'tiptap-editor',
      },
    },
  })
})

onBeforeUnmount(() => {
  if (editor.value) {
    editor.value.destroy()
  }
})

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = messagesRef.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
    container.scrollTop += event.deltaY; // 纵向滚动
  }
}

// 发送消息
function send() {
  if (!editor.value || editor.value.isEmpty) return
  
  const content = editor.value.getHTML()
  messages.value.push({ role: 'user', content })
  editor.value.commands.clearContent()
  
  // 模拟 AI 回复
  setTimeout(() => {
    messages.value.push({ role: 'agent', content: $t('chat.demoReply') })
    scrollToBottom()
  }, 600)
  scrollToBottom()
}

// 插入换行
function insertLineBreak() {
  editor.value?.commands.setHardBreak()
}

// 插入图片
function insertImage() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const src = e.target?.result as string
        editor.value?.commands.setImage({ src })
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

// 触发@提及
function triggerMention() {
  editor.value?.commands.insertContent('@')
  editor.value?.commands.focus()
}

// 选择提及项
function selectMention(suggestion: any) {
  editor.value?.commands.insertContent({
    type: 'mention',
    attrs: {
      id: suggestion.id,
      label: suggestion.name,
    },
  })
  mentionSuggestions.value = []
}

// 语音识别功能
function initSpeechRecognition() {
  if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition
    recognition.value = new SpeechRecognition()
    recognition.value.continuous = false
    recognition.value.interimResults = false
    recognition.value.lang = 'zh-CN'
    
    recognition.value.onresult = (event: any) => {
      const transcript = event.results[0][0].transcript
      if (editor.value && transcript.trim()) {
        editor.value.commands.insertContent(transcript)
      }
    }
    
    recognition.value.onerror = (event: any) => {
      console.error('语音识别错误:', event.error)
      isRecording.value = false
    }
    
    recognition.value.onend = () => {
      isRecording.value = false
    }
  }
}

// 开始语音输入
function startVoiceInput() {
  if (recognition.value && !isRecording.value) {
    isRecording.value = true
    recognition.value.start()
  }
}

// 停止语音输入
function stopVoiceInput() {
  if (recognition.value && isRecording.value) {
    recognition.value.stop()
    isRecording.value = false
  }
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
  position: relative;
}
.chatbox-root, .chatbox-root * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
}
.dark-theme .chatbox-root {
  background: #030303;
  color: #ffffff;
  height: 100%;
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
  margin-bottom: 12px;
}
.msg-bubble {
  max-width: 80%;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.4;
  word-break: break-word;
}

/* 用户消息气泡 */
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg.user .msg-bubble {
  color: #ffffff;
  background: #007AFF;
  border-bottom-right-radius: 4px;
}
.dark-theme .chat-msg.user .msg-bubble {
  background: #0066CC;
  color: #ffffff;
}

/* 机器人消息气泡 */
.chat-msg.agent .msg-bubble {
  color: #000000;
  background: #F2F2F7;
  border-bottom-left-radius: 4px;
}
.dark-theme .chat-msg.agent .msg-bubble {
  background: #2C2C2E;
  color: #ffffff;
}

/* 上方工具栏 */
  .chatbox-top-toolbar {
    display: flex;
    gap: 8px;
    padding: 8px 12px;
    background: #F8F9FA;
    border-bottom: 1px solid #E5E5EA;
  }
  .dark-theme .chatbox-top-toolbar {
    background: #1C1C1E;
    border-bottom: 1px solid #38383A;
  }

  /* 输入容器 */
  .chatbox-input-container {
    padding: 12px;
    background: #fff;
  }
  .dark-theme .chatbox-input-container {
    background: #030303;
  }
  
  /* 输入框包装器 */
  .chatbox-input-wrapper {
    display: flex;
    align-items: flex-end;
    position: relative;
    background: #F2F2F7;
    border-radius: 20px;
    border: 1px solid #E5E5EA;
    overflow: hidden;
  }
  .dark-theme .chatbox-input-wrapper {
    background: #2C2C2E;
    border-color: #38383A;
  }

  /* 下方工具栏 */
  .chatbox-bottom-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    background: #F8F9FA;
    border-top: 1px solid #E5E5EA;
  }
  .dark-theme .chatbox-bottom-toolbar {
    background: #1C1C1E;
    border-top: 1px solid #38383A;
  }

/* Tiptap 编辑器样式 */
 :deep(.chatbox-editor) {
   flex: 1;
   min-height: 20px;
   max-height: 120px;
   overflow-y: auto;
 }

:deep(.tiptap-editor) {
  padding: 12px 16px;
  outline: none;
  font-size: 14px;
  line-height: 1.4;
  color: #000000;
  user-select: text;
  -webkit-user-select: text;
  -ms-user-select: text;
}

:deep(.dark-theme .tiptap-editor) {
  color: #ffffff;
}

:deep(.tiptap-editor p) {
  margin: 0;
}

:deep(.tiptap-editor img) {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  margin: 4px 0;
}

:deep(.mention) {
  background: #007AFF;
  color: #ffffff;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
  text-decoration: none;
}

:deep(.dark-theme .mention) {
  background: #0066CC;
}

/* 工具栏按钮通用样式 */
  .toolbar-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    border: none;
    background: #ffffff;
    border-radius: 8px;
    color: #007AFF;
    cursor: pointer;
    transition: all 0.2s;
    font-size: 13px;
    border: 1px solid #E5E5EA;
  }
  .toolbar-btn:hover {
    background: #F0F0F0;
    transform: translateY(-1px);
  }
  .dark-theme .toolbar-btn {
    background: #2C2C2E;
    color: #0A84FF;
    border-color: #38383A;
  }
  .dark-theme .toolbar-btn:hover {
    background: #38383A;
  }
  
  /* 发送按钮 */
  .chatbox-send-btn {
    background: #007AFF;
    border: none;
    border-radius: 50%;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;
    color: #ffffff;
    margin: 8px;
    flex-shrink: 0;
  }
  .chatbox-send-btn:hover:not(:disabled) {
    background: #0056CC;
    transform: scale(1.05);
  }
  .chatbox-send-btn:disabled {
    background: #C7C7CC;
    cursor: not-allowed;
    transform: none;
  }
  .dark-theme .chatbox-send-btn:disabled {
    background: #48484A;
  }

  /* 模型选择器 */
  .model-selector {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .model-selector label {
    font-size: 13px;
    color: #666;
    font-weight: 500;
  }
  .dark-theme .model-selector label {
    color: #999;
  }
  .model-dropdown {
    padding: 6px 10px;
    border: 1px solid #E5E5EA;
    border-radius: 6px;
    background: #ffffff;
    color: #333;
    font-size: 13px;
    cursor: pointer;
  }
  .dark-theme .model-dropdown {
    background: #2C2C2E;
    border-color: #38383A;
    color: #ffffff;
  }

  /* 语音按钮 */
  .voice-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    border: 1px solid #E5E5EA;
    background: #ffffff;
    border-radius: 8px;
    color: #666;
    cursor: pointer;
    transition: all 0.2s;
    font-size: 13px;
    user-select: none;
  }
  .voice-btn:hover {
    background: #F0F0F0;
  }
  .voice-btn.recording {
    background: #FF3B30;
    color: #ffffff;
    border-color: #FF3B30;
    animation: pulse 1.5s infinite;
  }
  .dark-theme .voice-btn {
    background: #2C2C2E;
    border-color: #38383A;
    color: #999;
  }
  .dark-theme .voice-btn:hover {
    background: #38383A;
  }
  .dark-theme .voice-btn.recording {
    background: #FF453A;
    border-color: #FF453A;
  }

  @keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.05); }
    100% { transform: scale(1); }
  }

/* Mention 建议列表 */
.mention-suggestions {
  position: fixed;
  background: #ffffff;
  border: 1px solid #E5E5EA;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
  min-width: 200px;
}
.dark-theme .mention-suggestions {
  background: #2C2C2E;
  border-color: #38383A;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.mention-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.mention-item:hover,
.mention-item.active {
  background: #F2F2F7;
}
.dark-theme .mention-item:hover,
.dark-theme .mention-item.active {
  background: #38383A;
}

.mention-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #007AFF;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.mention-info {
  flex: 1;
}

.mention-name {
  font-weight: 500;
  font-size: 14px;
  color: #000000;
}
.dark-theme .mention-name {
  color: #ffffff;
}

.mention-type {
  font-size: 12px;
  color: #8E8E93;
  margin-top: 2px;
}
.dark-theme .mention-type {
  color: #8E8E93;
}
</style>