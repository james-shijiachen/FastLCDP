import { ref, computed } from 'vue'

export interface DetectionOptions {
  useGeolocation?: boolean // 是否使用地理位置API（会弹出确认框）
  useIpDetection?: boolean // 是否使用IP地址检测
  silent?: boolean // 静默模式，不使用任何需要用户授权的API
}

export interface IpLocationInfo {
  countryCode: string
  region: string
  city: string
  timezone: string
  isp: string
  ip: string
  isChina: boolean
  isOverseas: boolean
}

// 时区到地域的映射
const TIMEZONE_MAPPING: Record<string, string> = {
  // 中国时区
  'Asia/Shanghai': 'CN',
  'Asia/Beijing': 'CN',
  'Asia/Chongqing': 'CN',
  'Asia/Urumqi': 'CN',
  'Asia/Hong_Kong': 'HK',
  'Asia/Taipei': 'TW',
  
  // 亚洲其他时区
  'Asia/Tokyo': 'JP',
  'Asia/Seoul': 'KR',
  'Asia/Bangkok': 'TH',
  'Asia/Singapore': 'SG',
  'Asia/Kolkata': 'IN',
  'Asia/Dubai': 'AE',
  
  // 欧洲时区
  'Europe/London': 'GB',
  'Europe/Berlin': 'DE',
  'Europe/Paris': 'FR',
  'Europe/Madrid': 'ES',
  'Europe/Rome': 'IT',
  'Europe/Moscow': 'RU',
  
  // 美洲时区
  'America/New_York': 'US',
  'America/Los_Angeles': 'US',
  'America/Chicago': 'US',
  'America/Toronto': 'CA',
  'America/Sao_Paulo': 'BR',
  
  // 大洋洲时区
  'Australia/Sydney': 'AU',
  'Australia/Melbourne': 'AU',
  'Pacific/Auckland': 'NZ'
}

/**
 * Vue 3 组合式函数：地域检测器
 */
