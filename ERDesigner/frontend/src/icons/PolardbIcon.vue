<template>
  <svg viewBox="0 0 24 24" fill="currentColor">
    <!-- Polar star background layers -->
    <circle cx="12" cy="12" r="10" fill="currentColor" opacity="0.1"/>
    <circle cx="12" cy="12" r="8" fill="currentColor" opacity="0.15"/>
    <circle cx="12" cy="12" r="6" fill="currentColor" opacity="0.2"/>
    
    <!-- Main polar star rays with depth -->
    <path d="M12 2L13 10L12 12L11 10L12 2z" fill="currentColor" opacity="0.4"/>
    <path d="M12 3L12.5 10.5L12 12L11.5 10.5L12 3z" fill="currentColor" opacity="0.6"/>
    <path d="M22 12L14 13L12 12L14 11L22 12z" fill="currentColor" opacity="0.4"/>
    <path d="M21 12L14.5 12.5L12 12L14.5 11.5L21 12z" fill="currentColor" opacity="0.6"/>
    <path d="M12 22L11 14L12 12L13 14L12 22z" fill="currentColor" opacity="0.4"/>
    <path d="M12 21L11.5 13.5L12 12L12.5 13.5L12 21z" fill="currentColor" opacity="0.6"/>
    <path d="M2 12L10 11L12 12L10 13L2 12z" fill="currentColor" opacity="0.4"/>
    <path d="M3 12L9.5 11.5L12 12L9.5 12.5L3 12z" fill="currentColor" opacity="0.6"/>
    
    <!-- Diagonal rays with multiple layers -->
    <path d="M19.07 4.93L13.41 10.59L12 12L10.59 13.41L4.93 19.07" stroke="currentColor" stroke-width="2" opacity="0.3" fill="none"/>
    <path d="M19.07 4.93L13.41 10.59L12 12L10.59 13.41L4.93 19.07" stroke="currentColor" stroke-width="1" opacity="0.5" fill="none"/>
    <path d="M4.93 4.93L10.59 10.59L12 12L13.41 13.41L19.07 19.07" stroke="currentColor" stroke-width="2" opacity="0.3" fill="none"/>
    <path d="M4.93 4.93L10.59 10.59L12 12L13.41 13.41L19.07 19.07" stroke="currentColor" stroke-width="1" opacity="0.5" fill="none"/>
    
    <!-- Additional smaller rays -->
    <path d="M12 4L12.5 9L12 12L11.5 9L12 4z" fill="currentColor" opacity="0.3"/>
    <path d="M20 12L15 12.5L12 12L15 11.5L20 12z" fill="currentColor" opacity="0.3"/>
    <path d="M12 20L11.5 15L12 12L12.5 15L12 20z" fill="currentColor" opacity="0.3"/>
    <path d="M4 12L9 11.5L12 12L9 12.5L4 12z" fill="currentColor" opacity="0.3"/>
    
    <!-- Center core with multiple layers -->
    <circle cx="12" cy="12" r="4" fill="currentColor" opacity="0.2"/>
    <circle cx="12" cy="12" r="3" fill="currentColor" opacity="0.3"/>
    <circle cx="12" cy="12" r="2" fill="currentColor" opacity="0.5"/>
    <circle cx="12" cy="12" r="1.5" fill="currentColor" opacity="0.7"/>
    <circle cx="12" cy="12" r="0.8" fill="currentColor" opacity="0.9"/>
    
    <!-- Database layers with depth -->
    <ellipse cx="12" cy="18" rx="5" ry="1.2" fill="currentColor" opacity="0.3"/>
    <ellipse cx="12" cy="18" rx="4" ry="1" fill="currentColor" opacity="0.5"/>
    <ellipse cx="12" cy="18" rx="3" ry="0.8" fill="currentColor" opacity="0.7"/>
    <ellipse cx="12" cy="19" rx="6" ry="1.8" fill="currentColor" opacity="0.2"/>
    <ellipse cx="12" cy="19" rx="5" ry="1.5" fill="currentColor" opacity="0.4"/>
    <ellipse cx="12" cy="19" rx="4" ry="1.2" fill="currentColor" opacity="0.6"/>
  </svg>
</template>