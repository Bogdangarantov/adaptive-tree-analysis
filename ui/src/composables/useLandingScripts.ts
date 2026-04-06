export async function useLandingScripts(): Promise<void> {
  await appendScript('landing-chartjs', 'https://cdn.jsdelivr.net/npm/chart.js');
  await appendScript('landing-particles', '/generated/js/particles.js');
  await appendScript('landing-trees', '/generated/js/trees.js');
  await appendScript('landing-charts', '/generated/js/charts.js');
  await appendScript('landing-demo', '/generated/js/demo.js');
  await appendScript('landing-main', '/generated/js/main.js');
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
