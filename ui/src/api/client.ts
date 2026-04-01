import type { components } from '@dto/api-types';

type HealthResponse = components['schemas']['HealthResponse'];
type AvlOperationRequest = components['schemas']['AvlOperationRequest'];
type AvlOperationResponse = components['schemas']['AvlOperationResponse'];
export type PlaygroundOperationRequest = components['schemas']['PlaygroundOperationRequest'];
export type PlaygroundOperationResponse = components['schemas']['PlaygroundOperationResponse'];
export type BenchmarkSummaryResponse = components['schemas']['BenchmarkSummaryResponse'];
export interface BenchmarkHeightGrowthPointResponse {
  datasetSize: number;
  averageTreeHeight: number;
  runCount: number;
}

export interface BenchmarkHeightGrowthSeriesResponse {
  treeType: string;
  points: BenchmarkHeightGrowthPointResponse[];
}

export interface BenchmarkHeightGrowthResponse {
  totalRuns: number;
  series: BenchmarkHeightGrowthSeriesResponse[];
}

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
    insertTimeNs?: number | null;
    searchTimeNs?: number | null;
    deleteTimeNs?: number | null;
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

export async function getBenchmarkSummary(): Promise<BenchmarkSummaryResponse> {
  const response = await fetch(`${API_BASE}/benchmark/summary`);
  if (!response.ok) {
    throw new Error(`Benchmark summary request failed with status ${response.status}`);
  }
  return response.json();
}

export async function getBenchmarkHeightGrowth(): Promise<BenchmarkHeightGrowthResponse> {
  const response = await fetch(`${API_BASE}/benchmark/height-growth`);
  if (!response.ok) {
    throw new Error(`Benchmark height-growth request failed with status ${response.status}`);
  }
  return response.json();
}

export async function runPlaygroundOperation(payload: PlaygroundOperationRequest): Promise<PlaygroundOperationResponse> {
  const response = await fetch(`${API_BASE}/trees/playground/operations`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`Playground operation failed with status ${response.status}`);
  }

  return response.json();
}
