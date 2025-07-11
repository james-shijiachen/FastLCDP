<template>
  <div class="reports-view">
    <h2>{{ $t('app.reports') }}</h2>
    <div class="charts-row">
      <div class="chart-box">
        <h4>任务状态分布</h4>
        <div ref="taskChartRef" class="chart"></div>
      </div>
      <div class="chart-box">
        <h4>需求状态分布</h4>
        <div ref="requirementChartRef" class="chart"></div>
      </div>
      <div class="chart-box">
        <h4>Bug状态分布</h4>
        <div ref="bugChartRef" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import { useTaskStore } from '../stores/taskStore';
import { useRequirementStore } from '../stores/requirementStore';
import { useBugStore } from '../stores/bugStore';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const taskStore = useTaskStore();
const requirementStore = useRequirementStore();
const bugStore = useBugStore();

const taskChartRef = ref<HTMLDivElement | null>(null);
const requirementChartRef = ref<HTMLDivElement | null>(null);
const bugChartRef = ref<HTMLDivElement | null>(null);

function renderTaskChart() {
  if (!taskChartRef.value) return;
  const chart = echarts.init(taskChartRef.value);
  const data = [
    { value: taskStore.tasks.filter(t => t.status === 'todo').length, name: t('app.backlog') },
    { value: taskStore.tasks.filter(t => t.status === 'in-progress').length, name: t('app.board') },
    { value: taskStore.tasks.filter(t => t.status === 'done').length, name: t('app.reports') },
  ];
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      name: t('app.status'),
      type: 'pie',
      radius: '60%',
      data,
      label: { formatter: '{b}: {c}' },
    }],
  });
}
function renderRequirementChart() {
  if (!requirementChartRef.value) return;
  const chart = echarts.init(requirementChartRef.value);
  const data = [
    { value: requirementStore.requirements.filter(r => r.status === 'new').length, name: '新建' },
    { value: requirementStore.requirements.filter(r => r.status === 'review').length, name: '评审中' },
    { value: requirementStore.requirements.filter(r => r.status === 'in-progress').length, name: '进行中' },
    { value: requirementStore.requirements.filter(r => r.status === 'done').length, name: '已完成' },
    { value: requirementStore.requirements.filter(r => r.status === 'closed').length, name: '已关闭' },
  ];
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      name: '需求状态',
      type: 'pie',
      radius: '60%',
      data,
      label: { formatter: '{b}: {c}' },
    }],
  });
}
function renderBugChart() {
  if (!bugChartRef.value) return;
  const chart = echarts.init(bugChartRef.value);
  const data = [
    { value: bugStore.bugs.filter(b => b.status === 'new').length, name: '新建' },
    { value: bugStore.bugs.filter(b => b.status === 'open').length, name: '打开' },
    { value: bugStore.bugs.filter(b => b.status === 'in-progress').length, name: '处理中' },
    { value: bugStore.bugs.filter(b => b.status === 'resolved').length, name: '已解决' },
    { value: bugStore.bugs.filter(b => b.status === 'closed').length, name: '已关闭' },
    { value: bugStore.bugs.filter(b => b.status === 'rejected').length, name: '已拒绝' },
  ];
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      name: 'Bug状态',
      type: 'pie',
      radius: '60%',
      data,
      label: { formatter: '{b}: {c}' },
    }],
  });
}

function renderAllCharts() {
  renderTaskChart();
  renderRequirementChart();
  renderBugChart();
}

onMounted(() => {
  renderAllCharts();
});

watch(
  [() => taskStore.tasks, () => requirementStore.requirements, () => bugStore.bugs],
  () => {
    renderAllCharts();
  },
  { deep: true }
);
</script>

<style scoped>
.reports-view {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.charts-row {
  display: flex;
  gap: 32px;
  margin-top: 24px;
}
.chart-box {
  flex: 1;
  background: #f8f9fb;
  border-radius: 8px;
  padding: 16px;
  min-width: 320px;
  min-height: 340px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.chart {
  width: 260px;
  height: 260px;
}
</style> 