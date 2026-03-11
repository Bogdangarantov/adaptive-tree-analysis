<template>
  <section class="section demo-section" id="demo">
    <div class="container">
      <h2 class="section-title fade-in">Interactive Tree Demo</h2>
      <p class="section-description fade-in">Build and manipulate your own search tree in real-time</p>

      <div class="demo-wrapper">
        <div class="demo-controls glass-card fade-in">
          <h3><i class="fas fa-gamepad"></i> Tree Operations</h3>
          <div class="control-group">
            <input type="number" id="nodeValue" placeholder="Enter value..." class="control-input" />
            <button class="control-btn insert-btn" @click="callGlobal('insertNode')">
              <i class="fas fa-plus"></i> Insert
            </button>
          </div>
          <div class="control-group">
            <input type="number" id="searchValue" placeholder="Search value..." class="control-input" />
            <button class="control-btn search-btn" @click="callGlobal('searchNode')">
              <i class="fas fa-search"></i> Search
            </button>
          </div>
          <div class="control-group">
            <input type="number" id="deleteValue" placeholder="Delete value..." class="control-input" />
            <button class="control-btn delete-btn" @click="callGlobal('deleteNode')">
              <i class="fas fa-trash"></i> Delete
            </button>
          </div>
          <button class="control-btn reset-btn" @click="callGlobal('resetTree')">
            <i class="fas fa-redo"></i> Reset Tree
          </button>

          <div class="demo-message" id="demoMessage"></div>
        </div>

        <div class="demo-canvas-wrapper glass-card fade-in">
          <canvas id="demoCanvas" width="700" height="500"></canvas>
        </div>
      </div>

      <div class="stats-dashboard fade-in">
        <div class="stat-card glass-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <i class="fas fa-layer-group"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value" id="treeHeight">0</div>
            <div class="stat-label">Tree Height</div>
          </div>
        </div>

        <div class="stat-card glass-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <i class="fas fa-circle-notch"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value" id="nodeCount">0</div>
            <div class="stat-label">Node Count</div>
          </div>
        </div>

        <div class="stat-card glass-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <i class="fas fa-balance-scale"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value" id="balanceFactor">0</div>
            <div class="stat-label">Balance Factor</div>
          </div>
        </div>

        <div class="stat-card glass-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <i class="fas fa-calculator"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value" id="operationCount">0</div>
            <div class="stat-label">Operations</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { callGlobal } from '../../composables/useGlobalFunction';
</script>
