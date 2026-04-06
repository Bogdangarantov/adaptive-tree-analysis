<template>
  <section class="section viz-section" id="visualization">
    <div class="container">
      <h2 class="section-title fade-in">Візуалізації дерев</h2>
      <p class="section-description fade-in">Інтерактивні візуальні представлення різних деревних структур</p>

      <div class="viz-grid">
        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-balance-scale"></i> AVL-дерево</h3>
          <canvas id="avlCanvas" width="500" height="350"></canvas>
          <p class="viz-description">Самобалансування з відображенням факторів балансу</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-circle"></i> Червоно-чорне дерево</h3>
          <canvas id="rbCanvas" width="500" height="350"></canvas>
          <p class="viz-description">Кольорове кодування вузлів із дотриманням red-black інваріантів</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-layer-group"></i> Splay-дерево</h3>
          <canvas id="bstCanvas" width="500" height="350"></canvas>
          <p class="viz-description">Самоналаштовувана структура, що підтягує нещодавно використані вузли до кореня.</p>
        </div>
      </div>
    </div>
  </section>
</template>
