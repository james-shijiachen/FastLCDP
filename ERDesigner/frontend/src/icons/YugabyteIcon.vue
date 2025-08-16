<template>
  <svg viewBox="0 0 24 24" fill="currentColor">
    <!-- Y shape representing Yugabyte -->
    <path d="M12 2L8 8h3v6h2V8h3L12 2z" fill="currentColor"/>
    <!-- Database cylinder base -->
    <ellipse cx="12" cy="18" rx="6" ry="2" fill="currentColor"/>
    <rect x="6" y="16" width="12" height="4" fill="currentColor"/>
    <ellipse cx="12" cy="16" rx="6" ry="2" fill="currentColor" opacity="0.8"/>
    <!-- Connection lines -->
    <path d="M12 14v2" stroke="currentColor" stroke-width="2" fill="none"/>
    <!-- Distributed nodes -->
    <circle cx="8" cy="12" r="1" fill="currentColor"/>
    <circle cx="16" cy="12" r="1" fill="currentColor"/>
    <circle cx="12" cy="10" r="1" fill="currentColor"/>
    <!-- Connection between nodes -->
    <path d="M8 12h8M12 10v2" stroke="currentColor" stroke-width="0.5" fill="none" opacity="0.6"/>
  </svg>
</template>