const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateRegisterField(field, values) {
  const v = String(values[field] ?? '').trim();
  switch (field) {
    case 'username':
      if (!v) return '用户名不能为空';
      if (v.length < 3 || v.length > 50) return '用户名长度需在 3～50 个字符之间';
      return '';
    case 'email':
      if (!v) return '邮箱不能为空';
      if (v.length > 128) return '邮箱长度不能超过 128 个字符';
      if (!EMAIL_RE.test(v)) return '邮箱格式不正确，请填写如 name@example.com';
      return '';
    case 'password':
      if (!v) return '密码不能为空';
      if (v.length < 6 || v.length > 64) return '密码长度需在 6～64 个字符之间';
      return '';
    case 'confirmPassword':
      if (!v) return '请再次输入密码';
      if (v !== String(values.password ?? '')) return '两次输入的密码不一致';
      return '';
    case 'captchaCode':
      if (!v) return '请输入验证码';
      return '';
    default:
      return '';
  }
}

export function validateLoginField(field, values) {
  const v = String(values[field] ?? '').trim();
  switch (field) {
    case 'username':
      if (!v) return '用户名不能为空';
      return '';
    case 'password':
      if (!v) return '密码不能为空';
      return '';
    case 'captchaCode':
      if (!v) return '请输入验证码';
      return '';
    default:
      return '';
  }
}

export function applyServerFieldErrors(fieldErrors, serverMap) {
  if (!serverMap || typeof serverMap !== 'object') return fieldErrors;
  const next = { ...fieldErrors };
  Object.entries(serverMap).forEach(([key, msg]) => {
    if (msg) next[key] = msg;
  });
  return next;
}
