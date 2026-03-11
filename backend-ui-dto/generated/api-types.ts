/**
 * AUTO-GENERATED DTO TYPES (bootstrap)
 * Source: application/src/main/resources/openapi/adaptive-tree-analysis.openapi.yaml
 * Do not edit manually. Regenerate via: npm run generate (in backend-ui-dto)
 */

export interface components {
  schemas: {
    HealthResponse: {
      status: string;
      service: string;
    };
    TreeOperationType: 'INSERT' | 'DELETE' | 'SEARCH';
    AvlOperationRequest: {
      operation: components['schemas']['TreeOperationType'];
      key: number;
    };
    TreeStepEvent: {
      type: string;
      nodeKey?: number | null;
      metadata?: Record<string, string | number | boolean | null> | null;
    };
    TreeMetrics: {
      executionTimeNs: number;
      rotationCount: number;
      treeHeight: number;
      operationCount: number;
    };
    AvlOperationResponse: {
      treeType: 'avl';
      operation: components['schemas']['TreeOperationType'];
      inputKey: number;
      steps: components['schemas']['TreeStepEvent'][];
      metrics: components['schemas']['TreeMetrics'];
    };
  };
}

export interface paths {
  '/api/v1/health': {
    get: {
      responses: {
        200: {
          content: {
            'application/json': components['schemas']['HealthResponse'];
          };
        };
      };
    };
  };
  '/api/v1/trees/avl/operations': {
    post: {
      requestBody: {
        content: {
          'application/json': components['schemas']['AvlOperationRequest'];
        };
      };
      responses: {
        200: {
          content: {
            'application/json': components['schemas']['AvlOperationResponse'];
          };
        };
      };
    };
  };
}

export type TreeOperationType = components['schemas']['TreeOperationType'];
export type HealthResponse = components['schemas']['HealthResponse'];
export type AvlOperationRequest = components['schemas']['AvlOperationRequest'];
export type AvlOperationResponse = components['schemas']['AvlOperationResponse'];
export type TreeStepEvent = components['schemas']['TreeStepEvent'];
export type TreeMetrics = components['schemas']['TreeMetrics'];
