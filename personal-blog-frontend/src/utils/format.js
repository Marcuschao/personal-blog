const TZ = 'Asia/Shanghai';

function shanghaiDateKey(d) {
  return d.toLocaleDateString('en-CA', { timeZone: TZ });
}

export function formatChatMessageTime(value) {
  if (!value) return '';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '';

  const timeStr = d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    timeZone: TZ,
  });

  const msgKey = shanghaiDateKey(d);
  const todayKey = shanghaiDateKey(new Date());
  if (msgKey === todayKey) return timeStr;

  const [y, m, day] = msgKey.split('-').map(Number);
  const [ty, tm, td] = todayKey.split('-').map(Number);
  const diffDays = Math.round((Date.UTC(ty, tm - 1, td) - Date.UTC(y, m - 1, day)) / 86400000);

  if (diffDays === 1) return `昨天 ${timeStr}`;
  if (y === ty) return `${m}/${day} ${timeStr}`;
  return `${y}/${m}/${day} ${timeStr}`;
}

export function formatShortDateTime(value) {
  if (!value) return '—';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '—';
  return d.toLocaleString('zh-CN', {
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}
