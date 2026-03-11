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
function createHeightChart() {
    const ctx = document.getElementById('heightChart').getContext('2d');
    
    const nodeCount = [10, 50, 100, 500, 1000, 5000, 10000];
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: nodeCount,
            datasets: [
                {
                    label: 'AVL Tree Height',
                    data: [3.3, 5.6, 6.6, 8.9, 9.9, 12.3, 13.3],
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7
                },
                {
                    label: 'Red-Black Tree Height',
                    data: [3.5, 5.8, 6.8, 9.1, 10.1, 12.5, 13.5],
                    borderColor: '#00d4ff',
                    backgroundColor: 'rgba(0, 212, 255, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7
                },
                {
                    label: 'Extensible Tree Height',
                    data: [3.2, 5.4, 6.4, 8.7, 9.7, 12.1, 13.1],
                    borderColor: '#7c3aed',
                    backgroundColor: 'rgba(124, 58, 237, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7
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
