// Chart.js Configuration and Setup

function formatNanos(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) {
        return '—';
    }
    const ns = Number(value);
    if (ns >= 1_000_000) {
        return `${(ns / 1_000_000).toFixed(2)} мс`;
    }
    if (ns >= 1_000) {
        return `${(ns / 1_000).toFixed(2)} мкс`;
    }
    return `${ns.toFixed(0)} нс`;
}

// Bar Chart - Average operation time per tree type
async function createComplexityChart() {
    const canvas = document.getElementById('complexityChart');
    if (!canvas) {
        return;
    }

    const ctx = canvas.getContext('2d');
    const realData = await loadOperationTimesData();
    const chartData = realData ?? {
        labels: [],
        datasets: []
    };

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: chartData.labels,
            datasets: chartData.datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 12
                        },
                        padding: 15
                    }
                },
                title: {
                    display: !chartData.datasets.length,
                    text: 'Немає benchmark-даних для часу операцій',
                    color: '#94a3b8',
                    font: {
                        family: 'Inter',
                        size: 13
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(10, 14, 26, 0.95)',
                    titleColor: '#00d4ff',
                    bodyColor: '#ffffff',
                    borderColor: 'rgba(0, 212, 255, 0.3)',
                    borderWidth: 1,
                    padding: 12,
                    displayColors: true,
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${formatNanos(context.parsed.y)}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(255, 255, 255, 0.05)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 11
                        },
                        callback: function(value) {
                            return formatNanos(value);
                        }
                    }
                },
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    },
                    ticks: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 12
                        }
                    }
                }
            }
        }
    });
}

async function loadOperationTimesData() {
    try {
        const response = await fetch('/api/v1/benchmark/summary');
        if (!response.ok) {
            throw new Error(`summary failed with status ${response.status}`);
        }

        const payload = await response.json();
        const trees = Array.isArray(payload.trees) ? payload.trees : [];
        if (!trees.length) {
            return {
                labels: [],
                datasets: []
            };
        }

        const palette = {
            avl: {
                label: 'AVL Tree',
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.6)'
            },
            red_black: {
                label: 'Red-Black Tree',
                borderColor: '#00d4ff',
                backgroundColor: 'rgba(0, 212, 255, 0.6)'
            },
            splay: {
                label: 'Splay Tree',
                borderColor: '#f59e0b',
                backgroundColor: 'rgba(245, 158, 11, 0.6)'
            }
        };

        return {
            labels: ['Пошук', 'Вставка', 'Видалення'],
            datasets: trees.map((tree) => {
                const style = palette[tree.treeType] || {
                    label: tree.treeType,
                    borderColor: '#c084fc',
                    backgroundColor: 'rgba(192, 132, 252, 0.6)'
                };
                return {
                    label: style.label,
                    data: [
                        Number(tree.averageSearchTimeNs ?? 0),
                        Number(tree.averageInsertTimeNs ?? 0),
                        Number(tree.averageDeleteTimeNs ?? 0)
                    ],
                    backgroundColor: style.backgroundColor,
                    borderColor: style.borderColor,
                    borderWidth: 2
                };
            })
        };
    } catch (error) {
        console.error('Failed to load operation times data', error);
        return {
            labels: [],
            datasets: []
        };
    }
}

// Line Chart - Tree Height Growth
async function createHeightChart() {
    const canvas = document.getElementById('heightChart');
    if (!canvas) {
        return;
    }

    const ctx = canvas.getContext('2d');
    const realData = await loadHeightGrowthData();
    const chartData = realData ?? {
        labels: [],
        datasets: []
    };

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: chartData.datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 12
                        },
                        padding: 15
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(10, 14, 26, 0.95)',
                    titleColor: '#00d4ff',
                    bodyColor: '#ffffff',
                    borderColor: 'rgba(0, 212, 255, 0.3)',
                    borderWidth: 1,
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            const rawPoint = context.raw || {};
                            const value = typeof rawPoint.y === 'number' ? rawPoint.y.toFixed(2) : context.formattedValue;
                            const runCount = rawPoint.runCount ? ` (${rawPoint.runCount} runs)` : '';
                            return `${context.dataset.label}: ${value}${runCount}`;
                        }
                    }
                },
                title: {
                    display: !chartData.datasets.length,
                    text: 'Немає реальних benchmark-даних для графіка висоти',
                    color: '#94a3b8',
                    font: {
                        family: 'Inter',
                        size: 13
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Tree Height',
                        color: '#00d4ff',
                        font: {
                            family: 'Inter',
                            size: 13,
                            weight: 'bold'
                        }
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.05)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 11
                        }
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Number of Nodes',
                        color: '#00d4ff',
                        font: {
                            family: 'Inter',
                            size: 13,
                            weight: 'bold'
                        }
                    },
                    grid: {
                        display: false,
                        drawBorder: false
                    },
                    ticks: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 11
                        }
                    }
                }
            }
        }
    });
}

