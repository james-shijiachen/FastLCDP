import axios, { type InternalAxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import type { Entity, Relationship, Datasource, View, Index } from '@/types/entity'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error: AxiosError) => {
    console.error('API请求失败:', error)
    return Promise.reject(error)
  }
)

// 数据源相关API
export const datasourceApi = {
  // 获取所有数据源
  getDatasources: () => api.get<Datasource[]>('/datasources'),
  // 获取单个数据源
  getDatasource: (id: string) => api.get<Datasource>(`/datasources/${id}`),
  // 创建数据源
  createDatasource: (datasource: Omit<Datasource, 'id' | 'createdTime'>) => 
    api.post<Datasource>('/datasources', datasource),
  // 更新数据源
  updateDatasource: (id: string, datasource: Partial<Datasource>) => 
    api.put<Datasource>(`/datasources/${id}`, datasource),
  // 删除数据源
  deleteDatasource: (id: string) => api.delete(`/datasources/${id}`)
}

// 实体相关API
export const entityApi = {
  // 获取所有实体
  getEntities: () => api.get<Entity[]>('/entities'),
  // 获取单个实体
  getEntity: (id: string) => api.get<Entity>(`/entities/${id}`),
  // 创建实体
  createEntity: (entity: Omit<Entity, 'id' | 'createdAt'>) => 
    api.post<Entity>('/entities', entity),
  // 更新实体
  updateEntity: (id: string, entity: Partial<Entity>) => 
    api.put<Entity>(`/entities/${id}`, entity),
  // 删除实体
  deleteEntity: (id: string) => api.delete(`/entities/${id}`),
  // 获取数据源下的实体
  getEntitiesByDatasource: (datasourceId: string) => 
    api.get<Entity[]>(`/datasources/${datasourceId}/entities`)
}

// 关系相关API
export const relationshipApi = {
  // 获取所有关系
  getRelationships: () => api.get<Relationship[]>('/relationships'),
  // 获取单个关系
  getRelationship: (id: string) => api.get<Relationship>(`/relationships/${id}`),
  // 创建关系
  createRelationship: (relationship: Omit<Relationship, 'id'>) => 
    api.post<Relationship>('/relationships', relationship),
  // 更新关系
  updateRelationship: (id: string, relationship: Partial<Relationship>) => 
    api.put<Relationship>(`/relationships/${id}`, relationship),
  // 删除关系
  deleteRelationship: (id: string) => api.delete(`/relationships/${id}`)
}

// 视图相关API
export const viewApi = {
  // 获取所有视图
  getViews: () => api.get<View[]>('/views'),
  // 获取单个视图
  getView: (id: string) => api.get<View>(`/views/${id}`),
  // 创建视图
  createView: (view: Omit<View, 'id' | 'createdTime'>) => 
    api.post<View>('/views', view),
  // 更新视图
  updateView: (id: string, view: Partial<View>) => 
    api.put<View>(`/views/${id}`, view),
  // 删除视图
  deleteView: (id: string) => api.delete(`/views/${id}`)
}

// 索引相关API
export const indexApi = {
  // 获取所有索引
  getIndexes: () => api.get<Index[]>('/indexes'),
  // 获取单个索引
  getIndex: (id: string) => api.get<Index>(`/indexes/${id}`),
  // 创建索引
  createIndex: (index: Omit<Index, 'id'>) => 
    api.post<Index>('/indexes', index),
  // 更新索引
  updateIndex: (id: string, index: Partial<Index>) => 
    api.put<Index>(`/indexes/${id}`, index),
  // 删除索引
  deleteIndex: (id: string) => api.delete(`/indexes/${id}`)
}

// 图表相关API
export const diagramApi = {
  // 导出图表
  exportDiagram: (data: { entities: Entity[], relationships: Relationship[] }) => 
    api.post('/diagrams/export', data),
  // 导入图表
  importDiagram: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/diagrams/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  // 保存图表
  saveDiagram: (data: { entities: Entity[], relationships: Relationship[] }) => 
    api.post('/diagrams/save', data),
  // 加载图表
  loadDiagram: (id: string) => api.get(`/diagrams/${id}`)
}

// 工具函数
export const apiUtils = {
  // 生成唯一ID
  generateId: () => Math.random().toString(36),
  
  // 格式化日期
  formatDate: (date: Date) => date.toISOString(),
  
  // 解析日期
  parseDate: (dateString: string) => new Date(dateString)
}

export default api 