import { createI18n } from 'vue-i18n'
import en from '../i18n/locales/en.json'
import zh from '../i18n/locales/zh.json'

const messages = {
  en,
  zh
}

const i18n = createI18n({
  legacy: false,
  locale: 'en', // 默认语言为英文
  fallbackLocale: 'en',
  messages
})

export default i18n