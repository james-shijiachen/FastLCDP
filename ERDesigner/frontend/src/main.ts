import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { getBrowserLanguage, SUPPORTED_LANGUAGES } from '@/utils/commons'

import App from './App.vue'
import '@/styles/style.css'
import { createI18n } from 'vue-i18n'
import en from '../i18n/locales/en.json'
import zh from '../i18n/locales/zh.json'

const messages = { en, zh }

let defaultLocale = localStorage.getItem('locale') || getBrowserLanguage()
// 如果检测到的语言不在当前支持的语言中，回退到英语
if (!SUPPORTED_LANGUAGES.includes(defaultLocale)) {
  defaultLocale = 'en'
}
const i18n = createI18n({
  legacy: false,
  locale: defaultLocale,
  fallbackLocale: 'en',
  messages
})

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(i18n)
app.mount('#app')

document.title = i18n.global.t('app.title')
