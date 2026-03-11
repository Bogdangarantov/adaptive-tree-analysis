<template>
  <section class="section viz-section" id="visualization">
    <div class="container">
      <h2 class="section-title fade-in">Tree Visualizations</h2>
      <p class="section-description fade-in">Interactive visual representations of different tree structures</p>

      <div class="viz-grid">
        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-balance-scale"></i> AVL Tree (Balanced)</h3>
          <canvas id="avlCanvas" width="500" height="350"></canvas>
          <p class="viz-description">Self-balancing with balance factors displayed</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-circle"></i> Red-Black Tree</h3>
          <canvas id="rbCanvas" width="500" height="350"></canvas>
          <p class="viz-description">Color-coded nodes maintaining red-black properties</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-layer-group"></i> Extensible Tree</h3>
          <canvas id="bstCanvas" width="500" height="350"></canvas>
          <p class="viz-description">An extensible tree structure optimized for large blocks of data.</p>
        </div>
      </div>
    </div>
  </section>
</template>
