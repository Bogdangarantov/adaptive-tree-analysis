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
    title: 'AVL-дерева',
    description: 'Строго збалансоване дерево пошуку з фактором балансу ±1. Забезпечує O(log n) для основних операцій завдяки ротаціям.',
    iconClass: 'fas fa-balance-scale',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    delay: '0.1s'
  },
  {
    title: 'Червоно-чорні дерева',
    description: 'Самобалансування через кольорові властивості вузлів. Часто дають швидші вставки й видалення, ніж AVL, але трохи менш строгі за пошуком.',
    iconClass: 'fas fa-circle',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    delay: '0.2s'
  },
  {
    title: 'Splay-дерева',
    description: 'Самоналаштовувані дерева пошуку, які підтягують нещодавно використані вузли ближче до кореня.',
    iconClass: 'fas fa-layer-group',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    delay: '0.3s'
  }
];

export const complexityRows: ComplexityRow[] = [
  {
    treeType: 'AVL-дерево',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: 'Строгий ±1',
    searchClass: 'best',
    insertClass: 'best',
    deleteClass: 'best',
    spaceClass: 'best',
    balanceClass: 'best'
  },
  {
    treeType: 'Червоно-чорне дерево',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: "М'який",
    searchClass: 'best',
    insertClass: 'best',
    deleteClass: 'best',
    spaceClass: 'best',
    balanceClass: 'good'
  },
  {
    treeType: 'Splay-дерево',
    search: 'O(log n)',
    insert: 'O(log n)',
    delete: 'O(log n)',
    space: 'O(n)',
    balance: 'Адаптивний',
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
      'Навчальний проєкт для порівняння AVL, червоно-чорних і Splay-дерев та аналізу їхніх характеристик.'
    ]
  },
  {
    title: 'Технології',
    iconClass: 'fas fa-code',
    lines: ['Vue 3, TypeScript, D3.js', 'Spring Boot, REST API']
  }
];
