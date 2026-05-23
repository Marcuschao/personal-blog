export function resolveMediaUrl(url) {
  if (!url || typeof url !== 'string') return '';
  const trimmed = url.trim();
  if (!trimmed) return '';
  if (/^(https?:|data:|blob:)/i.test(trimmed)) return trimmed;
  if (trimmed.startsWith('//')) {
    if (typeof window === 'undefined') return trimmed;
    return `${window.location.protocol}${trimmed}`;
  }
  if (typeof window === 'undefined') return trimmed;
  const path = trimmed.startsWith('/') ? trimmed : `/${trimmed}`;
  return new URL(path, window.location.origin).href;
}
