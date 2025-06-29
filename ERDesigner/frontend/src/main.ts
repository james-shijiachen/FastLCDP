import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import index from './index'
import './style.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(index)
app.mount('#app')
