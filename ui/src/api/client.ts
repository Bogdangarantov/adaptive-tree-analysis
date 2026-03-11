import type { AvlOperationRequest, AvlOperationResponse, HealthResponse } from '@dto/api-types';

const API_BASE = '/api/v1';

export interface BenchmarkQuickRunRequest {
  datasetSize: number;
  distributionType: string;
  insertPercent: number;
  searchPercent: number;
  deletePercent: number;
  repeatCount: number;
}

export interface BenchmarkQuickRunResponse {
  experiment: {
    id: string;
    datasetId: string;
    title: string;
    operationsProfile: string;
    status: string;
    hardwareInfo?: string | null;
    startedAt?: string | null;
    finishedAt?: string | null;
    createdAt: string;
  };
  stats: Array<{
    id: string;
    experimentId: string;
    treeType: string;
    algorithmVersion?: string | null;
    executionTimeNs: number;
    operationCount: number;
    rotationCount: number;
    treeHeight: number;
    rebalancesCount?: number | null;
    avgNodeDepth?: number | null;
    maxNodeDepth?: number | null;
    memoryBytes?: number | null;
    createdAt: string;
  }>;
}

export async function getHealth(): Promise<HealthResponse> {
  const response = await fetch(`${API_BASE}/health`);
  if (!response.ok) {
    throw new Error(`Health request failed with status ${response.status}`);
  }
  return response.json();
}

export async function executeAvlOperation(payload: AvlOperationRequest): Promise<AvlOperationResponse> {
  const response = await fetch(`${API_BASE}/trees/avl/operations`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`AVL operation request failed with status ${response.status}`);
  }

  return response.json();
}

export async function runBenchmarkQuick(payload: BenchmarkQuickRunRequest): Promise<BenchmarkQuickRunResponse> {
  const response = await fetch(`${API_BASE}/benchmark/quick-run`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`Benchmark quick-run failed with status ${response.status}`);
  }

  return response.json();
}
