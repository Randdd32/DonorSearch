export const getBrowserFingerprint = (): string => {
  const FINGERPRINT_KEY = 'browser_fingerprint';
  let fingerprint = localStorage.getItem(FINGERPRINT_KEY);
  
  if (!fingerprint) {
    fingerprint = crypto.randomUUID();
    localStorage.setItem(FINGERPRINT_KEY, fingerprint);
  }
  
  return fingerprint;
};