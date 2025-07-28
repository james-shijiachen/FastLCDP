<template>
  <div class="monaco-code-design-view" :class="{ 'dark-theme': isDarkTheme }">
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
        <span class="status-item" @click="toggleLanguageDropdown" style="cursor: pointer; position: relative;">
          {{ $t('code.language') }}: {{ getCurrentLanguageLabel() }}
          <div v-if="showLanguageDropdown" class="language-dropdown-menu" @click.stop>
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
import * as monaco from 'monaco-editor'

// 配置Monaco Editor的worker环境
;(window as any).MonacoEnvironment = {
  getWorker: function (workerId: string, label: string) {
    // 使用简单的worker创建方式
    const workerCode = `
      // Monaco Editor Worker
      self.onmessage = function(e) {
        // 基础worker功能
        self.postMessage({ type: 'ready' });
      };
    `;
    
    const blob = new Blob([workerCode], { type: 'application/javascript' });
    return new Worker(URL.createObjectURL(blob));
  }
};

interface Props {
  code?: string
  language?: string
  isDarkTheme?: boolean
  theme?: 'vs' | 'vs-dark' | 'hc-black'
  readOnly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  code: '',
  language: 'json',
  isDarkTheme: false,
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
      theme: props.theme || (props.isDarkTheme ? 'vs-dark' : 'vs'),
      readOnly: props.readOnly,
      automaticLayout: true,
      fontSize: 14,
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

// 格式化代码
const formatCode = async () => {
  if (!editor) return
  
  try {
    await editor.getAction('editor.action.formatDocument')?.run()
  } catch (error) {
    console.warn('Format failed:', error)
  }
}

const toggleLanguageDropdown = (event: Event) => {
  event.stopPropagation()
  console.log('toggleLanguageDropdown')
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
const setTheme = (theme: 'vs' | 'vs-dark' | 'hc-black') => {
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
watch(() => props.isDarkTheme, (isDark) => {
  if (editor) {
    setTheme(isDark ? 'vs-dark' : 'vs')
  }
})

// 生命周期
onMounted(async () => {
  await nextTick()
  await initEditor()
})

onBeforeUnmount(() => {
  if (editor) {
    editor.dispose()
  }
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
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dark-theme .monaco-code-design-view {
  background: #101010;
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