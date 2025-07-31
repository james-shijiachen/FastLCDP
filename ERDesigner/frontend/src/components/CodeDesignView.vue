<template>
  <div class="monaco-code-design-view">
    <!-- Monaco Editor 容器 -->
    <div ref="editorContainer" class="editor-container"></div>
    <!-- 底部状态栏 -->
    <div class="status-bar">
      <div class="status-left">
        <span class="status-item">{{ $t('code.lines') }}: {{ lineCount }}</span>
        <span class="status-item">{{ $t('code.characters') }}: {{ characterCount }}</span>
        <span class="status-item">{{ $t('code.cursor') }}: {{ cursorPosition.line }}:{{ cursorPosition.column }}</span>
      </div>
      <div class="status-right">
        <span class="status-item" ref="languageRef" @click="toggleLanguageDropdown" style="cursor: pointer; position: relative;">
          {{ $t('code.language') }}: {{ getCurrentLanguageLabel() }}
          <div v-if="showLanguageDropdown" class="language-dropdown-menu" ref="languageDropdownRef" @click.stop @wheel.prevent="handleModalWheel">
            <div 
              v-for="lang in supportedLanguages" 
              :key="lang.value" 
              class="language-dropdown-item"
              :class="{ active: lang.value === selectedLanguage }"
              @click="selectLanguage(lang.value)">
              {{ lang.label }}
            </div>
          </div>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import type {CanvasState } from '../types/entity'
import * as monaco from 'monaco-editor'

// 配置Monaco Editor的worker环境
;(self as any).MonacoEnvironment = {
  getWorker: function (workerId: string, label: string) {
    switch (label) {
      case 'json':
        return new Worker(
          new URL('monaco-editor/esm/vs/language/json/json.worker.js', import.meta.url),
          { type: 'module' }
        );
      case 'datasource':
      case 'java':
      case 'python':
      case 'go':
      case 'cpp':
      case 'csharp':
      case 'sql':
        // 对于这些语言，Monaco Editor没有专门的语言服务worker
        // 使用基础的editor worker，格式化将通过我们的手动实现来完成
        return new Worker(
          new URL('monaco-editor/esm/vs/editor/editor.worker.js', import.meta.url),
          { type: 'module' }
        );
      default:
        return new Worker(
          workerId.endsWith('json.worker') 
            ? new URL('monaco-editor/esm/vs/language/json/json.worker.js', import.meta.url)
            : new URL('monaco-editor/esm/vs/editor/editor.worker.js', import.meta.url),
          { type: 'module' }
        );
    }
  }
};

interface Props {
  code?: string
  language?: string
  theme: 'vs' | 'custom-dark'
  readOnly?: boolean
  codeDesignStatus: CanvasState
}

const props = withDefaults(defineProps<Props>(), {
  code: '',
  language: 'json',
  readOnly: false
})

const emit = defineEmits<{
  'update:code': [code: string]
  'update:language': [language: string]
  'save': [code: string, language: string]
  'change': [code: string]
}>()

const { t: $t } = useI18n()

// 响应式变量
const editorContainer = ref<HTMLDivElement>()
const languageRef = ref<HTMLDivElement>()
const languageDropdownRef = ref<HTMLDivElement>()
const selectedLanguage = ref(props.language)
const lastSaved = ref('')
const cursorPosition = ref({ line: 1, column: 1 })
const lineCount = ref(1)
const characterCount = ref(0)
const showLanguageDropdown = ref(false)

// Monaco Editor 实例
let editor: monaco.editor.IStandaloneCodeEditor | null = null

// 支持的语言列表
const supportedLanguages = [
  { value: 'json', label: 'JSON' },
  { value: 'sql', label: 'SQL' },
  { value: 'java', label: 'Java' },
  { value: 'python', label: 'Python' },
  { value: 'go', label: 'Go' },
  { value: 'cpp', label: 'C++' },
  { value: 'csharp', label: 'C#' },
  { value: 'datasource', label: 'DataSource' }
]

// 计算属性
const getCurrentLanguageLabel = () => {
  const lang = supportedLanguages.find(l => l.value === selectedLanguage.value)
  return lang ? lang.label : selectedLanguage.value.toUpperCase()
}

