import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { TreeNode } from '../types/entity'

export const useDragStore = defineStore('dragState', () => {
  const draggingNode = ref<TreeNode | null>(null)
  const draggingChildren = computed(() => {
    return getAllChildNodes(draggingNode.value || {} as TreeNode)
  })
  
  function startDrag(node: TreeNode) {
    draggingNode.value = node
  }
  
  function endDrag() {
    draggingNode.value = null
  }
  
  function canDropOn(targetNode: TreeNode): boolean {
    if (!draggingNode.value) return false
    
    // 不能拖拽到自己
    if (draggingNode.value.id === targetNode.id) return false
    
    // 不能拖拽到自己的子节点
    if (draggingChildren.value.some(child => child.id === targetNode.id)) return false

    return true
  }
  
  // 递归获取所有子节点（包括所有层级的后代节点）
  function getAllChildNodes(node: TreeNode): TreeNode[] {
    const allChildren: TreeNode[] = []

    if (!node.children || node.children.length === 0) {
        return allChildren
    }

    // 遍历直接子节点
    for (const child of node.children) {
        // 添加当前子节点
        allChildren.push(child)
        // 递归获取子节点的所有后代
        const grandChildren = getAllChildNodes(child)
        allChildren.push(...grandChildren)
    }
    return allChildren
  }

  return {
    draggingNode,
    draggingChildren,
    startDrag,
    endDrag,
    canDropOn
  }
})