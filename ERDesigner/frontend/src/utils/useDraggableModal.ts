import { ref, onUnmounted, onMounted } from 'vue'

export function useDraggableModal() {
  const modalRef = ref<HTMLElement | null>(null)
  let startX = 0, startY = 0, startLeft = 0, startTop = 0

  function onHeaderMousedown(e: MouseEvent) {
    if (!modalRef.value) return
    modalRef.value.style.transition = 'none'
    // 只允许左键拖动
    if (e.button !== 0) return

    // 1. 获取当前 modal 的左上角坐标
    const rect = modalRef.value.getBoundingClientRect()
    const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const left = rect.left + scrollLeft + rect.width/2
    const top = rect.top + scrollTop

    // 2. 记录拖拽起点
    startX = e.clientX
    startY = e.clientY
    startLeft = left
    startTop = top

    document.addEventListener('mousemove', onMouseMove)
    document.addEventListener('mouseup', onMouseUp)
  }

  function onMouseMove(e: MouseEvent) {
    if (!modalRef.value) return
    const deltaX = e.clientX - startX
    const deltaY = e.clientY - startY
    modalRef.value.style.left = `${startLeft + deltaX}px`
    modalRef.value.style.top = `${startTop + deltaY}px`
    modalRef.value.style.transform = '' // 拖动后取消居中
  }

  function onMouseUp() {
    if (modalRef.value) {
        modalRef.value.style.transition = 'left 0.5s cubic-bezier(0.4,0,0.2,1), top 0.5s cubic-bezier(0.4,0,0.2,1), transform 0.5s cubic-bezier(0.4,0,0.2,1)'
      }
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    checkAndResetIfOutOfView()
  }

  function checkAndResetIfOutOfView() {
    if (modalRef.value) {
      const rect = modalRef.value.getBoundingClientRect()
      const outOfView =
        rect.right < 0 ||
        rect.left > window.innerWidth ||
        rect.bottom < 0 ||
        rect.top > window.innerHeight
      if (outOfView) {
        resetPosition()
      }
    }
  }

  // 可选：弹窗关闭时重置位置
  function resetPosition() {
    if (modalRef.value) {
        modalRef.value.style.left = '50%'
        modalRef.value.style.top = '20%'
        modalRef.value.style.transform = 'translate(-50%, 0)'
    }
  }

  // 监听窗口大小变化
  function onResize() {
    checkAndResetIfOutOfView()
  }

  onMounted(() => {
    window.addEventListener('resize', onResize)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', onResize)
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  })

  return {
    modalRef,
    onHeaderMousedown,
    resetPosition
  }
}