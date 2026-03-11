// Tree Drawing Functions

// Draw Hero Tree Animation
function drawHeroTree() {
    const svg = document.getElementById('heroTreeSVG');
    const nodes = [
        { x: 150, y: 50, value: 50 },
        { x: 80, y: 120, value: 30 },
        { x: 220, y: 120, value: 70 },
        { x: 40, y: 190, value: 20 },
        { x: 120, y: 190, value: 40 },
        { x: 180, y: 190, value: 60 },
        { x: 260, y: 190, value: 80 }
    ];

    const edges = [
        [0, 1], [0, 2], [1, 3], [1, 4], [2, 5], [2, 6]
    ];

    // Draw edges
    edges.forEach(([from, to], index) => {
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', nodes[from].x);
        line.setAttribute('y1', nodes[from].y);
        line.setAttribute('x2', nodes[to].x);
        line.setAttribute('y2', nodes[to].y);
        line.setAttribute('stroke', '#00d4ff');
        line.setAttribute('stroke-width', '2');
        line.setAttribute('opacity', '0');
        svg.appendChild(line);

        setTimeout(() => {
            line.setAttribute('opacity', '0.6');
            line.style.transition = 'opacity 0.5s ease';
        }, index * 100);
    });

    // Draw nodes
    nodes.forEach((node, index) => {
        const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        g.setAttribute('opacity', '0');

        const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        circle.setAttribute('cx', node.x);
        circle.setAttribute('cy', node.y);
        circle.setAttribute('r', '20');
        circle.setAttribute('fill', 'url(#gradient)');
        circle.setAttribute('stroke', '#00d4ff');
        circle.setAttribute('stroke-width', '2');

        const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', node.x);
        text.setAttribute('y', node.y + 5);
        text.setAttribute('text-anchor', 'middle');
        text.setAttribute('fill', 'white');
        text.setAttribute('font-size', '14');
        text.setAttribute('font-weight', 'bold');
        text.textContent = node.value;

        g.appendChild(circle);
        g.appendChild(text);
        svg.appendChild(g);

        setTimeout(() => {
            g.setAttribute('opacity', '1');
            g.style.transition = 'opacity 0.5s ease';
        }, index * 150);
    });

    // Add gradient definition
    const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
    const gradient = document.createElementNS('http://www.w3.org/2000/svg', 'linearGradient');
    gradient.setAttribute('id', 'gradient');
    gradient.setAttribute('x1', '0%');
    gradient.setAttribute('y1', '0%');
    gradient.setAttribute('x2', '100%');
    gradient.setAttribute('y2', '100%');

    const stop1 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop1.setAttribute('offset', '0%');
    stop1.setAttribute('stop-color', '#667eea');

    const stop2 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop2.setAttribute('offset', '100%');
    stop2.setAttribute('stop-color', '#764ba2');

    gradient.appendChild(stop1);
    gradient.appendChild(stop2);
    defs.appendChild(gradient);
    svg.insertBefore(defs, svg.firstChild);
}

// Draw Extensible Tree (formerly BST)
function drawBST() {
    const canvas = document.getElementById('bstCanvas');
    const ctx = canvas.getContext('2d');
    
    const nodes = [
        { x: 250, y: 40, value: 50 },
        { x: 150, y: 120, value: 30 },
        { x: 350, y: 120, value: 70 },
        { x: 80, y: 200, value: 20 },
        { x: 220, y: 200, value: 40 },
        { x: 280, y: 200, value: 60 },
        { x: 420, y: 200, value: 80 }
    ];

    const edges = [
        [0, 1], [0, 2], [1, 3], [1, 4], [2, 5], [2, 6]
    ];

    // Draw edges
    ctx.strokeStyle = '#00d4ff';
    ctx.lineWidth = 2;
    edges.forEach(([from, to]) => {
        ctx.beginPath();
        ctx.moveTo(nodes[from].x, nodes[from].y);
        ctx.lineTo(nodes[to].x, nodes[to].y);
        ctx.stroke();
    });

    // Draw nodes
    nodes.forEach(node => {
        // Outer glow
        const gradient = ctx.createRadialGradient(node.x, node.y, 15, node.x, node.y, 30);
        gradient.addColorStop(0, 'rgba(0, 212, 255, 0.3)');
        gradient.addColorStop(1, 'rgba(0, 212, 255, 0)');
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 30, 0, Math.PI * 2);
        ctx.fill();

        // Node circle
        const nodeGradient = ctx.createLinearGradient(node.x - 20, node.y - 20, node.x + 20, node.y + 20);
        nodeGradient.addColorStop(0, '#667eea');
        nodeGradient.addColorStop(1, '#764ba2');
        ctx.fillStyle = nodeGradient;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 20, 0, Math.PI * 2);
        ctx.fill();

        // Border
        ctx.strokeStyle = '#00d4ff';
        ctx.lineWidth = 2;
        ctx.stroke();

        // Value text
        ctx.fillStyle = 'white';
        ctx.font = 'bold 16px Inter';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(node.value, node.x, node.y);
    });
}

