<template>
  <el-container style="min-height: 100vh;">
    <el-header height="60px">
      <div class="nav-bar">
        <div class="logo-title">
          <Operation style="font-size: 28px; margin-right: 8px;" />
          <span class="system-title">{{ $t('app.title') }}</span>
        </div>
        <el-menu :default-active="route.path" mode="horizontal" router>
          <el-menu-item index="/backlog">{{ $t('app.backlog') }}</el-menu-item>
          <el-menu-item index="/board">{{ $t('app.board') }}</el-menu-item>
          <el-menu-item index="/requirements">{{ $t('app.requirements') }}</el-menu-item>
          <el-menu-item index="/bugs">{{ $t('app.bugs') }}</el-menu-item>
          <el-menu-item index="/reports">{{ $t('app.reports') }}</el-menu-item>
        </el-menu>
        <el-dropdown class="lang-switch" @command="switchLang">
          <span class="el-dropdown-link">
            {{ currentLang === 'zh' ? '中文' : 'EN' }}<ArrowDown style="margin-left:4px;" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="zh">中文</el-dropdown-item>
              <el-dropdown-item command="en">EN</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { Operation, ArrowDown } from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';

const route = useRoute();
const { locale } = useI18n();
const currentLang = ref(locale.value);
function switchLang(lang: string) {
  locale.value = lang;
  currentLang.value = lang;
}
</script>

<style scoped>
.el-header {
  background: #fff;
  box-shadow: 0 2px 8px #f0f1f2;
  padding: 0 32px;
}
.nav-bar {
  display: flex;
  align-items: center;
}
.logo-title {
  display: flex;
  align-items: center;
  margin-right: 32px;
}
.system-title {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  letter-spacing: 2px;
}
.lang-switch {
  margin-left: auto;
  margin-right: 8px;
  cursor: pointer;
}
.el-main {
  background: #f5f6fa;
}
</style>
