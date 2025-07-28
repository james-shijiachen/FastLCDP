<template>
  <div class="toolbar-wrapper" @wheel.prevent="handleModalWheel">
    <button class="scroll-btn left" @click="scrollLeft" v-show="showLeftScroll" :aria-label="$t('toolbar.scrollLeft')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
    </button>
    <div class="toolbar" ref="toolbarRef">
      <!-- 新建图表 -->
      <button @click="newDiagram" :title="$t('toolbar.newDiagram')" :aria-label="$t('toolbar.newDiagram')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M4 4h12l4 4v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4z"/>
          <polyline points="16 4 16 8 20 8"/>
          <line x1="8" y1="13" x2="16" y2="13"/>
          <line x1="8" y1="17" x2="16" y2="17"/>
          <line x1="8" y1="9" x2="10" y2="9"/>
        </svg>
      </button>
      <!-- 保存 -->
      <button @click="saveDiagram" :title="$t('toolbar.saveDiagram')" :aria-label="$t('toolbar.saveDiagram')">
        <svg viewBox="0 0 23 23" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
          <polyline points="17 21 17 13 7 13 7 21"/>
          <polyline points="7 3 7 8 15 8"/>
        </svg>
      </button>
      <!-- 导入-->
      <button @click="importDiagram" :title="$t('toolbar.importDiagram')" :aria-label="$t('toolbar.importDiagram')">
        <svg fill="currentColor" viewBox="0 0 25 25">
          <path d="M20,24H0V0h14.41L20,5.59v4.38h-2V8h-6V2H2v20h18V24z M14,6h3.59L14,2.41V6z M11,16l4.71-4.71l1.41,1.41L14.83,15h8.59v2
          h-8.59l2.29,2.29l-1.41,1.41L11,16z"/>
        </svg>
      </button>
      <!-- 导出-->
      <button @click="exportDiagram" :title="$t('toolbar.exportDiagram')" :aria-label="$t('toolbar.exportDiagram')">
        <svg fill="currentColor" stroke="currentColor" stroke-width="2" viewBox="0 0 89 89">
          <path d="M73,76.5v5c0,2.2-1.9,3.5-4.1,3.5H3.6C1.4,85,0,83.8,0,81.5V32.1c0-2.2,1.4-4.2,3.6-4.2h11.7
          c2.2,0,4,1.8,4,4s-1.8,4-4,4H8V77h57v-0.5c0-2.2,1.8-4,4-4S73,74.3,73,76.5z M90.8,39.2L66,64.5c-1.2,1.2-2.9,1.5-4.4,0.9
          C60,64.7,59,63.3,59,61.6V50.7c-8-0.2-27.2,0.6-34.2,12.9c-0.7,1.3-2.1,2.1-3.5,2.1c-0.3,0-0.7,0-1-0.1c-1.8-0.5-3-2.1-3-3.9
          c0-0.6,0-16.1,11.6-27.6C36.2,26.6,46,22.6,59,21.9V11c0-1.6,1-3.1,2.5-3.7C63.1,6.7,64.8,7,66,8.2l24.9,25.3
          C92.4,35.1,92.4,37.6,90.8,39.2z M82.2,36.3L67,20.9v4.9c0,2.2-1.7,4-4,4c-12.4,0-21.9,3.3-28.4,9.9c-3,3-5,6.3-6.3,9.5
          c9.4-5.6,21.3-6.6,28.6-6.6c3.8,0,6.3,0.3,6.6,0.3c2,0.2,3.5,2,3.5,4v4.9L82.2,36.3z"/>
        </svg>
      </button>
      <!-- 撤销 -->
      <div class="toolbar-separator"></div>
      <button @click="undo" :title="$t('toolbar.undo')" :aria-label="$t('toolbar.undo')">
        <svg viewBox="0 0 23 23" fill="none">
          <path d="M8.70714 3.29289C9.09766 3.68342 9.09766 4.31658 8.70714 4.70711L6.41424 7H14C17.866 7 21 10.134 21 14C21 17.866 17.866 21 14 21H8.15388C7.60159 21 7.15388 20.5523 7.15388 20C7.15388 19.4477 7.60159 19 8.15388 19H14C16.7615 19 19 16.7614 19 14C19 11.2386 16.7615 9 14 9H6.41424L8.70714 11.2929C9.09766 11.6834 9.09766 12.3166 8.70714 12.7071C8.31661 13.0976 7.68345 13.0976 7.29292 12.7071L2.58582 8L7.29292 3.29289C7.68345 2.90237 8.31661 2.90237 8.70714 3.29289Z" fill="currentColor"/>
        </svg>
      </button>
      <!-- 重做 -->
      <button @click="redo" :title="$t('toolbar.redo')" :aria-label="$t('toolbar.redo')">
        <svg viewBox="0 0 23 23" fill="none">
          <path fill-rule="evenodd" clip-rule="evenodd" d="M15.2929 3.29289C15.6834 2.90237 16.3166 2.90237 16.7071 3.29289L21.4142 8L16.7071 12.7071C16.3166 13.0976 15.6834 13.0976 15.2929 12.7071C14.9024 12.3166 14.9024 11.6834 15.2929 11.2929L17.5858 9H10C7.23858 9 5 11.2386 5 14C5 16.7614 7.23857 19 10 19H15.8462C16.3984 19 16.8462 19.4477 16.8462 20C16.8462 20.5523 16.3984 21 15.8462 21H10C6.134 21 3 17.866 3 14C3 10.134 6.13401 7 10 7H17.5858L15.2929 4.70711C14.9024 4.31658 14.9024 3.68342 15.2929 3.29289Z" fill="currentColor"/>
        </svg>
      </button>
      <div class="toolbar-separator"></div>
      <!-- 放大 -->
      <button @click="zoomIn" :title="$t('toolbar.zoomIn')" :aria-label="$t('toolbar.zoomIn')">
        <svg viewBox="0 0 22 22" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="11" y1="8" x2="11" y2="14"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <!-- 百分比显示/设置 -->
      <button
          ref="zoomButtonRef"
          class="zoom-percentage"
          @click="toggleDropdown"
          @dblclick="enableInput"
          :title="$t('toolbar.setZoom')"
          :aria-label="$t('toolbar.setZoom')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <text x="4" y="18" font-size="14">%</text>
          </svg>
          <span class="zoom-text">
            <span v-if="!inputMode">{{ Math.round((zoomLevel ?? 1) * 100) }}%</span>
            <span v-else style="white-space: nowrap;">
              <input
                ref="zoomInputRef"
                v-model="inputValue"
                @keyup.enter="confirmInput"
                @blur="confirmInput"
                style="width: 35px; text-align: right; font-size: 12px;"/>%
            </span>
          </span>
        </button>
        <!-- 下拉框 -->
        <div v-if="dropdownVisible" :style="dropdownStyle" class="zoom-dropdown" @mousedown.prevent>
          <div
            v-for="option in zoomOptions"
            :key="option"
            class="zoom-option"
            @click="selectZoom(option)">
            {{ option }}%
          </div>
        </div>
      <!-- 缩小 -->
      <button @click="zoomOut" :title="$t('toolbar.zoomOut')" :aria-label="$t('toolbar.zoomOut')">
        <svg viewBox="0 0 23 23" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <!-- 元素撑满 -->
      <button @click="resetZoom" :title="$t('toolbar.fitToScreen')" :aria-label="$t('toolbar.fitToScreen')">
        <svg viewBox="0 0 32 32">
            <g fill="currentColor">
                <g id="Icon-Set" transform="translate(-152.000000, -983.000000)">
                    <path d="M176.972,989 L172,989 C171.448,989 171,989.448 171,990 C171,990.553 171.448,991 172,991 L174.628,991 L169.83,995.799 L171.244,997.213 L176.022,992.435 L176,995 C176,995.553 176.448,996 177,996 C177.552,996 178,995.553 178,995 L178,990 C178,989.704 177.877,989.465 177.684,989.301 C177.502,989.115 177.251,989 176.972,989 L176.972,989 Z M182,1011 C182,1012.1 181.104,1013 180,1013 L156,1013 C154.896,1013 154,1012.1 154,1011 L154,987 C154,985.896 154.896,985 156,985 L180,985 C181.104,985 182,985.896 182,987 L182,1011 L182,1011 Z M180,983 L156,983 C153.791,983 152,984.791 152,987 L152,1011 C152,1013.21 153.791,1015 156,1015 L180,1015 C182.209,1015 184,1013.21 184,1011 L184,987 C184,984.791 182.209,983 180,983 L180,983 Z M164.756,1000.79 L159.978,1005.57 L160,1003 C160,1002.45 159.552,1002 159,1002 C158.448,1002 158,1002.45 158,1003 L158,1008 C158,1008.3 158.123,1008.54 158.316,1008.7 C158.497,1008.88 158.749,1009 159.028,1009 L164,1009 C164.552,1009 165,1008.55 165,1008 C165,1007.45 164.552,1007 164,1007 L161.372,1007 L166.17,1002.2 L164.756,1000.79 L164.756,1000.79 Z M177,1002 C176.448,1002 176,1002.45 176,1003 L176.022,1005.57 L171.244,1000.79 L169.83,1002.2 L174.628,1007 L172,1007 C171.448,1007 171,1007.45 171,1008 C171,1008.55 171.448,1009 172,1009 L176.972,1009 C177.251,1009 177.503,1008.88 177.684,1008.7 C177.877,1008.54 178,1008.3 178,1008 L178,1003 C178,1002.45 177.552,1002 177,1002 L177,1002 Z M164,991 C164.552,991 165,990.553 165,990 C165,989.448 164.552,989 164,989 L159.028,989 C158.749,989 158.498,989.115 158.316,989.301 C158.123,989.465 158,989.704 158,990 L158,995 C158,995.553 158.448,996 159,996 C159.552,996 160,995.553 160,995 L159.978,992.435 L164.756,997.213 L166.17,995.799 L161.372,991 L164,991 L164,991 Z"></path>
                </g>
            </g>
        </svg>
      </button>
      <!-- 拖拽画布 -->
      <button @click="dragCanvas" :title="$t('toolbar.dragCanvas')" :aria-label="$t('toolbar.dragCanvas')">
        <component :is="isDragMode ? DragCanvasIcon : SelectionBoxIcon" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 插入实体 -->
      <button @click="addEntity" :title="$t('toolbar.addEntity')" :aria-label="$t('toolbar.addEntity')">
        <component :is="AddEntityIcon" />
      </button>
      <!-- 复制实体 -->
      <button @click="copyEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.copyEntity')" :aria-label="$t('toolbar.copyEntity')">
        <component :is="CopyIcon" />
      </button>
      <!-- 粘贴实体 -->
      <button @click="pasteEntity" :disabled="!canPaste" :title="$t('toolbar.pasteEntity')" :aria-label="$t('toolbar.pasteEntity')">
        <component :is="PasteIcon" />
      </button>
      <!-- 删除实体 -->
      <button @click="deleteEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.deleteEntity')" :aria-label="$t('toolbar.deleteEntity')">
        <component :is="DeleteIcon" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 实体染色 -->
      <button @click="colorEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.colorEntity')" :aria-label="$t('toolbar.colorEntity')">
        <svg fill="currentColor" viewBox="0 0 16 16">
          <path d="M8 .5C3.58.5 0 3.86 0 8s3.58 7.5 8 7.5c4.69 0 1.04-2.83 2.79-4.55.76-.75 1.63-.87 2.44-.87.37 0 .73.03 1.06.03.99 0 1.72-.23 1.72-2.1C16 3.86 12.42.5 8 .5zm6.65 8.32c-.05.01-.16.02-.37.02-.14 0-.29 0-.45-.01-.19 0-.39-.01-.61-.01-.89 0-2.19.13-3.32 1.23-1.17 1.16-.9 2.6-.74 3.47.03.18.08.44.09.6-.16.05-.52.13-1.26.13-3.72 0-6.75-2.8-6.75-6.25S4.28 1.75 8 1.75s6.75 2.8 6.75 6.25c0 .5-.06.74-.1.82z"/>
          <path d="M5.9 9.47c-1.03 0-1.86.8-1.86 1.79s.84 1.79 1.86 1.79 1.86-.8 1.86-1.79-.84-1.79-1.86-1.79zm0 2.35c-.35 0-.64-.25-.64-.56s.29-.56.64-.56.64.25.64.56-.29.56-.64.56zm-.2-4.59c0-.99-.84-1.79-1.86-1.79s-1.86.8-1.86 1.79.84 1.79 1.86 1.79 1.86-.8 1.86-1.79zm-1.86.56c-.35 0-.64-.25-.64-.56s.29-.56.64-.56.64.25.64.56-.29.56-.64.56zM7.37 2.5c-1.03 0-1.86.8-1.86 1.79s.84 1.79 1.86 1.79 1.86-.8 1.86-1.79S8.39 2.5 7.37 2.5zm0 2.35c-.35 0-.64-.25-.64-.56s.29-.56.64-.56.64.25.64.56-.29.56-.64.56zm2.47 1.31c0 .99.84 1.79 1.86 1.79s1.86-.8 1.86-1.79-.84-1.79-1.86-1.79-1.86.8-1.86 1.79zm2.5 0c0 .31-.29.56-.64.56s-.64-.25-.64-.56.29-.56.64-.56.64.25.64.56z"/>
        </svg>
      </button>
      <!-- 实体外框染色 -->
      <button @click="colorEntityBorder" :disabled="!isSelectedEntity" :title="$t('toolbar.colorEntityBorder')" :aria-label="$t('toolbar.colorEntityBorder')">
        <svg viewBox="0 0 24 24" fill="none">
          <path fill="currentColor" d="M14.8686 4.13134L14.1615 3.42423L14.1615 3.42423L14.8686 4.13134ZM7.81459 7.48152L8.08931 8.44304L7.81459 7.48152ZM5.57564 9.83884L6.55004 10.0637V10.0637L5.57564 9.83884ZM3 21L2.02561 20.7751C1.94808 21.1111 2.04909 21.4633 2.29289 21.7071C2.5367 21.9509 2.8889 22.0519 3.22486 21.9744L3 21ZM14.1611 18.4243L13.9363 17.4499L13.9363 17.4499L14.1611 18.4243ZM16.5185 16.1854L15.5569 15.9107L16.5185 16.1854ZM19.8686 9.13134L20.5757 9.83845V9.83845L19.8686 9.13134ZM19.8686 6.8686L19.1615 7.57571H19.1615L19.8686 6.8686ZM17.1314 4.13134L17.8385 3.42423V3.42423L17.1314 4.13134ZM20.5368 8.30899L19.5858 7.99997L20.5368 8.30899ZM20.5368 7.69095L19.5858 7.99997L20.5368 7.69095ZM15.4404 18.0251L15.9601 18.8794H15.9601L15.4404 18.0251ZM16.0539 17.4424L16.8804 18.0054L16.8804 18.0054L16.0539 17.4424ZM6.55756 7.94607L7.12056 8.77253L7.12056 8.77253L6.55756 7.94607ZM5.97487 8.55957L6.82922 9.07928L6.82922 9.07928L5.97487 8.55957ZM15.691 3.46313L15.382 2.51207L15.691 3.46313ZM16.309 3.46313L16.618 2.51207L16.618 2.51207L16.309 3.46313ZM9.14645 16.2676C9.53697 15.8771 9.53697 15.2439 9.14644 14.8534C8.75591 14.4629 8.12275 14.4629 7.73223 14.8534L9.14645 16.2676ZM10 14.5C10 14.7761 9.77614 15 9.5 15V17C10.8807 17 12 15.8807 12 14.5H10ZM9.5 15C9.22386 15 9 14.7761 9 14.5H7C7 15.8807 8.11929 17 9.5 17V15ZM9 14.5C9 14.2238 9.22386 14 9.5 14V12C8.11929 12 7 13.1193 7 14.5H9ZM9.5 14C9.77614 14 10 14.2238 10 14.5H12C12 13.1193 10.8807 12 9.5 12V14ZM14.1615 3.42423L12.2929 5.29286L13.7071 6.70708L15.5757 4.83845L14.1615 3.42423ZM12.7253 5.03845L7.53987 6.51999L8.08931 8.44304L13.2747 6.96149L12.7253 5.03845ZM4.60125 9.61398L2.02561 20.7751L3.97439 21.2248L6.55004 10.0637L4.60125 9.61398ZM3.22486 21.9744L14.386 19.3987L13.9363 17.4499L2.77514 20.0256L3.22486 21.9744ZM17.48 16.4601L18.9615 11.2747L17.0385 10.7252L15.5569 15.9107L17.48 16.4601ZM18.7071 11.7071L20.5757 9.83845L19.1615 8.42424L17.2929 10.2929L18.7071 11.7071ZM20.5757 6.16149L17.8385 3.42423L16.4243 4.83845L19.1615 7.57571L20.5757 6.16149ZM20.5757 9.83845C20.7621 9.65211 20.9449 9.47038 21.0858 9.30446C21.2342 9.12961 21.3938 8.90772 21.4879 8.618L19.5858 7.99997C19.6057 7.93858 19.6292 7.92986 19.5611 8.01011C19.4854 8.09928 19.3712 8.21456 19.1615 8.42424L20.5757 9.83845ZM19.1615 7.57571C19.3712 7.78538 19.4854 7.90066 19.5611 7.98984C19.6292 8.07008 19.6057 8.06136 19.5858 7.99997L21.4879 7.38194C21.3938 7.09222 21.2342 6.87033 21.0858 6.69548C20.9449 6.52957 20.7621 6.34783 20.5757 6.16149L19.1615 7.57571ZM21.4879 8.618C21.6184 8.21632 21.6184 7.78362 21.4879 7.38194L19.5858 7.99997V7.99997L21.4879 8.618ZM14.386 19.3987C14.988 19.2598 15.5141 19.1507 15.9601 18.8794L14.9207 17.1708C14.8157 17.2346 14.6727 17.28 13.9363 17.4499L14.386 19.3987ZM15.5569 15.9107C15.3493 16.6373 15.2966 16.7778 15.2274 16.8794L16.8804 18.0054C17.1743 17.574 17.3103 17.0541 17.48 16.4601L15.5569 15.9107ZM15.9601 18.8794C16.3257 18.6571 16.6395 18.359 16.8804 18.0054L15.2274 16.8794C15.1471 16.9973 15.0426 17.0966 14.9207 17.1708L15.9601 18.8794ZM7.53987 6.51999C6.94585 6.68971 6.426 6.82571 5.99457 7.11961L7.12056 8.77253C7.22213 8.70334 7.36263 8.65066 8.08931 8.44304L7.53987 6.51999ZM6.55004 10.0637C6.71998 9.32729 6.76535 9.18427 6.82922 9.07928L5.12053 8.03986C4.84922 8.48586 4.74017 9.01202 4.60125 9.61398L6.55004 10.0637ZM5.99457 7.11961C5.64092 7.36052 5.34291 7.67429 5.12053 8.03986L6.82922 9.07928C6.90334 8.95742 7.00268 8.85283 7.12056 8.77253L5.99457 7.11961ZM15.5757 4.83845C15.7854 4.62878 15.9007 4.51459 15.9899 4.43889C16.0701 4.37076 16.0614 4.39424 16 4.41418L15.382 2.51207C15.0922 2.60621 14.8704 2.76578 14.6955 2.91421C14.5296 3.05506 14.3479 3.2379 14.1615 3.42423L15.5757 4.83845ZM17.8385 3.42423C17.6521 3.23789 17.4704 3.05506 17.3045 2.91421C17.1296 2.76578 16.9078 2.60621 16.618 2.51207L16 4.41418C15.9386 4.39424 15.9299 4.37077 16.0101 4.43889C16.0993 4.51459 16.2146 4.62877 16.4243 4.83845L17.8385 3.42423ZM16 4.41418H16L16.618 2.51207C16.2163 2.38156 15.7837 2.38156 15.382 2.51207L16 4.41418ZM12.2929 6.70708L17.2929 11.7071L18.7071 10.2929L13.7071 5.29286L12.2929 6.70708ZM7.73223 14.8534L2.29289 20.2929L3.70711 21.7071L9.14645 16.2676L7.73223 14.8534Z"/>
        </svg>
      </button>
      <!-- 字体颜色调整 -->
      <button @click="changeEntityFontColor" :disabled="!isSelectedEntity" :title="$t('toolbar.changeFontColor')" :aria-label="$t('toolbar.changeFontColor')">
        <svg viewBox="0 0 23 23" fill="none">
          <!-- 外框 -->
          <rect x="3" y="3" width="18" height="18" rx="3" stroke="currentColor" stroke-width="2" fill="none"/>
          <!-- 大写A -->
          <text x="12" y="17" text-anchor="middle" font-size="14" font-weight="bold" fill="currentColor">A</text>
        </svg>
      </button>
      <!-- 连线染色 -->
      <button @click="colorRelation" :title="$t('toolbar.colorRelation')" :aria-label="$t('toolbar.colorRelation')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="4" y1="20" x2="20" y2="4"/>
          <circle cx="4" cy="20" r="2"/>
          <circle cx="20" cy="4" r="2"/>
        </svg>
      </button>
      <!-- 网格显示/隐藏 -->
      <button @click="toggleGrid" :title="$t('toolbar.toggleGrid')" :aria-label="$t('toolbar.toggleGrid')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="18" height="18" rx="2"/>
          <path d="M3 9h18M3 15h18M9 3v18M15 3v18"/>
        </svg>
      </button>
      <!-- 全屏 -->
      <button @click="toggleFullscreen" :title="$t('toolbar.fullscreen')" :aria-label="$t('toolbar.fullscreen')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="4 9 4 4 9 4"/>
          <polyline points="20 9 20 4 15 4"/>
          <polyline points="20 15 20 20 15 20"/>
          <polyline points="4 15 4 20 9 20"/>
        </svg>
      </button>
      <!-- 侧边栏显示/隐藏 -->
      <button @click="toggleSidebar" :title="$t('toolbar.toggleSidebar')" :aria-label="$t('toolbar.toggleSidebar')" style="margin-left: auto;">
        <svg v-if="sidebarVisible" width="32" height="24" viewBox="0 0 32 24" fill="none">
          <rect x="3" y="4" width="17" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
          <rect x="22" y="4" width="7" height="24" stroke="currentColor" stroke-width="2" fill="currentColor"/>
        </svg>
        <svg v-else width="32" height="24" viewBox="0 0 32 24" fill="none">
          <rect x="3" y="4" width="17" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
          <rect x="22" y="4" width="7" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
        </svg>
      </button>
    </div>
    <button class="scroll-btn right" @click="scrollRight" v-show="showRightScroll" :aria-label="$t('toolbar.scrollRight')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="9 6 15 12 9 18"/>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import AddEntityIcon from '@/assets/AddEntityIcon.vue'
