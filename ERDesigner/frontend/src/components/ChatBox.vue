<template>
  <div class="chatbox-root" @wheel="handleModalWheel">
    <div class="chatbox-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="msg-bubble">
          <div class="msg-content" v-html="msg.content"></div>
        </div>
      </div>
    </div>
    <!-- 聊天框外框容器 -->
    <div class="chatbox-outer-container">
      <!-- 输入区域容器 -->
      <div class="chatbox-input-container">
        <div class="chatbox-input-wrapper">
          <editor-content 
            :editor="editor" 
            class="chatbox-editor"
            @keydown.enter.exact.prevent="send"
            @keydown.shift.enter.exact="insertLineBreak"
          />
        </div>
        <!-- 工具栏容器 -->
        <div class="chatbox-toolbar-wrapper">
          <!-- 左侧工具按钮 -->
          <div class="left-tools">
            <button class="toolbar-btn" @click="triggerMention" title="@提及">
              <Icon name="at" style="width: 18px; height: 18px;"/>
            </button>
            <button class="toolbar-btn" @click="insertImage" title="图片">
              <Icon name="picture" style="width: 18px; height: 18px;"/>
            </button>
          </div>
          
          <!-- 右侧控件 -->
          <div class="right-controls">
            <!-- 模型选择器 -->
            <div class="model-selector">
              <VueSelect
              v-model="selectedModel"
              :options="modelOptions"
              :clearable="false"
              :searchable="false"
              :append-to-body="true"
              :calculate-position="calculateDropdownPosition"
              class="model-dropdown"
              placeholder="选择模型"/>
            </div>
            
            <!-- 语音按钮 -->
            <button 
              class="voice-btn" 
              @mousedown="startVoiceInput"
              @mouseup="stopVoiceInput"
              @mouseleave="stopVoiceInput"
              :class="{ recording: isRecording }"
              title="按住说话">
              <Icon name="microphone" style="width: 18px; height: 18px;"/>
            </button>
            
            <!-- 发送按钮 -->
            <button class="chatbox-send-btn" @click="send" :disabled="!canSend">
              <Icon name="send-prompt" style="width: 18px; height: 18px;"/>
            </button>
          </div>
        </div>
      </div>
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
        <div class="mention-avatar" :class="`mention-${suggestion.category}`">
          <Icon v-if="suggestion.category === 'datasource'" name="database" />
          <Icon v-else-if="suggestion.category === 'entity'" name="view" />
          <Icon v-else-if="suggestion.category === 'relationship'" name="hard-relation" />
          <Icon v-else-if="suggestion.category === 'index'" name="key" />
          <span v-else>{{ suggestion.avatar }}</span>
        </div>
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
import VueSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import { Editor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Mention from '@tiptap/extension-mention'
import Placeholder from '@tiptap/extension-placeholder'
import Icon from '@/components/Icon.vue'
import { useDSDiagramStore } from '@/stores/dsDiagram'

interface Message {
  role: 'user' | 'agent'
  content: string
}

const { t: $t } = useI18n()
const store = useDSDiagramStore()
const messages = ref<Message[]>([])
const messagesRef = ref<HTMLElement | null>(null)
const editor = ref<Editor>()
const mentionSuggestions = ref<any[]>([])
const selectedMentionIndex = ref(0)
const mentionPosition = ref({ top: '0px', left: '0px' })
const selectedModel = ref('gpt-4')
const isRecording = ref(false)
const recognition = ref<any>(null)

// 模型选项数据
const modelOptions = ref([
  { label: 'GPT-4', value: 'gpt-4' },
  { label: 'GPT-3.5', value: 'gpt-3.5-turbo' },
  { label: 'Claude-3', value: 'claude-3' },
  { label: 'Gemini', value: 'gemini-pro' }
])

// 计算下拉框位置，确保在上方显示
const calculateDropdownPosition = (dropdownList: HTMLElement, component: any, { width }: { width: number }) => {
  const toggle = component.$refs.toggle
  if (!toggle) return
  
  const rect = toggle.getBoundingClientRect()
  const dropdownHeight = dropdownList.offsetHeight || 200
  const spaceBelow = window.innerHeight - rect.bottom
  const spaceAbove = rect.top
  
  // 如果下方空间不足或者上方空间更充足，则在上方显示
  if (spaceBelow < dropdownHeight && spaceAbove > dropdownHeight) {
    dropdownList.style.top = `${rect.top - dropdownHeight}px`
  } else {
    dropdownList.style.top = `${rect.bottom}px`
  }
  
  dropdownList.style.left = `${rect.left}px`
  dropdownList.style.width = `${width}px`
  dropdownList.style.zIndex = '9999'
}

// 计算是否可以发送
const canSend = computed(() => {
  return editor.value && !editor.value.isEmpty
})

// 获取提及建议数据
const mentionData = computed(() => {
  const suggestions: any[] = []
  
  // 添加数据源
  store.datasources.forEach(datasource => {
    suggestions.push({
      id: `datasource_${datasource.id}`,
      name: datasource.name,
      type: '数据源',
      category: 'datasource',
      avatar: datasource.name.charAt(0).toUpperCase(),
      originalId: datasource.id
    })
  })
  
  // 添加实体
  store.entities.forEach(entity => {
    suggestions.push({
      id: `entity_${entity.id}`,
      name: entity.name,
      type: '实体',
      category: 'entity',
      avatar: entity.name.charAt(0).toUpperCase(),
      originalId: entity.id
    })
  })
  
  // 添加关系
  store.relationships.forEach(relationship => {
    const name = relationship.name || `关系_${relationship.id.slice(0, 8)}`
    suggestions.push({
      id: `relationship_${relationship.id}`,
      name: name,
      type: '关系',
      category: 'relationship',
      avatar: name.charAt(0).toUpperCase(),
      originalId: relationship.id
    })
  })
  
  // 添加索引
  store.indexes.forEach(index => {
    suggestions.push({
      id: `index_${index.id}`,
      name: index.name,
      type: '索引',
      category: 'index',
      avatar: index.name.charAt(0).toUpperCase(),
      originalId: index.id
    })
  })
  
  return suggestions
})

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
            return mentionData.value.filter(item => 
              item.name.toLowerCase().includes(query.toLowerCase())
            ).slice(0, 10)
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
                   const viewportHeight = window.innerHeight
                   const viewportWidth = window.innerWidth
                   const suggestionHeight = 200 // 预估建议列表高度
                   const suggestionWidth = 300 // 预估建议列表宽度
                   
                   let top = rect.top - suggestionHeight - 8
                   let left = rect.left
                   
                   // 如果上方空间不够，显示在下方
                   if (top < 0) {
                     top = rect.bottom + 5
                   }
                   
                   // 如果右侧空间不够，向左调整
                   if (left + suggestionWidth > viewportWidth) {
                     left = viewportWidth - suggestionWidth - 10
                   }
                   
                   // 确保不超出左边界
                   if (left < 10) {
                     left = 10
                   }
                   
                   mentionPosition.value = {
                     top: `${top}px`,
                     left: `${left}px`
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
  
  // 获取事件目标元素
  const target = event.target as HTMLElement;
  
  // 检查是否在输入框区域内
  const isInInputArea = target.closest('.chatbox-editor') || target.closest('.chatbox-input-wrapper');
  
  if (isInInputArea) {
    // 在输入框区域，阻止浏览器滚动并手动控制输入框滚动
    event.preventDefault();
    const inputWrapper = target.closest('.chatbox-input-wrapper') as HTMLElement;
    if (inputWrapper) {
      inputWrapper.scrollTop += event.deltaY;
    }
    return;
  } else {
    // 在聊天框区域，阻止默认行为并滚动聊天内容
    event.preventDefault();
    const container = messagesRef.value;
    if (container) {
      container.scrollTop += event.deltaY;
    }
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
  /* 隐藏滚动条但保持滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}
.chatbox-messages::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
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

/* 聊天框外框容器 */
.chatbox-outer-container {
  border: 1px solid #3A3A3C;
  border-radius: 4px;
  background: #030303;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin: 8px;
}
.dark-theme .chatbox-outer-container {
  background: #030303;
  border-color: #3A3A3C;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 输入区域容器 */
.chatbox-input-container {
  display: flex;
  flex-direction: column;
  max-height: 200px;
  background: #0d0d0d;
  overflow: hidden;
}

/* 工具栏容器 */
.chatbox-toolbar-container {
  padding: 8px 12px 12px 12px;
  background: #1C1C1E;
  border-top: 1px solid #3A3A3C;
}
.dark-theme .chatbox-toolbar-container {
  background: #1C1C1E;
  border-top-color: #3A3A3C;
}

/* 输入区域包装器 */
.chatbox-input-wrapper {
  flex: 1;
  min-height: 48px;
  transition: all 0.2s ease;
  border-radius: 4px;
  overflow-y: auto;
  overflow-x: hidden;
  word-wrap: break-word;
  word-break: break-word;
}

/* 工具栏包装器 */
.chatbox-toolbar-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  padding: 0 8px 8px 8px;
}

/* 左侧工具按钮 */
.left-tools {
  display: flex;
  align-items: center;
}

/* 右侧控件 */
.right-controls {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Tiptap 编辑器样式 */
:deep(.chatbox-editor) {
  width: 100%;
  min-height: 32px;
  padding: 8px;
  overflow: visible;
}

:deep(.tiptap-editor) {
  padding: 0;
  outline: none;
  font-size: 14px;
  line-height: 1.4;
  user-select: text;
  -webkit-user-select: text;
  -ms-user-select: text;
  background: transparent;
  border: none;
  width: 100%;
  word-wrap: break-word;
  word-break: break-word;
  overflow-wrap: break-word;
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
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.toolbar-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}
.dark-theme .toolbar-btn {
  color: #8E8E93;
}
.dark-theme .toolbar-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}
  
/* 发送按钮 */
.chatbox-send-btn {
  background: #007AFF;
  border: none;
  border-radius: 4px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #ffffff;
  flex-shrink: 0;
  font-size: 14px;
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
}

/* Vue Select 组件样式 */
:deep(.model-dropdown) {
  min-width: 100px;
  font-size: 12px;
}

:deep(.model-dropdown .vs__dropdown-toggle) {
  padding: 2px 4px;
  border: none;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.05);
  min-height: 24px;
  transition: all 0.2s;
}

:deep(.model-dropdown .vs__dropdown-toggle:hover) {
  background: rgba(0, 0, 0, 0.1);
}

:deep(.model-dropdown .vs__selected-options) {
  padding: 0;
  margin: 0;
}

:deep(.model-dropdown .vs__selected) {
  color: #666;
  font-size: 12px;
  margin: 0;
  padding: 0;
  border: none;
  background: transparent;
}

:deep(.model-dropdown .vs__actions) {
  padding: 0 4px;
}

:deep(.model-dropdown .vs__clear) {
  display: none;
}

:deep(.model-dropdown .vs__open-indicator) {
  fill: #666;
  transform: scale(0.8);
}

/* 暗色主题下的 Vue Select 样式 */
:deep(.dark-theme .model-dropdown .vs__dropdown-toggle) {
  background: rgba(255, 255, 255, 0.1);
}

:deep(.dark-theme .model-dropdown .vs__dropdown-toggle:hover) {
  background: rgba(255, 255, 255, 0.15);
}

:deep(.dark-theme .model-dropdown .vs__selected) {
  color: #8E8E93;
}

:deep(.dark-theme .model-dropdown .vs__open-indicator) {
  fill: #8E8E93;
}

/* 语音按钮 */
.voice-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 16px;
  user-select: none;
}
.voice-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}
.voice-btn.recording {
  background: #FF3B30;
  color: #ffffff;
  animation: pulse 1.5s infinite;
}
.dark-theme .voice-btn {
  color: #8E8E93;
}
.dark-theme .voice-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}
.dark-theme .voice-btn.recording {
  background: #FF453A;
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
  max-width: 300px;
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
  padding: 8px 12px;
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
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  font-size: 12px;
  font-weight: 500;
}

/* 不同类型的mention图标样式 */
.mention-datasource {
  background: #007AFF;
  color: #ffffff;
}
.mention-entity {
  background: #34C759;
  color: #ffffff;
}
.mention-relationship {
  background: #FF9500;
  color: #ffffff;
}
.mention-index {
  background: #AF52DE;
  color: #ffffff;
}

.mention-info {
  flex: 1;
  min-width: 0;
}
.mention-name {
  font-size: 14px;
  font-weight: 500;
  color: #000000;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.mention-type {
  font-size: 12px;
  color: #8E8E93;
  margin-top: 2px;
}
.dark-theme .mention-name {
  color: #ffffff;
}
.dark-theme .mention-type {
  color: #8E8E93;
}


</style>