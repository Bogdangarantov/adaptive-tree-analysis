// Interactive Tree Demo Implementation

class TreeNode {
    constructor(value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

class BinarySearchTree {
    constructor() {
        this.root = null;
        this.operationCount = 0;
    }

    insert(value) {
        this.operationCount++;
        const newNode = new TreeNode(value);
        
        if (this.root === null) {
            this.root = newNode;
            return true;
        }

        let current = this.root;
        while (true) {
            if (value === current.value) return false; // Duplicate
            
            if (value < current.value) {
                if (current.left === null) {
                    current.left = newNode;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right === null) {
                    current.right = newNode;
                    return true;
                }
                current = current.right;
            }
        }
    }

    search(value) {
        this.operationCount++;
        let current = this.root;
        
        while (current !== null) {
            if (value === current.value) return current;
            if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    delete(value) {
        this.operationCount++;
        this.root = this._deleteNode(this.root, value);
    }

    _deleteNode(node, value) {
        if (node === null) return null;

        if (value < node.value) {
            node.left = this._deleteNode(node.left, value);
        } else if (value > node.value) {
            node.right = this._deleteNode(node.right, value);
        } else {
            // Node to delete found
            if (node.left === null) return node.right;
            if (node.right === null) return node.left;

            // Node with two children
            let minRight = this._findMin(node.right);
            node.value = minRight.value;
            node.right = this._deleteNode(node.right, minRight.value);
        }
        return node;
    }

    _findMin(node) {
        while (node.left !== null) {
            node = node.left;
        }
        return node;
    }

    getHeight(node = this.root) {
        if (node === null) return 0;
        return 1 + Math.max(this.getHeight(node.left), this.getHeight(node.right));
    }

    getNodeCount(node = this.root) {
        if (node === null) return 0;
        return 1 + this.getNodeCount(node.left) + this.getNodeCount(node.right);
    }

    getBalanceFactor(node = this.root) {
        if (node === null) return 0;
        return Math.abs(this.getHeight(node.left) - this.getHeight(node.right));
    }
}

// Demo tree instance
let demoTree = new BinarySearchTree();
let highlightedNode = null;

// Initialize with some values
[50, 30, 70, 20, 40, 60, 80].forEach(val => demoTree.insert(val));
demoTree.operationCount = 0; // Reset count after initial setup

// Canvas drawing for demo
function drawDemoTree() {
    const canvas = document.getElementById('demoCanvas');
    const ctx = canvas.getContext('2d');

    if (!canvas || !ctx) {
        return;
    }

    // Make canvas resolution match its displayed size for sharper rendering
    const rect = canvas.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    if (canvas.width !== rect.width * dpr || canvas.height !== rect.height * dpr) {
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    } else {
        // Reset any previous transforms before redrawing
        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    }

    ctx.clearRect(0, 0, rect.width, rect.height);

    if (demoTree.root === null) {
        ctx.fillStyle = '#a0aec0';
        ctx.font = '18px Inter';
        ctx.textAlign = 'center';
        ctx.fillText('Tree is empty. Insert nodes to begin.', rect.width / 2, rect.height / 2);
        return;
    }

    const nodes = [];
    const edges = [];

    const height = demoTree.getHeight();
    const maxDepth = Math.max(1, height);
    const verticalGap = Math.max(70, (rect.height - 120) / maxDepth);
    const minHorizontalGap = 40;
    const initialOffset = Math.max(minHorizontalGap, rect.width / Math.pow(2, maxDepth));

    // Calculate positions using recursive traversal with adaptive spacing
    function calculatePositions(node, x, y, offsetX) {
        if (node === null) return;

        nodes.push({ x, y, value: node.value, node: node });

        if (node.left) {
            const nextOffset = Math.max(minHorizontalGap, offsetX / 2);
            edges.push({ x1: x, y1: y, x2: x - nextOffset, y2: y + verticalGap });
            calculatePositions(node.left, x - nextOffset, y + verticalGap, nextOffset);
        }

        if (node.right) {
            const nextOffset = Math.max(minHorizontalGap, offsetX / 2);
            edges.push({ x1: x, y1: y, x2: x + nextOffset, y2: y + verticalGap });
            calculatePositions(node.right, x + nextOffset, y + verticalGap, nextOffset);
        }
    }

    calculatePositions(demoTree.root, rect.width / 2, 50, initialOffset);

    // Draw edges
    ctx.strokeStyle = '#00d4ff';
    ctx.lineWidth = 2;
    edges.forEach(edge => {
        ctx.beginPath();
        ctx.moveTo(edge.x1, edge.y1);
        ctx.lineTo(edge.x2, edge.y2);
        ctx.stroke();
    });

    // Draw nodes
    nodes.forEach(nodeData => {
        const isHighlighted = highlightedNode && highlightedNode.value === nodeData.value;

        // Outer glow
        if (isHighlighted) {
            const gradient = ctx.createRadialGradient(nodeData.x, nodeData.y, 20, nodeData.x, nodeData.y, 40);
            gradient.addColorStop(0, 'rgba(16, 185, 129, 0.5)');
            gradient.addColorStop(1, 'rgba(16, 185, 129, 0)');
            ctx.fillStyle = gradient;
            ctx.beginPath();
            ctx.arc(nodeData.x, nodeData.y, 40, 0, Math.PI * 2);
            ctx.fill();
        }

        // Node circle
        const nodeGradient = ctx.createLinearGradient(
            nodeData.x - 25, nodeData.y - 25,
            nodeData.x + 25, nodeData.y + 25
        );
        
        if (isHighlighted) {
            nodeGradient.addColorStop(0, '#43e97b');
            nodeGradient.addColorStop(1, '#38f9d7');
        } else {
            nodeGradient.addColorStop(0, '#667eea');
            nodeGradient.addColorStop(1, '#764ba2');
        }
        
        ctx.fillStyle = nodeGradient;
        ctx.beginPath();
        ctx.arc(nodeData.x, nodeData.y, 25, 0, Math.PI * 2);
        ctx.fill();

        // Border
        ctx.strokeStyle = isHighlighted ? '#10b981' : '#00d4ff';
        ctx.lineWidth = isHighlighted ? 3 : 2;
        ctx.stroke();

        // Value text
        ctx.fillStyle = 'white';
        ctx.font = 'bold 18px Inter';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(nodeData.value, nodeData.x, nodeData.y);
    });

    updateStats();
}

// Update statistics
function updateStats() {
    animateValue('treeHeight', demoTree.getHeight());
    animateValue('nodeCount', demoTree.getNodeCount());
    animateValue('balanceFactor', demoTree.getBalanceFactor());
    animateValue('operationCount', demoTree.operationCount);
}

function animateValue(id, target) {
    const element = document.getElementById(id);
    const current = parseInt(element.textContent) || 0;
    const increment = target > current ? 1 : -1;
    const steps = Math.abs(target - current);
    
    if (steps === 0) return;
    
    let step = 0;
    const interval = setInterval(() => {
        step++;
        element.textContent = current + (increment * step);
        
        if (step >= steps) {
            clearInterval(interval);
        }
    }, 30);
}

function showMessage(text, type = 'info') {
    const messageEl = document.getElementById('demoMessage');
    messageEl.textContent = text;
    
    messageEl.style.background = type === 'error' 
        ? 'rgba(245, 87, 108, 0.1)' 
        : type === 'success'
        ? 'rgba(16, 185, 129, 0.1)'
        : 'rgba(0, 212, 255, 0.1)';
    
    messageEl.style.borderColor = type === 'error'
        ? 'rgba(245, 87, 108, 0.3)'
        : type === 'success'
        ? 'rgba(16, 185, 129, 0.3)'
        : 'rgba(0, 212, 255, 0.3)';
    
    messageEl.style.color = type === 'error'
        ? '#f5576c'
        : type === 'success'
        ? '#10b981'
        : '#00d4ff';
}

// Operation functions
function insertNode() {
    const input = document.getElementById('nodeValue');
    const value = parseInt(input.value);
    
    if (isNaN(value)) {
        showMessage('Please enter a valid number', 'error');
        return;
    }
    
    const success = demoTree.insert(value);
    
    if (success) {
        showMessage(`Successfully inserted ${value}`, 'success');
        highlightedNode = null;
        drawDemoTree();
    } else {
        showMessage(`Value ${value} already exists in the tree`, 'error');
    }
    
    input.value = '';
}

function searchNode() {
    const input = document.getElementById('searchValue');
    const value = parseInt(input.value);
    
    if (isNaN(value)) {
        showMessage('Please enter a valid number', 'error');
        return;
    }
    
    const found = demoTree.search(value);
    
    if (found) {
        highlightedNode = found;
        showMessage(`Found ${value} in the tree!`, 'success');
        drawDemoTree();
    } else {
        highlightedNode = null;
        showMessage(`Value ${value} not found in the tree`, 'error');
        drawDemoTree();
    }
    
    input.value = '';
}

function deleteNode() {
    const input = document.getElementById('deleteValue');
    const value = parseInt(input.value);
    
    if (isNaN(value)) {
        showMessage('Please enter a valid number', 'error');
        return;
    }
    
    const found = demoTree.search(value);
    
    if (found) {
        demoTree.delete(value);
        highlightedNode = null;
        showMessage(`Successfully deleted ${value}`, 'success');
        drawDemoTree();
    } else {
        showMessage(`Value ${value} not found in the tree`, 'error');
    }
    
    input.value = '';
}

function resetTree() {
    demoTree = new BinarySearchTree();
    [50, 30, 70, 20, 40, 60, 80].forEach(val => demoTree.insert(val));
    demoTree.operationCount = 0;
    highlightedNode = null;
    showMessage('Tree has been reset to default values', 'info');
    drawDemoTree();
}

// Enter key support
document.getElementById('nodeValue')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') insertNode();
});

document.getElementById('searchValue')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') searchNode();
});

document.getElementById('deleteValue')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') deleteNode();
});

// Initial draw
setTimeout(() => {
    drawDemoTree();
    showMessage('Interactive demo ready! Try inserting, searching, or deleting nodes.', 'info');
}, 300);
