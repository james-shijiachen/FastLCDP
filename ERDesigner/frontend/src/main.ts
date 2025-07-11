import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import './style.css'
import { createI18n } from 'vue-i18n'
import en from '../i18n/locales/en.json'
import zh from '../i18n/locales/zh.json'

const messages = { en, zh }
let defaultLocale = localStorage.getItem('locale') || 'en'
if (!['en', 'zh'].includes(defaultLocale)) defaultLocale = 'en'
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
