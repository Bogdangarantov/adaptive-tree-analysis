import type { components } from '@dto/api-types';

type PlaygroundTreeNode = components['schemas']['PlaygroundTreeNode'];

export interface PositionedTreeNode {
  key: number;
  x: number;
  y: number;
  depth: number;
  color?: string | null;
  height?: number | null;
}

export interface PositionedTreeEdge {
  fromX: number;
  fromY: number;
  toX: number;
  toY: number;
}

export interface TreeLayout {
  width: number;
  height: number;
  nodes: PositionedTreeNode[];
  edges: PositionedTreeEdge[];
}

const HORIZONTAL_GAP = 76;
const VERTICAL_GAP = 88;
const PADDING_X = 48;
const PADDING_Y = 40;

export function layoutTree(root: PlaygroundTreeNode | null | undefined): TreeLayout {
  if (!root || typeof root.key !== 'number') {
    return {
      width: 420,
      height: 220,
      nodes: [],
      edges: []
    };
  }

  const nodes: PositionedTreeNode[] = [];
  const edges: PositionedTreeEdge[] = [];
  let currentLeafIndex = 0;
  let maxDepth = 0;

  const placeNode = (node: PlaygroundTreeNode, depth: number): PositionedTreeNode => {
    const leftPlaced = node.left ? placeNode(node.left, depth + 1) : null;
    const ownLeafIndex = currentLeafIndex++;
    const rightPlaced = node.right ? placeNode(node.right, depth + 1) : null;

    let x = PADDING_X + ownLeafIndex * HORIZONTAL_GAP;
    if (leftPlaced && rightPlaced) {
      x = (leftPlaced.x + rightPlaced.x) / 2;
    } else if (leftPlaced) {
      x = leftPlaced.x + HORIZONTAL_GAP / 2;
    } else if (rightPlaced) {
      x = rightPlaced.x - HORIZONTAL_GAP / 2;
    }

    const positioned: PositionedTreeNode = {
      key: node.key!,
      x,
      y: PADDING_Y + depth * VERTICAL_GAP,
      depth,
      color: node.color,
      height: node.height
    };

    nodes.push(positioned);
    maxDepth = Math.max(maxDepth, depth);

    if (leftPlaced) {
      edges.push({
        fromX: positioned.x,
        fromY: positioned.y,
        toX: leftPlaced.x,
        toY: leftPlaced.y
      });
    }

    if (rightPlaced) {
      edges.push({
        fromX: positioned.x,
        fromY: positioned.y,
        toX: rightPlaced.x,
        toY: rightPlaced.y
      });
    }

    return positioned;
  };

  placeNode(root, 0);

  return {
    width: Math.max(420, PADDING_X * 2 + currentLeafIndex * HORIZONTAL_GAP),
    height: Math.max(220, PADDING_Y * 2 + (maxDepth + 1) * VERTICAL_GAP),
    nodes,
    edges
  };
}
