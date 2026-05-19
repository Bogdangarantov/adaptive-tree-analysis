// Chart.js Configuration and Setup

// Bar Chart - Operation Time Complexity
function createComplexityChart() {
    const ctx = document.getElementById('complexityChart').getContext('2d');
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Search', 'Insert', 'Delete'],
            datasets: [
                {
                    label: 'AVL Tree',
                    data: [3, 4, 4], // O(log n) - slightly more balanced
                    backgroundColor: 'rgba(16, 185, 129, 0.6)',
                    borderColor: '#10b981',
                    borderWidth: 2
                },
                {
                    label: 'Red-Black Tree',
                    data: [3.5, 3.5, 3.5], // O(log n) - balanced
                    backgroundColor: 'rgba(0, 212, 255, 0.6)',
                    borderColor: '#00d4ff',
                    borderWidth: 2
                },
                {
                    label: 'Extensible Tree',
                    data: [3.2, 3.2, 3.2], // O(log n) - very efficient
                    backgroundColor: 'rgba(124, 58, 237, 0.6)',
                    borderColor: '#7c3aed',
                    borderWidth: 2
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
                title: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(10, 14, 26, 0.95)',
                    titleColor: '#00d4ff',
                    bodyColor: '#ffffff',
                    borderColor: 'rgba(0, 212, 255, 0.3)',
                    borderWidth: 1,
                    padding: 12,
                    displayColors: true
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
                            if (value === 10) return 'O(n)';
                            if (value >= 3 && value <= 4) return 'O(log n)';
                            return '';
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
