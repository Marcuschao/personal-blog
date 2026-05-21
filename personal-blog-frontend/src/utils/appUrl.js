export function appPublicUrl(path = '/') {
  const base = import.meta.env.BASE_URL || '/';
  let root = base.endsWith('/') ? base.slice(0, -1) : base;
  if (!root.startsWith('/')) root = `/${root}`;
  const p = path.startsWith('/') ? path : `/${path}`;
  const combined = `${root}${p}`.replace(/\/{2,}/g, '/');
  if (typeof window === 'undefined') return combined;
  return new URL(combined, window.location.origin).href;
}
