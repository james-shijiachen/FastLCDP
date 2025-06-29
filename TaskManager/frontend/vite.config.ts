import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  base: process.env.VITE_BASE_PATH || '/taskmanager/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 3003,
    open: false,
    cors: true
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  }
})