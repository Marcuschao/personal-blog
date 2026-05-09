<template>
  <footer class="footer">
    <div class="container footer-inner">
      <div class="footer-accent" aria-hidden="true" />
      <p class="copyright">&copy; 2026 我的个人博客 · All rights reserved.</p>
      <p class="credit">
        <span class="credit-dot" />Powered by Vue 3
      </p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  margin-top: auto;
  padding: 2.75rem 0;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.6) 0%, rgba(226, 232, 240, 0.9) 100%);
  border-top: 1px solid var(--color-border);
  position: relative;
}

.footer-inner {
  text-align: center;
  position: relative;
}

.footer-accent {
  position: absolute;
  left: 50%;
  top: -1px;
  transform: translateX(-50%);
  width: min(480px, 72%);
  height: 3px;
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
  background: var(--gradient-cta);
  opacity: 0.65;
}

.copyright {
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--color-text-muted);
  letter-spacing: 0.02em;
}

.credit {
  margin-top: 0.65rem;
  font-size: 0.8rem;
  color: var(--color-text-soft);
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  opacity: 0.85;
}

.credit-dot {
  width: 6px;
  height: 6px;
  border-radius: var(--radius-pill);
  background: var(--gradient-cta);
}

.footer p {
  margin: 0;
}
</style>