import PasteIcon from '@/assets/PasteIcon.vue'
import CopyIcon from '@/assets/CopyIcon.vue'
import DeleteIcon from '@/assets/DeleteIcon.vue'
import DragCanvasIcon from '@/assets/DragCanvasIcon.vue'
import SelectionBoxIcon from '@/assets/SelectionBoxIcon.vue'

const emit = defineEmits([
  'toggleSidebar',
  'newDiagram',
  'saveDiagram',
  'exportDiagram',
  'addEntity',
  'undo',
  'redo',
  'zoomIn',
  'zoomOut',
  'setZoom',
  'resetZoom',
  'toggleGrid',
  'toggleFullscreen',
  'copyEntity',
  'deleteEntity',
  'importDiagram',
  'colorEntityBorder',
  'changeEntityFontColor',
  'colorEntity',
  'colorRelation',
  'pasteEntity',
  'dragCanvas'
])
const props = defineProps({
  sidebarVisible: Boolean,
  zoomLevel: Number,
  canPaste: Boolean,
  isSelectedEntity: Boolean
})
const { t: $t } = useI18n()
function toggleSidebar() { emit('toggleSidebar') }
function newDiagram() { emit('newDiagram') }
function saveDiagram() { emit('saveDiagram') }
function exportDiagram() { emit('exportDiagram') }
function undo() { emit('undo') }
function redo() { emit('redo') }
function zoomIn() { emit('zoomIn') }
function zoomOut() { emit('zoomOut') }
function resetZoom() { emit('resetZoom') }
function toggleGrid() { emit('toggleGrid') }
function toggleFullscreen() { emit('toggleFullscreen') }
function setZoom(level: number) { emit('setZoom', level) }
function addEntity() { emit('addEntity') }
function colorEntity() { emit('colorEntity') }
function colorRelation() { emit('colorRelation') }
function copyEntity() { emit('copyEntity') }
function pasteEntity() { emit('pasteEntity') }
function deleteEntity() { emit('deleteEntity') }
function importDiagram() { emit('importDiagram') }
function colorEntityBorder() { emit('colorEntityBorder') }
function changeEntityFontColor() { emit('changeEntityFontColor') }
function dragCanvas() { 
  isDragMode.value = !isDragMode.value
  emit('dragCanvas', isDragMode.value)
}

