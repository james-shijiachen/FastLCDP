# 浏览器API确认框说明

## 会触发确认框的浏览器API

### 1. 地理位置API (Geolocation API)
**API调用**: `navigator.geolocation.getCurrentPosition()`

**确认框内容**: "是否允许 [网站] 访问您的位置信息？"

**触发条件**:
- 首次访问网站时调用地理位置API
- 用户之前拒绝过授权，再次调用时
- 在非HTTPS环境下调用（某些浏览器）

**获取的信息**:
- 精确的经纬度坐标
- 位置精度信息
- 海拔高度（如果可用）
- 速度信息（如果可用）

### 2. 摄像头和麦克风API (MediaDevices API)
**API调用**: `navigator.mediaDevices.getUserMedia()`

**确认框内容**: "是否允许 [网站] 使用您的摄像头/麦克风？"

### 3. 通知API (Notification API)
**API调用**: `Notification.requestPermission()`

**确认框内容**: "是否允许 [网站] 显示通知？"

### 4. 剪贴板API (Clipboard API)
**API调用**: `navigator.clipboard.readText()`

**确认框内容**: "是否允许 [网站] 访问剪贴板？"

## 不会触发确认框的浏览器信息

### 1. 基本浏览器信息
- `navigator.userAgent` - 用户代理字符串
- `navigator.language` - 浏览器语言
- `navigator.languages` - 语言偏好列表
- `navigator.platform` - 操作系统平台
- `navigator.cookieEnabled` - Cookie是否启用
- `navigator.onLine` - 网络连接状态

### 2. 时区信息
- `Intl.DateTimeFormat().resolvedOptions().timeZone` - 时区
- `new Date().getTimezoneOffset()` - 时区偏移

### 3. 屏幕信息
- `screen.width` / `screen.height` - 屏幕分辨率
- `screen.colorDepth` - 颜色深度
- `window.devicePixelRatio` - 设备像素比

### 4. 浏览器功能检测
- `navigator.geolocation` - 是否支持地理位置（仅检测支持性，不获取位置）
- `navigator.serviceWorker` - 是否支持Service Worker
- `'localStorage' in window` - 是否支持本地存储

## 地域检测的避免确认框策略

### 方案1: 静默检测（推荐）
仅使用不需要用户授权的API：
```javascript
// 使用静默检测
const result = await regionDetector.detectRegionSilent()
```

**检测方法**:
1. 浏览器语言偏好 (`navigator.languages`)
2. 时区信息 (`Intl.DateTimeFormat().resolvedOptions().timeZone`)
3. 语言-地域映射表
4. 时区-地域映射表

**优点**:
- 无需用户授权
- 响应速度快
- 用户体验好
- 隐私友好

**缺点**:
- 精度相对较低
- 可能被VPN等工具影响

### 方案2: 渐进式检测
先进行静默检测，在特定场景下请求地理位置授权：
```javascript
// 先静默检测
let result = await regionDetector.detectRegionSilent()

// 在需要高精度时再请求地理位置
if (needHighAccuracy) {
  result = await regionDetector.detectRegion({ useGeolocation: true })
}
```

### 方案3: 用户主动触发
让用户主动选择是否使用地理位置：
```javascript
// 提供两个按钮给用户选择
<button @click="detectWithLocation">精确检测（需要位置权限）</button>
<button @click="detectSilent">快速检测（无需权限）</button>
```

## 最佳实践建议

### 1. 默认使用静默检测
- 对于大多数应用场景，静默检测的精度已经足够
- 避免在页面加载时立即请求地理位置权限

### 2. 明确告知用户
- 如果需要地理位置权限，提前说明用途和好处
- 提供"跳过"选项，不强制要求授权

### 3. 缓存检测结果
- 避免重复检测和重复请求权限
- 设置合理的缓存过期时间（如24小时）

### 4. 优雅降级
- 地理位置检测失败时，自动降级到静默检测
- 提供手动选择地域的备选方案

### 5. 尊重用户选择
- 用户拒绝授权后，不要反复请求
- 记住用户的选择偏好

## 检测精度对比

| 检测方法 | 精度 | 是否需要授权 | 响应速度 | 隐私友好 |
|---------|------|-------------|----------|----------|
| 地理位置API | 很高 | 是 | 慢 | 低 |
| 时区检测 | 中等 | 否 | 快 | 高 |
| 语言检测 | 中等 | 否 | 很快 | 高 |
| IP地址检测 | 高 | 否 | 中等 | 中等 |
| 综合检测 | 高 | 否 | 快 | 高 |

## 总结

对于地域检测功能，建议采用**静默检测**作为默认方案，这样可以：
- 避免弹出确认框，提升用户体验
- 保护用户隐私
- 获得足够精度的地域信息
- 快速响应，不影响页面加载速度

只在确实需要高精度位置信息的特定功能中，才考虑请求地理位置权限。