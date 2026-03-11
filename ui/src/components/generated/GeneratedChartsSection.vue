<template>
  <section class="section charts-section" id="charts">
    <div class="container">
      <h2 class="section-title fade-in">Performance Analysis</h2>
      <p class="section-description fade-in">Comparative analysis of time complexity and efficiency metrics</p>

      <div class="charts-grid">
        <div class="chart-card glass-card fade-in">
          <h3><i class="fas fa-chart-bar"></i> Operation Time Complexity</h3>
          <div style="height: 350px; position: relative;">
            <canvas id="complexityChart"></canvas>
          </div>
        </div>

        <div class="chart-card glass-card fade-in">
          <h3><i class="fas fa-chart-line"></i> Tree Height Growth</h3>
          <div style="height: 350px; position: relative;">
            <canvas id="heightChart"></canvas>
          </div>
        </div>

        <div class="chart-card glass-card fade-in full-width">
          <h3><i class="fas fa-chart-area"></i> Multi-Metric Comparison</h3>
          <div style="height: 400px; position: relative;">
            <canvas id="radarChart"></canvas>
          </div>
        </div>
      </div>

      <div class="complexity-table-wrapper fade-in">
        <h3><i class="fas fa-table"></i> Algorithm Complexity Comparison</h3>
        <div class="table-container glass-card">
          <table class="complexity-table">
            <thead>
              <tr>
                <th>Tree Type</th>
                <th>Search</th>
                <th>Insert</th>
                <th>Delete</th>
                <th>Space</th>
                <th>Balance</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in complexityRows" :key="row.treeType">
                <td><strong>{{ row.treeType }}</strong></td>
                <td><span class="complexity-badge" :class="row.searchClass">{{ row.search }}</span></td>
                <td><span class="complexity-badge" :class="row.insertClass">{{ row.insert }}</span></td>
                <td><span class="complexity-badge" :class="row.deleteClass">{{ row.delete }}</span></td>
                <td><span class="complexity-badge" :class="row.spaceClass">{{ row.space }}</span></td>
                <td><span class="complexity-badge" :class="row.balanceClass">{{ row.balance }}</span></td>
              </tr>
            </tbody>
          </table>
          <p class="table-note">* Amortized time complexity</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { complexityRows } from '../../constants/generatedContent';
</script>