const toolbarRef = ref<HTMLElement | null>(null)
const showLeftScroll = ref(false)
const showRightScroll = ref(false)
const dropdownVisible = ref(false)
const inputMode = ref(false)
const inputValue = ref('')
const zoomInputRef = ref<HTMLInputElement | null>(null)
const zoomOptions = [50, 75, 100, 150, 200]
const zoomButtonRef = ref<HTMLElement | null>(null)
const dropdownStyle = ref({})
const isDragMode = ref(false)

/* 百分比设置下拉框 */
function toggleDropdown() {
  if (inputMode.value) return
  dropdownVisible.value = !dropdownVisible.value
  if(dropdownVisible.value) {
    nextTick(() => {
      if (zoomButtonRef.value) {
        const rect = zoomButtonRef.value.getBoundingClientRect()
        dropdownStyle.value = {
          left: rect.left - 17 + 'px',
          top: rect.top - 30 + 'px'
        }
      }
    })
  }
}

/* 百分比设置下拉框选择 */
function selectZoom(val: number) {
  setZoom(val / 100)
  dropdownVisible.value = false
}

/* 百分比设置下拉框输入 */
function enableInput() {
  inputMode.value = true
  inputValue.value = Math.round((props.zoomLevel ?? 1) * 100).toString()
  dropdownVisible.value = false
  nextTick(() => {
    zoomInputRef.value?.focus()
    zoomInputRef.value?.select()
  })
}