// Draw AVL Tree with Balance Factors
function drawAVL() {
    const canvas = document.getElementById('avlCanvas');
    const ctx = canvas.getContext('2d');
    
    const nodes = [
        { x: 250, y: 40, value: 50, bf: 0 },
        { x: 150, y: 120, value: 30, bf: 0 },
        { x: 350, y: 120, value: 70, bf: -1 },
        { x: 80, y: 200, value: 20, bf: 0 },
        { x: 220, y: 200, value: 40, bf: 0 },
        { x: 420, y: 200, value: 80, bf: 0 }
    ];

    const edges = [
        [0, 1], [0, 2], [1, 3], [1, 4], [2, 5]
    ];

    // Draw edges
    ctx.strokeStyle = '#10b981';
    ctx.lineWidth = 2;
    edges.forEach(([from, to]) => {
        ctx.beginPath();
        ctx.moveTo(nodes[from].x, nodes[from].y);
        ctx.lineTo(nodes[to].x, nodes[to].y);
        ctx.stroke();
    });

    // Draw nodes
    nodes.forEach(node => {
        // Outer glow
        const gradient = ctx.createRadialGradient(node.x, node.y, 15, node.x, node.y, 30);
        gradient.addColorStop(0, 'rgba(16, 185, 129, 0.3)');
        gradient.addColorStop(1, 'rgba(16, 185, 129, 0)');
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 30, 0, Math.PI * 2);
        ctx.fill();

        // Node circle
        const nodeGradient = ctx.createLinearGradient(node.x - 20, node.y - 20, node.x + 20, node.y + 20);
        nodeGradient.addColorStop(0, '#43e97b');
        nodeGradient.addColorStop(1, '#38f9d7');
        ctx.fillStyle = nodeGradient;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 20, 0, Math.PI * 2);
        ctx.fill();

        // Border
        ctx.strokeStyle = '#10b981';
        ctx.lineWidth = 2;
        ctx.stroke();

        // Value text
        ctx.fillStyle = 'white';
        ctx.font = 'bold 16px Inter';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(node.value, node.x, node.y);

        // Balance factor
        ctx.fillStyle = '#10b981';
        ctx.font = 'bold 12px Inter';
        ctx.fillText(`BF: ${node.bf}`, node.x, node.y + 40);
    });
}

// Draw Red-Black Tree
function drawRedBlack() {
    const canvas = document.getElementById('rbCanvas');
    const ctx = canvas.getContext('2d');
    
    const nodes = [
        { x: 250, y: 40, value: 40, color: 'black' },
        { x: 150, y: 120, value: 20, color: 'red' },
        { x: 350, y: 120, value: 60, color: 'red' },
        { x: 80, y: 200, value: 10, color: 'black' },
        { x: 220, y: 200, value: 30, color: 'black' },
        { x: 280, y: 200, value: 50, color: 'black' },
        { x: 420, y: 200, value: 70, color: 'black' }
    ];

    const edges = [
        [0, 1], [0, 2], [1, 3], [1, 4], [2, 5], [2, 6]
    ];

    // Draw edges
    ctx.strokeStyle = '#f5576c';
    ctx.lineWidth = 2;
    edges.forEach(([from, to]) => {
        ctx.beginPath();
        ctx.moveTo(nodes[from].x, nodes[from].y);
        ctx.lineTo(nodes[to].x, nodes[to].y);
        ctx.stroke();
    });

    // Draw nodes
    nodes.forEach(node => {
        // Outer glow
        const glowColor = node.color === 'red' ? 'rgba(245, 87, 108, 0.3)' : 'rgba(100, 100, 100, 0.3)';
        const gradient = ctx.createRadialGradient(node.x, node.y, 15, node.x, node.y, 30);
        gradient.addColorStop(0, glowColor);
        gradient.addColorStop(1, node.color === 'red' ? 'rgba(245, 87, 108, 0)' : 'rgba(100, 100, 100, 0)');
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 30, 0, Math.PI * 2);
        ctx.fill();

        // Node circle
        if (node.color === 'red') {
            const nodeGradient = ctx.createLinearGradient(node.x - 20, node.y - 20, node.x + 20, node.y + 20);
            nodeGradient.addColorStop(0, '#f093fb');
            nodeGradient.addColorStop(1, '#f5576c');
            ctx.fillStyle = nodeGradient;
        } else {
            ctx.fillStyle = '#1a1a2e';
        }
        
        ctx.beginPath();
        ctx.arc(node.x, node.y, 20, 0, Math.PI * 2);
        ctx.fill();

        // Border
        ctx.strokeStyle = node.color === 'red' ? '#f5576c' : '#666';
        ctx.lineWidth = 2;
        ctx.stroke();

        // Value text
        ctx.fillStyle = 'white';
        ctx.font = 'bold 16px Inter';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(node.value, node.x, node.y);
    });
}

// Initialize all tree drawings
setTimeout(() => {
    drawHeroTree();
    drawBST();
    drawAVL();
    drawRedBlack();
}, 100);
