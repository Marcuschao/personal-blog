export function stripTrailingSlashInBrowserUrl() {
  const { pathname, search, hash } = window.location;
  if (pathname.length > 1 && pathname.endsWith('/')) {
    const next = pathname.replace(/\/+$/, '') + search + hash;
    window.history.replaceState(window.history.state, '', next);
  }
}