/* 百分比设置下拉框输入确认 */
function confirmInput() {
  let val = parseInt(inputValue.value, 10)
  if (isNaN(val) || val < 10) val = 10
  if (val > 500) val = 500
  setZoom(val / 100)
  inputMode.value = false
}

/* 监听滚轮事件（屏蔽浏览器默认滚动） */
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = toolbarRef.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
  }
}

/* 滚动按钮 */
function updateScrollBtns() {
  const el = toolbarRef.value
  if (!el) return
  // 放宽误差，避免浮点导致按钮不出现
  showLeftScroll.value = el.scrollLeft > 0
  showRightScroll.value = el.scrollLeft + el.clientWidth < el.scrollWidth
}

/* 滚动按钮左移 */
function scrollLeft() {
  const el = toolbarRef.value
  if (el) {
    el.scrollBy({ left: -100, behavior: 'smooth' })
    setTimeout(updateScrollBtns, 300)
  }
}

/* 滚动按钮右移 */
function scrollRight() {
  const el = toolbarRef.value
  if (el) {
    el.scrollBy({ left: 100, behavior: 'smooth' })
    setTimeout(updateScrollBtns, 300)
  }
}

/* 点击工具栏外部关闭下拉框和输入框 */
function handleClickOutside(event: MouseEvent) {
  if (toolbarRef.value && !toolbarRef.value.contains(event.target as Node)) {
    dropdownVisible.value = false
    inputMode.value = false
  }
}