// 初始化Monaco Editor
const initEditor = async () => {
  if (!editorContainer.value) return

  try {
    // 创建编辑器实例
    editor = monaco.editor.create(editorContainer.value, {
      value: props.code,
      language: selectedLanguage.value,
      readOnly: props.readOnly,
      theme: props.theme,
      automaticLayout: true,
      fontSize: Math.round(14 * (props.codeDesignStatus.zoom || 1)),
      lineNumbers: 'on',
      minimap: { enabled: false },
      scrollBeyondLastLine: false,
      wordWrap: 'on',
      tabSize: 2,
      insertSpaces: true,
      detectIndentation: false,
      folding: true,
      foldingStrategy: 'auto',
      showFoldingControls: 'always',
      unfoldOnClickAfterEndOfLine: false,
      contextmenu: true,
      mouseWheelZoom: true,
      cursorBlinking: 'blink',
      cursorSmoothCaretAnimation: 'on',
      renderWhitespace: 'selection',
      renderControlCharacters: false,
      fontLigatures: true,
      hideCursorInOverviewRuler: true,
      overviewRulerBorder: false,
      overviewRulerLanes: 0,
      suggest: {
        showKeywords: true,
        showSnippets: true,
        showFunctions: true,
        showConstructors: true,
        showFields: true,
        showVariables: true,
        showClasses: true,
        showStructs: true,
        showInterfaces: true,
        showModules: true,
        showProperties: true,
        showEvents: true,
        showOperators: true,
        showUnits: true,
        showValues: true,
        showConstants: true,
        showEnums: true,
        showEnumMembers: true,
        showColors: true,
        showFiles: true,
        showReferences: true,
        showFolders: true,
        showTypeParameters: true,
        showUsers: true,
        showIssues: true
      }
    })

    // 监听内容变化
    editor.onDidChangeModelContent(() => {
      const value = editor?.getValue() || ''
      emit('update:code', value)
      emit('change', value)
      updateStats()
    })

    // 监听光标位置变化
    editor.onDidChangeCursorPosition((e) => {
      cursorPosition.value = {
        line: e.position.lineNumber,
        column: e.position.column
      }
    })

    // 监听选择变化
    editor.onDidChangeCursorSelection(() => {
      updateStats()
    })

    // 添加快捷键
    editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
      handleSave()
    })

    editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyF, () => {
      formatCode()
    })

    // 初始化统计信息
    updateStats()

  } catch (error) {
    console.error('Failed to initialize Monaco Editor:', error)
  }
}

// 定义自定义深色主题
monaco.editor.defineTheme('custom-dark', {
  base: 'vs-dark', // 继承内置深色主题
  inherit: true,
  rules: [
    { token: 'comment', foreground: '#9CDCFE', fontStyle: 'italic' },
    { token: 'string', foreground: '#CE9178' }
  ],
  colors: {
    'editor.background': '#101010',
    'editorGutter.background': '#151515', 
    'editorLineNumber.foreground': '#313131',  // 行号颜色
    'editorLineNumber.activeForeground': '#C6C6C6', // 当前行号颜色
  }
});

/* 监听滚轮事件（屏蔽浏览器默认滚动） */
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = languageDropdownRef.value;
  if (container) {
    container.scrollTop += event.deltaY; // 纵向滚动
  }
}

// 格式化代码
const formatCode = async () => {
  if (!editor) {
    return
  }
  
  try {
    const model = editor.getModel()
    if (!model) {
      return
    }
    
    const language = model.getLanguageId()
    const value = editor.getValue()
    let formatted = value
    
    switch (language) {
      case 'json':
        try {
          const parsed = JSON.parse(value)
          formatted = JSON.stringify(parsed, null, 2)
        } catch (jsonError) {
          console.warn('JSON format failed:', jsonError)
          return
        }
        break
        
      case 'sql':
        // SQL基本格式化：关键字大写，适当换行
        formatted = value
          .replace(/\b(select|from|where|join|on|group|by|order|having|insert|into|values|update|set|delete|create|table|drop)\b/gi, (match) => match.toUpperCase())
          .replace(/,\s*/g, ',\n  ')
          .replace(/\s+(from|where|join|group\s+by|order\s+by|having)\s+/gi, '\n$1 ')
        break
        
      case 'java':
      case 'cpp':
      case 'csharp':
        // 基本的大括号和缩进格式化
        formatted = value
          .replace(/\{/g, ' {\n')
          .replace(/\}/g, '\n}\n')
          .replace(/;/g, ';\n')
          .split('\n')
          .map((line, index, lines) => {
            const trimmed = line.trim()
            if (!trimmed) return ''
            
            let indent = 0
            for (let i = 0; i < index; i++) {
              const prevLine = lines[i].trim()
              if (prevLine.includes('{')) indent++
              if (prevLine.includes('}')) indent--
            }
            if (trimmed.includes('}')) indent--
            
            return '  '.repeat(Math.max(0, indent)) + trimmed
          })
          .join('\n')
          .replace(/\n\s*\n\s*\n/g, '\n\n') // 移除多余空行
        break
        
      case 'python':
        // Python基本格式化：保持缩进结构
        formatted = value
          .split('\n')
          .map(line => line.trimRight()) // 移除行尾空格
          .join('\n')
          .replace(/\n\s*\n\s*\n/g, '\n\n') // 移除多余空行
        break
        
      default:
        return
    }
    
    if (formatted !== value) {
      editor.setValue(formatted)
    }
    
  } catch (error) {
    console.error('Format failed:', error)
  }
}

