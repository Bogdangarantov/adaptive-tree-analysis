export function callGlobal(functionName: string): void {
  const fn = (window as Record<string, unknown>)[functionName];
  if (typeof fn === 'function') {
    (fn as () => void)();
  }
}

export function callGlobalWithArg(functionName: string, arg: string): void {
  const fn = (window as Record<string, unknown>)[functionName];
  if (typeof fn === 'function') {
    (fn as (value: string) => void)(arg);
  }
}