/* 挂载 */
onMounted(() => {
  nextTick(updateScrollBtns)
  toolbarRef.value?.addEventListener('scroll', updateScrollBtns)
  window.addEventListener('resize', updateScrollBtns)
  document.addEventListener('click', handleClickOutside)
})

/* 卸载 */
onBeforeUnmount(() => {
  toolbarRef.value?.removeEventListener('scroll', updateScrollBtns)
  window.removeEventListener('resize', updateScrollBtns)
  document.removeEventListener('click', handleClickOutside)
})

/* 监听工具栏是否超出边界 */
watch(toolbarRef, updateScrollBtns)
</script>

<style scoped>
/* 工具栏 */
.toolbar-wrapper {
  position: relative; /* 相对定位 */
  width: 100%; /* 宽度100% */
}
.toolbar {
  display: flex; /* 使用flex布局 */
  flex-wrap: nowrap; /* 不允许换行 */
  gap: 15px;  /* 增加按钮之间的间距 */
  overflow-x: auto; /* 横向滚动 */
  background: #f5f7fa;
  border-bottom: 0.5px solid #d9dce1; 
  justify-content: flex-start;
  align-items: center; 
  width: 100%; 
  box-sizing: border-box; 
  height: var(--toolbar-height); 
  scrollbar-width: none; /* Firefox隐藏原生滚动条 */
  -ms-overflow-style: none; /* IE和Edge隐藏原生滚动条 */
  padding-left: 16px;
  padding-right: 16px;
}
.toolbar::-webkit-scrollbar {
  display: none; /* Chrome和Safari隐藏原生滚动条 */
}
/* 工具栏分割线 */
.toolbar-separator {
  width: 1px;
  min-width: 1px;
  height: 16px;
  min-height: 16px;
  background: #d8d8d8;
  flex-shrink: 0;
}
/* 滚动按钮 */
.scroll-btn {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: var(--toolbar-icon-size);
  background: #dedede;
  border: none;
  z-index: 2;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: #409eff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: background 0.2s;
}
.scroll-btn.left { left: 0; }
.scroll-btn.right { right: 0; }

