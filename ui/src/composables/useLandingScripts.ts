export async function useLandingScripts(): Promise<void> {
  await appendScript('landing-chartjs', 'https://cdn.jsdelivr.net/npm/chart.js');
  await reloadScript('landing-particles', '/generated/js/particles.js');
  await reloadScript('landing-trees', '/generated/js/trees.js');
  await reloadScript('landing-charts', '/generated/js/charts.js');
  await reloadScript('landing-main', '/generated/js/main.js');
}

function appendScript(id: string, src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const existing = document.getElementById(id) as HTMLScriptElement | null;
    if (existing) {
      resolve();
      return;
    }

    const script = document.createElement('script');
    script.id = id;
    script.src = src;
    script.async = false;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error(`Failed to load script: ${src}`));
    document.body.appendChild(script);
  });
}

function reloadScript(id: string, src: string): Promise<void> {
  const existing = document.getElementById(id);
  if (existing) {
    existing.remove();
  }
  return appendScript(id, src);
}