function useRegionDetector() {
  // 响应式状态
  const region = ref<string>('UNKNOWN')
  const isDetecting = ref(false)
  const error = ref<string | null>(null)
  const lastDetectionTime = ref<number>(0)
  
  // 计算属性
  const isChina = computed(() => region.value === 'CN')
  const isOverseas = computed(() => region.value !== 'CN')
  
  /**
   * 基于时区检测地域
   */
  const detectByTimezone = (): string | null => {
    try {
      const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone
      
      if (TIMEZONE_MAPPING[timezone]) {
        const mapping = TIMEZONE_MAPPING[timezone]
        return mapping
      }
      
      return 'UNKNOWN'
    } catch (error) {
      console.warn('Timezone detection failed:', error)
      return 'UNKNOWN'
    }
  }
  
  /**
   * 基于IP地址检测地域
   */
  const detectByIpAddress = async (): Promise<string | null> => {
    try {
      // 使用免费的IP地理位置API服务
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 5000)
      
      const response = await fetch('https://ipapi.co/json/', {
        signal: controller.signal
      })
      
      clearTimeout(timeoutId)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const data = await response.json()
      if(data.country_code) {
        return data.country_code
      }
      return 'UNKNOWN'
    } catch (error) {
      console.warn('IP detection failed, trying fallback service:', error)
      
      // 备用API服务
      try {
        const response = await fetch('https://api.ipify.org?format=json')
        const ipData = await response.json()
        
        // 使用另一个服务获取位置信息
        const locationResponse = await fetch(`https://ipinfo.io/${ipData.ip}/json`)
        const locationData = await locationResponse.json()
        if(locationData.country) {
          return locationData.country
        }
        return 'UNKNOWN'
      } catch (fallbackError) {
        console.warn('IP detection completely failed:', fallbackError)
        return 'UNKNOWN'
      }
    }
  }
  
  /**
   * 基于地理位置API检测地域（需要用户授权）
   */
  const detectByGeolocation = async (): Promise<string | null> => {
    if (!navigator.geolocation) {
      return 'UNKNOWN'
    }
    
    try {
      const position = await new Promise<GeolocationPosition>((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
          resolve,
          reject,
          { timeout: 5000, enableHighAccuracy: false }
        )
      })
      
      // 这里可以根据经纬度进一步判断具体地域
      // 简单示例：根据经纬度范围判断
      const { latitude, longitude } = position.coords
      const geoRegion = getRegionByCoordinates(latitude, longitude)
      
      if (geoRegion) {
        return geoRegion
      }
      
      return 'UNKNOWN'
    } catch (error) {
      console.warn('Geolocation detection failed:', error)
      return 'UNKNOWN'
    }
  }
  
  /**
   * 根据经纬度判断地域（简单实现）
   */
  const getRegionByCoordinates = (lat: number, lng: number): string | null => {
    // 中国大陆范围
    if (lat >= 18 && lat <= 54 && lng >= 73 && lng <= 135) {
      return 'CN'
    }
    
    // 日本范围
    if (lat >= 24 && lat <= 46 && lng >= 123 && lng <= 146) {
      return 'JP'
    }
    
    // 美国范围
    if (lat >= 25 && lat <= 49 && lng >= -125 && lng <= -66) {
      return 'US'
    }
    
    // 欧洲范围
    if (lat >= 35 && lat <= 71 && lng >= -10 && lng <= 40) {
      return 'EU'
    }
    
    return null
  }
  
  /**
   * 检测用户地域信息
   * @param options 检测选项
   */
  const detectRegion = async (options: DetectionOptions = {}): Promise<string> => {
    if (isDetecting.value) {
      return region.value
    }
    
    isDetecting.value = true
    error.value = null
    
    try {
      // 基于时区检测
      let currentRegion = detectByTimezone()
      
      // 基于IP地址检测（准确且无需用户授权）
      if (options.useIpDetection !== false) {
        currentRegion = await detectByIpAddress()
      }
      
      // 根据配置决定是否使用地理位置API
      if (!options.silent && options.useGeolocation !== false) {
        // 默认尝试使用地理位置API，除非明确禁用
        currentRegion = await detectByGeolocation()
      }
      
      region.value = currentRegion || 'UNKNOWN'
      lastDetectionTime.value = Date.now()
      
      return region.value
    } catch (err) {
      error.value = err instanceof Error ? err.message : '检测失败'
      console.error('Region detection failed:', err)
      return region.value
    } finally {
      isDetecting.value = false
    }
  }
  
  /**
   * 静默检测（不使用需要用户授权的API）
   */
  const detectRegionSilent = async (): Promise<string> => {
    return await detectRegion({ silent: true, useGeolocation: false, useIpDetection: true })
  }
  
  /**
   * 判断用户是否为海外用户
   */
  const checkIsOverseasUser = async (): Promise<boolean> => {
    if (region.value === 'UNKNOWN') {
      await detectRegionSilent()
    }
    return region.value !== 'CN'
  }
  
  /**
   * 获取当前检测到的地域
   */
  const getRegion = (): string => {
    return region.value
  }
  
  return {
    // 响应式状态
    region,
    isDetecting,
    error,
    lastDetectionTime,
    
    // 计算属性
    isChina,
    isOverseas,
    
    // 方法
    detectRegion,
    detectRegionSilent,
    checkIsOverseasUser,
    getRegion,
    
    // 内部方法（可选暴露）
    detectByTimezone,
    detectByIpAddress,
    detectByGeolocation
  }
}

// 创建全局实例
const globalRegionDetector = useRegionDetector()

// 导出便捷方法（保持向后兼容）
export const detectUserRegion = () => globalRegionDetector.detectRegion()
export const regionDetector = {
  detectRegion: globalRegionDetector.detectRegion,
  detectRegionSilent: globalRegionDetector.detectRegionSilent,
  isOverseasUser: globalRegionDetector.checkIsOverseasUser,
  isChina: () => globalRegionDetector.isChina.value,
  getRegion: globalRegionDetector.getRegion
}

// 导出主要的组合式函数
export { useRegionDetector as useRegionDetection }
export default useRegionDetector