/* 工具栏按钮 */
.toolbar button {
  padding: 0;
  border: none;
  background: none;
  color: #212121;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.15s, color 0.15s;
}
.toolbar button, .zoom-percentage {
  width: var(--toolbar-icon-size); /* 增加按钮宽度 */
  height: var(--toolbar-icon-size); /* 增加按钮高度 */
  min-width: var(--toolbar-icon-size); /* 增加按钮宽度 */
  min-height: var(--toolbar-icon-size); /* 增加按钮高度 */
  font-size: 14px; /* 增加按钮字体大小 */   
  padding: 0;
}
.toolbar button:hover {
  transform: scale(1.12);
  color: #409eff;
  background: none;
}
.toolbar button:disabled svg {
  opacity: 0.4;         /* 变淡 */
  filter: grayscale(1); /* 变灰 */
  cursor: not-allowed;  /* 禁用手势 */
}
.toolbar button:disabled {
  background: none;  /* 可选：按钮背景变淡 */
  color: #aaa;          /* 可选：文字变灰 */
  border-color: none;   /* 可选：边框变淡 */
}
/* 百分比显示/设置 */
.zoom-percentage {
  display: flex;
  align-items: center;
  gap: 2px;
  background: none;
  border: none;
  color: #222;
  cursor: pointer;
  margin-right: 18px;
}
/* 百分比显示/设置文本 */
.zoom-text {
  min-width: 18px;
  text-align: right;
  font-size: 14px;
  color: #222;
}
.zoom-dropdown {
  position: absolute;
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  z-index: 100;
  min-width: 70px;
  margin-top: 2px;
}
.dark-theme .zoom-dropdown {
  color: #2f2f2f;
}
.zoom-option {
  padding: 6px 16px;
  cursor: pointer;
}
.zoom-option:hover {
  background: #f5f7fa;
}

/* 暗色主题 */
.dark-theme .toolbar {
  background: #030303;
  border-bottom: 0.5px solid #333333; 
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.2);
}
.dark-theme .toolbar-separator {
  background: #3d3d3d;
}
.dark-theme .toolbar button, .dark-theme .zoom-percentage, .dark-theme .zoom-text {
  color: #f4f3f3;
}
.dark-theme .toolbar button:hover{
  transform: scale(1.12);
  color: #409eff;
  background: none;
}

/* 768px以下移动端样式 */
@media (max-width: var(--mobile-breakpoint)) {
  .toolbar {
    flex-wrap: nowrap;     /* 移动端允许换行 */
    min-height: var(--toolbar-height);
    gap: 15px; /* 移动端工具栏按钮间距 */
  }
  .toolbar button, .zoom-percentage, .scroll-btn {
    font-size: 14px; /* 增加按钮字体大小 */   
    padding: 0;
  }
}
</style> 