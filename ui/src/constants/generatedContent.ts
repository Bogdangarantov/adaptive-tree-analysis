export interface OverviewCard {
  title: string;
  description: string;
  iconClass: string;
  gradient: string;
  delay: string;
}

export interface ComplexityRow {
  treeType: string;
  search: string;
  insert: string;
  delete: string;
  space: string;
  balance: string;
  searchClass: string;
  insertClass: string;
  deleteClass: string;
  spaceClass: string;
  balanceClass: string;
}

export interface FooterBlock {
  title: string;
  iconClass: string;
  lines: string[];
}

export const overviewCards: OverviewCard[] = [
  {
    title: 'AVL Trees',
    description: 'Strictly balanced search tree with balance factor ±1. Ensures O(log n) operations through rotations.',
    iconClass: 'fas fa-balance-scale',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    delay: '0.1s'
  },
  {
    title: 'Red-Black Trees',
    description: 'Self-balancing using color properties. Faster insertions/deletions than AVL, slightly slower searches.',
    iconClass: 'fas fa-circle',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    delay: '0.2s'
  },
  {
    title: 'Extensible Trees',
    description: 'Extensible search trees optimized for large datasets and block-oriented storage.',
    iconClass: 'fas fa-layer-group',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    delay: '0.3s'
  }
];

export const complexityRows: ComplexityRow[] = [
  {
    treeType: 'AVL Tree',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: 'Strict ±1',
    searchClass: 'best',
    insertClass: 'best',
    deleteClass: 'best',
    spaceClass: 'best',
    balanceClass: 'best'
  },
  {
    treeType: 'Red-Black Tree',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: 'Relaxed',
    searchClass: 'best',
    insertClass: 'best',
    deleteClass: 'best',
    spaceClass: 'best',
    balanceClass: 'good'
  },
  {
    treeType: 'Extensible Tree',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: 'Extensible',
    searchClass: 'best',
    insertClass: 'best',
    deleteClass: 'best',
    spaceClass: 'best',
    balanceClass: 'best'
  }
];

export const footerBlocks: FooterBlock[] = [
  {
    title: 'Adaptive Tree Analysis',
    iconClass: 'fas fa-project-diagram',
    lines: [
      'A comprehensive coursework project comparing AVL trees, Red-Black trees, and extensible trees and their performance characteristics.'
    ]
  },
  {
    title: 'Course Information',
    iconClass: 'fas fa-graduation-cap',
    lines: ['Data Structures & Algorithms', 'Academic Year 2024-2025']
  },
  {
    title: 'Technologies',
    iconClass: 'fas fa-code',
    lines: ['HTML5, CSS3, JavaScript', 'Chart.js, Canvas API']
  }
];
