import type { Entity, Field } from '@/types/entity'

// 递归获取所有父级字段
export function getAllParentFields(entities: Entity[], parentEntityId: string | undefined): Field[] {
  const result: Field[] = [];
  let currentParentId = parentEntityId;
  while (currentParentId) {
    const parent = entities.find(e => e.id === currentParentId);
    if (parent) {
      // 设置来源信息，用于显示来源字段
      parent.fields.forEach(field => {
        field.extended = {
          entityId: parent.id,
          fieldId: field.id
        }
      })
      // 按 serialNo 正排
      parent.fields.sort((a, b) => a.serialNo - b.serialNo)
      // 先递归上级，再加本级，保证顺序
      result.unshift(...parent.fields);
      currentParentId = parent.parentEntityId;
    } else {
      break;
    }
  }
  return result;
}

// 获取当前实体的所有子实体ID（递归）
export function getChildEntityIds(datasourceEntities: Entity[], parentId: string): string[] {
  const children = datasourceEntities.filter(e => e.parentEntityId === parentId)
  const childIds = children.map(c => c.id)
  
  // 递归获取子实体的子实体
  children.forEach(child => {
    childIds.push(...getChildEntityIds(datasourceEntities, child.id))
  })
  return childIds
}

// 计算实体的最小高度
export function calculateEntityHeight(entity: Entity): number {
  // 头部高度30px + 每个字段20px，最小高度60px
  const headerHeight = 30
  const fieldHeight = 25
  const minHeight = 60
  
  const calculatedHeight = headerHeight + entity.fields.length * fieldHeight
  return Math.max(minHeight, calculatedHeight)
}
// 计算实体的最小宽度
export function calculateEntityWidth(entity: Entity): number {
  // 基础最小宽度
  const minWidth = 150
  
  // 根据实体名称长度计算宽度
  const nameWidth = entity.name.length * 8 + 40
  
  // 根据字段内容计算宽度
  let maxFieldWidth = 0
  entity.fields.forEach(field => {
    const fieldNameWidth = field.name.length * 7
    const fieldTypeWidth = field.type.length * 6
    let fieldLengthWidth = 0
    let fieldScaleWidth = 0
    if(field.length && field.scale) {
      fieldLengthWidth = field.length.toString().length * 6
      fieldScaleWidth = field.scale.toString().length * 6
    } else if(field.length) {
      fieldLengthWidth = field.length.toString().length * 6
    }
    const iconWidth = field.isPrimaryKey || field.isUnique ? 25 : 8
    const fieldWidth = iconWidth + fieldNameWidth + fieldTypeWidth + fieldLengthWidth + fieldScaleWidth + 50
    maxFieldWidth = Math.max(maxFieldWidth, fieldWidth)
  })
  return Math.max(minWidth, nameWidth, maxFieldWidth)
}

// 更新实体尺寸
export function updateEntitySize(entity: Entity) {
  const width = calculateEntityWidth(entity)
  const height = calculateEntityHeight(entity)
  
  // 始终更新尺寸，实体框大小完全由计算确定
  entity.width = width
  entity.height = height
}

// 将屏幕（视口）坐标转换为画布坐标（避免使用getBoundingClientRect时返回的是视口坐标）
export function screenToCanvasCoords(clientX: number, clientY: number): { x: number, y: number } {
  // 查找SVG画布元素
  const svgCanvas = document.querySelector('.canvas-svg') as SVGSVGElement
  if (!svgCanvas) {
    return { x: clientX, y: clientY }
  }
  
  const rect = svgCanvas.getBoundingClientRect()
  const viewBox = svgCanvas.viewBox.baseVal
  
  // 计算在SVG中的相对位置
  const x = ((clientX - rect.left) / rect.width) * viewBox.width + viewBox.x
  const y = ((clientY - rect.top) / rect.height) * viewBox.height + viewBox.y
  return { x, y }
}

// 将DOMRect转换为画布坐标
export function rectToCanvasCoords(rect: DOMRect): { x: number, y: number, width: number, height: number } {
  const topLeft = screenToCanvasCoords(rect.left, rect.top)
  const bottomRight = screenToCanvasCoords(rect.right, rect.bottom)
  
  return {
    x: topLeft.x,
    y: topLeft.y,
    width: bottomRight.x - topLeft.x,
    height: bottomRight.y - topLeft.y
  }
}
