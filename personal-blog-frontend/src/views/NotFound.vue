<template>
  <div class="not-found ds-page">
    <div class="container nf-inner">
      <div class="illus" aria-hidden="true">
        <svg viewBox="0 0 200 160" class="illus-svg">
          <ellipse cx="100" cy="148" rx="72" ry="8" fill="currentColor" opacity="0.08" />
          <circle cx="72" cy="58" r="28" fill="none" stroke="currentColor" stroke-width="4" opacity="0.25" />
          <path
            d="M118 42 L158 82 L138 102 L98 62 Z"
            fill="none"
            stroke="currentColor"
            stroke-width="4"
            opacity="0.35"
            stroke-linejoin="round"
          />
          <circle cx="118" cy="88" r="36" fill="var(--color-surface)" stroke="currentColor" stroke-width="3" />
          <text x="118" y="98" text-anchor="middle" font-size="28" font-weight="700" fill="currentColor">404</text>
        </svg>
      </div>
      <h1 class="nf-title">页面迷路了</h1>
      <p class="nf-sub">链接可能过期，或地址敲错了。</p>
      <router-link to="/" class="ds-btn ds-btn--primary">返回首页</router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  min-height: 60vh;
  display: flex;
  align-items: center;
}

.nf-inner {
  text-align: center;
  padding: 2rem 1rem;
}

.illus {
  color: var(--color-primary);
  margin-bottom: 1.25rem;
}

.illus-svg {
  width: min(240px, 70vw);
  height: auto;
}

.nf-title {
  margin: 0 0 0.5rem;
  font-size: 1.65rem;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.nf-sub {
  margin: 0 0 1.75rem;
  color: var(--color-text-muted);
  font-size: 0.95rem;
}
</style>
