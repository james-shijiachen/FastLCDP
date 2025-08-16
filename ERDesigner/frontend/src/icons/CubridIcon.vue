<template>
  <svg viewBox="0 0 24 24" fill="currentColor">
    <!-- Outer hexagon layers -->
    <path d="M12 2L3 7v10l9 5 9-5V7l-9-5z" fill="currentColor" opacity="0.15"/>
    <path d="M12 3L4 7.5v9l8 4.5 8-4.5v-9l-8-4.5z" fill="currentColor" opacity="0.25"/>
    <path d="M12 4L5 8v8l7 4 7-4V8l-7-4z" fill="currentColor" opacity="0.35"/>
    
    <!-- Inner structure lines -->
    <path d="M12 4.5L5.5 8.25v7.5L12 19.5l6.5-3.75v-7.5L12 4.5z" fill="none" stroke="currentColor" stroke-width="1" opacity="0.6"/>
    <path d="M12 6L6.5 9.25v5.5L12 18l5.5-3.25v-5.5L12 6z" fill="none" stroke="currentColor" stroke-width="0.8" opacity="0.7"/>
    
    <!-- Center elements with layered design -->
    <circle cx="12" cy="12" r="4" fill="currentColor" opacity="0.2"/>
    <circle cx="12" cy="12" r="3" fill="currentColor" opacity="0.3"/>
    <circle cx="12" cy="12" r="2" fill="currentColor" opacity="0.4"/>
    
    <!-- Database cube representation -->
    <rect x="10" y="10" width="4" height="4" fill="currentColor" opacity="0.5"/>
    <rect x="10.5" y="10.5" width="3" height="3" fill="currentColor" opacity="0.7"/>
    
    <!-- Letter C with shadow effect -->
    <text x="12.2" y="14.2" text-anchor="middle" font-family="Arial, sans-serif" font-size="2.5" font-weight="bold" fill="currentColor" opacity="0.3">C</text>
    <text x="12" y="14" text-anchor="middle" font-family="Arial, sans-serif" font-size="2.5" font-weight="bold" fill="currentColor" opacity="0.9">C</text>
  </svg>
</template>