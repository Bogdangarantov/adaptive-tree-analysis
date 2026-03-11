export type TreeType = 'avl' | 'red-black' | 'splay';

export interface TreeNode {
  id: string;
  key: number;
  left: TreeNode | null;
  right: TreeNode | null;
  color?: 'RED' | 'BLACK';
  height?: number;
}

export interface TreeSnapshot {
  treeType: TreeType;
  root: TreeNode | null;
}

export interface TreeStepEvent {
  type: string;
  nodeId?: string;
  metadata?: Record<string, string | number | boolean | null>;
}