async function loadHeightGrowthData() {
    try {
        const response = await fetch('/api/v1/benchmark/height-growth');
        if (!response.ok) {
            throw new Error(`height-growth failed with status ${response.status}`);
        }

        const payload = await response.json();
        const series = Array.isArray(payload.series) ? payload.series : [];
        const labels = [...new Set(series.flatMap((entry) => (entry.points || []).map((point) => Number(point.datasetSize))))].sort((a, b) => a - b);

        if (!labels.length) {
            return {
                labels: [],
                datasets: []
            };
        }

        const palette = {
            avl: {
                label: 'AVL Tree Height',
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)'
            },
            'red-black': {
                label: 'Red-Black Tree Height',
                borderColor: '#00d4ff',
                backgroundColor: 'rgba(0, 212, 255, 0.1)'
            },
            splay: {
                label: 'Splay Tree Height',
                borderColor: '#f59e0b',
                backgroundColor: 'rgba(245, 158, 11, 0.12)'
            }
        };

        return {
            labels,
            datasets: series.map((entry) => {
                const style = palette[entry.treeType] || {
                    label: entry.treeType,
                    borderColor: '#c084fc',
                    backgroundColor: 'rgba(192, 132, 252, 0.12)'
                };
                const pointsBySize = new Map((entry.points || []).map((point) => [
                    Number(point.datasetSize),
                    {
                        x: Number(point.datasetSize),
                        y: Number(point.averageTreeHeight),
                        runCount: Number(point.runCount || 0)
                    }
                ]));

                return {
                    label: style.label,
                    data: labels.map((label) => pointsBySize.get(label) ?? null),
                    borderColor: style.borderColor,
                    backgroundColor: style.backgroundColor,
                    borderWidth: 3,
                    tension: 0.35,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    spanGaps: true
                };
            })
        };
    } catch (error) {
        console.error('Failed to load height growth data', error);
        return {
            labels: [],
            datasets: []
        };
    }
}

// Radar Chart - Multi-Metric Comparison
function createRadarChart() {
    const ctx = document.getElementById('radarChart').getContext('2d');
    
    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: ['Search Speed', 'Insert Speed', 'Delete Speed', 'Memory Efficiency', 'Balance Quality', 'Implementation Simplicity'],
            datasets: [
                {
                    label: 'AVL Tree',
                    data: [95, 85, 85, 90, 100, 70],
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.2)',
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Red-Black Tree',
                    data: [90, 90, 90, 90, 85, 75],
                    borderColor: '#f5576c',
                    backgroundColor: 'rgba(245, 87, 108, 0.2)',
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Extensible Tree',
                    data: [85, 95, 95, 80, 90, 65],
                    borderColor: '#7c3aed',
                    backgroundColor: 'rgba(124, 58, 237, 0.2)',
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 12
                        },
                        padding: 15
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(10, 14, 26, 0.95)',
                    titleColor: '#00d4ff',
                    bodyColor: '#ffffff',
                    borderColor: 'rgba(0, 212, 255, 0.3)',
                    borderWidth: 1,
                    padding: 12
                }
            },
            scales: {
                r: {
                    beginAtZero: true,
                    max: 100,
                    angleLines: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    pointLabels: {
                        color: '#a0aec0',
                        font: {
                            family: 'Inter',
                            size: 11
                        }
                    },
                    ticks: {
                        color: '#a0aec0',
                        backdropColor: 'transparent',
                        font: {
                            family: 'Inter',
                            size: 10
                        }
                    }
                }
            }
        }
    });
}

// Initialize all charts
setTimeout(() => {
    createComplexityChart();
    createHeightChart();
    createRadarChart();
}, 200);