// 更新统计信息
const updateStats = () => {
  if (!editor) return
  
  const model = editor.getModel()
  if (model) {
    lineCount.value = model.getLineCount()
    characterCount.value = model.getValueLength()
  }
}

// 语言变化处理
const onLanguageChange = () => {
  if (editor) {
    monaco.editor.setModelLanguage(editor.getModel()!, selectedLanguage.value)
    emit('update:language', selectedLanguage.value)
  }
}

const toggleLanguageDropdown = (event: Event) => {
  event.stopPropagation()
  showLanguageDropdown.value = !showLanguageDropdown.value
}

// 保存处理
const handleSave = () => {
  if (!editor) return
  
  const value = editor.getValue()
  emit('save', value, selectedLanguage.value)
  lastSaved.value = new Date().toLocaleTimeString()
}

// 设置主题
const setTheme = (theme: 'vs' | 'custom-dark') => {
  if (editor && theme) {
    monaco.editor.setTheme(theme)
  }
}

// 选择语言
const selectLanguage = (language: string) => {
  selectedLanguage.value = language
  showLanguageDropdown.value = false
  onLanguageChange()
}

// 监听props变化
watch(() => props.code, (newCode) => {
  if (editor && editor.getValue() !== newCode) {
    editor.setValue(newCode)
  }
})

watch(() => props.language, (newLanguage) => {
  selectedLanguage.value = newLanguage
  onLanguageChange()
})

watch(() => props.theme, (newTheme) => {
  if (newTheme && editor) {
    setTheme(newTheme)
  }
})

watch(() => props.readOnly, (readOnly) => {
  if (editor) {
    editor.updateOptions({ readOnly })
  }
})

// 监听主题变化
watch(() => props.theme, (newTheme) => {
  if (editor) {
    setTheme(newTheme)
  }
})

// 监听zoom变化
watch(() => props.codeDesignStatus, (status) => {
  if (editor && status.zoom) {
    const newFontSize = Math.round(14 * status.zoom)
    editor.updateOptions({ fontSize: newFontSize })
  }
}, { deep: true })

// 点击外部处理
const handleClickOutside = (event: MouseEvent) => {
  if (languageRef.value && !languageRef.value.contains(event.target as Node)) {
    showLanguageDropdown.value = false
  }
}

// 生命周期
onMounted(async () => {
  await nextTick()
  await initEditor()
  document.addEventListener('click', handleClickOutside)
})
onBeforeUnmount(() => {
  if (editor) {
    editor.dispose()
  }
  document.removeEventListener('click', handleClickOutside)
})

// 暴露方法给父组件
defineExpose({
  getEditor: () => editor,
  getValue: () => editor?.getValue() || '',
  setValue: (value: string) => editor?.setValue(value),
  formatCode,
  setTheme,
  focus: () => editor?.focus()
})
</script>

<style scoped>
.monaco-code-design-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #ffffff;
  font-family: var(--font-family-ui);
}
.dark-theme .monaco-code-design-view {
  background: #181818;
}
.editor-container {
  flex: 1;
  min-height: 0;
}
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 16px;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
  font-size: 12px;
  color: #666;
  min-height: 28px;
}
.status-left {
  display: flex;
  gap: 16px;
}
.status-right {
  display: flex;
  gap: 16px;
}

.status-item {
  white-space: nowrap;
}
.language-dropdown-menu {
  position: absolute;
  bottom: 100%;
  right: 0;
  margin-bottom: 4px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 120px;
  max-height: 200px;
  overflow-y: auto;
}
.dark-theme .language-dropdown-menu {
  background: #333333;
  border: 1px solid #333333;
}
.language-dropdown-menu::-webkit-scrollbar {
  width: 8px;
}
.language-dropdown-menu::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}
.language-dropdown-menu::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}
.language-dropdown-menu::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
.dark-theme .language-dropdown-menu::-webkit-scrollbar-track {
  background: #2a2a2a;
}
.dark-theme .language-dropdown-menu::-webkit-scrollbar-thumb {
  background: #555;
}
.dark-theme .language-dropdown-menu::-webkit-scrollbar-thumb:hover {
  background: #777;
}
.language-dropdown-item {
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}
.dark-theme .language-dropdown-item {
  border-bottom: 1px solid #333333;
}
.language-dropdown-item:hover {
  background-color: #f5f5f5;
}
.dark-theme .language-dropdown-item:hover {
  background-color: #565555;
}
.language-dropdown-item.active {
  background: #0366d6;
  color: #ffffff;
  font-weight: 500;
}
.dark-theme .language-dropdown-item.active {
  background-color: #472077;
  color: #fff;
}
.language-dropdown-item:last-child {
  border-bottom: none;
}
.dark-theme .status-bar {
  background: #1a1a1a;
  border-top: 1px solid #333;
  color: #ccc;
}
</style>