// 错误类型枚举
export enum ErrorType {
  VALIDATION = 'VALIDATION',
  NETWORK = 'NETWORK',
  BUSINESS = 'BUSINESS',
  SYSTEM = 'SYSTEM'
}

// 错误信息接口
export interface ErrorInfo {
  type: ErrorType
  message: string
  code?: string
  details?: any
  field?: string  // 字段名
  component?: string  // 组件名
}

// 字段错误接口
export interface FieldError {
  field: string
  message: string
  component?: string
}

// 简单的消息显示函数
function showMessage(message: string, type: 'error' | 'warning' | 'info' = 'error'): void {
  // 优先使用 Element Plus，如果没有则使用原生 alert
  try {
    const { ElMessage } = require('element-plus')
    switch (type) {
      case 'warning':
        ElMessage.warning(message)
        break
      case 'info':
        ElMessage.info(message)
        break
      default:
        ElMessage.error(message)
    }
  } catch {
    // 降级到原生 alert
    alert(`${type.toUpperCase()}: ${message}`)
  }
}

// 全局错误处理类
export class ErrorHandler {
  private static instance: ErrorHandler
  private errorQueue: ErrorInfo[] = []
  private isProcessing = false

  private constructor() {}

  static getInstance(): ErrorHandler {
    if (!ErrorHandler.instance) {
      ErrorHandler.instance = new ErrorHandler()
    }
    return ErrorHandler.instance
  }

  // 处理错误
  handleError(error: Error | string, type: ErrorType = ErrorType.BUSINESS, field?: string, component?: string): void {
    const errorInfo: ErrorInfo = {
      type,
      message: typeof error === 'string' ? error : error.message,
      details: error instanceof Error ? error.stack : undefined,
      field,
      component
    }

    this.errorQueue.push(errorInfo)
    this.processErrors()
  }

  // 处理字段错误
  handleFieldError(field: string, message: string, component?: string): void {
    this.handleError(message, ErrorType.VALIDATION, field, component)
  }

  // 处理验证错误
  handleValidationError(message: string): void {
    this.handleError(message, ErrorType.VALIDATION)
  }

  // 处理网络错误
  handleNetworkError(error: Error): void {
    this.handleError(error, ErrorType.NETWORK)
  }

  // 处理业务错误
  handleBusinessError(message: string): void {
    this.handleError(message, ErrorType.BUSINESS)
  }

  // 处理系统错误
  handleSystemError(error: Error): void {
    this.handleError(error, ErrorType.SYSTEM)
  }

  // 处理错误队列
  private processErrors(): void {
    if (this.isProcessing || this.errorQueue.length === 0) {
      return
    }

    this.isProcessing = true

    while (this.errorQueue.length > 0) {
      const error = this.errorQueue.shift()!
      this.showError(error)
    }

    this.isProcessing = false
  }

  // 显示错误信息
  private showError(errorInfo: ErrorInfo): void {
    const { type, message, field, component } = errorInfo

    // 如果有字段信息，触发字段级错误事件
    if (field && component) {
      this.emitFieldError(field, message, component)
      return
    }

    // 根据错误类型显示不同的消息
    switch (type) {
      case ErrorType.VALIDATION:
        showMessage(message, 'warning')
        break
      case ErrorType.NETWORK:
        showMessage(`Network error: ${message}`, 'error')
        break
      case ErrorType.BUSINESS:
        showMessage(message, 'error')
        break
      case ErrorType.SYSTEM:
        showMessage(`System error: ${message}`, 'error')
        console.error('System error:', errorInfo)
        break
      default:
        showMessage(message, 'error')
    }
  }

  // 触发字段错误事件
  private emitFieldError(field: string, message: string, component: string): void {
    const event = new CustomEvent('field-error', {
      detail: { field, message, component }
    })
    window.dispatchEvent(event)
  }

  // 清空错误队列
  clearErrors(): void {
    this.errorQueue = []
  }

  // 获取错误队列
  getErrors(): ErrorInfo[] {
    return [...this.errorQueue]
  }
}

// 创建全局错误处理器实例
export const errorHandler = ErrorHandler.getInstance()

// 错误处理装饰器
export function withErrorHandling<T extends any[], R>(
  fn: (...args: T) => R,
  errorType: ErrorType = ErrorType.BUSINESS
): (...args: T) => R {
  return (...args: T): R => {
    try {
      return fn(...args)
    } catch (error) {
      errorHandler.handleError(error as Error, errorType)
      throw error // 重新抛出错误，让调用方知道发生了错误
    }
  }
}

// 异步错误处理装饰器
export function withAsyncErrorHandling<T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  errorType: ErrorType = ErrorType.BUSINESS
): (...args: T) => Promise<R> {
  return async (...args: T): Promise<R> => {
    try {
      return await fn(...args)
    } catch (error) {
      errorHandler.handleError(error as Error, errorType)
      throw error
    }
  }
} 