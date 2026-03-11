-- Benchmark schema for adaptive-tree-analysis
-- PostgreSQL

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE datasets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    distribution_type TEXT NOT NULL,
    size INTEGER NOT NULL,
    seed BIGINT,
    payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT datasets_distribution_type_chk CHECK (
        distribution_type IN (
            'random',
            'sorted',
            'reverse_sorted',
            'almost_sorted',
            'zipf',
            'custom'
        )
    ),
    CONSTRAINT datasets_size_positive_chk CHECK (size > 0)
);

CREATE TABLE experiments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dataset_id UUID NOT NULL REFERENCES datasets(id) ON DELETE RESTRICT,
    title TEXT NOT NULL,
    operations_profile JSONB NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending',
    hardware_info JSONB,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT experiments_status_chk CHECK (status IN ('pending', 'running', 'completed', 'failed')),
    CONSTRAINT experiments_time_order_chk CHECK (
        finished_at IS NULL OR started_at IS NULL OR finished_at >= started_at
    )
);

CREATE TABLE experiment_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    tree_type TEXT NOT NULL,
    algorithm_version TEXT,
    execution_time_ns BIGINT NOT NULL,
    operation_count INTEGER NOT NULL,
    rotation_count INTEGER NOT NULL,
    tree_height INTEGER NOT NULL,
    rebalances_count INTEGER,
    avg_node_depth NUMERIC(10,4),
    max_node_depth INTEGER,
    memory_bytes BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT experiment_stats_tree_type_chk CHECK (tree_type IN ('avl', 'red_black', 'splay')),
    CONSTRAINT experiment_stats_execution_time_ns_chk CHECK (execution_time_ns >= 0),
    CONSTRAINT experiment_stats_operation_count_chk CHECK (operation_count >= 0),
    CONSTRAINT experiment_stats_rotation_count_chk CHECK (rotation_count >= 0),
    CONSTRAINT experiment_stats_tree_height_chk CHECK (tree_height >= 0),
    CONSTRAINT experiment_stats_rebalances_count_chk CHECK (rebalances_count IS NULL OR rebalances_count >= 0),
    CONSTRAINT experiment_stats_avg_node_depth_chk CHECK (avg_node_depth IS NULL OR avg_node_depth >= 0),
    CONSTRAINT experiment_stats_max_node_depth_chk CHECK (max_node_depth IS NULL OR max_node_depth >= 0),
    CONSTRAINT experiment_stats_memory_bytes_chk CHECK (memory_bytes IS NULL OR memory_bytes >= 0),
    CONSTRAINT experiment_stats_experiment_tree_uniq UNIQUE (experiment_id, tree_type)
);

CREATE INDEX idx_datasets_distribution_size ON datasets(distribution_type, size);
CREATE INDEX idx_experiments_dataset_status_created_at ON experiments(dataset_id, status, created_at DESC);
CREATE INDEX idx_experiment_stats_tree_execution_time ON experiment_stats(tree_type, execution_time_ns);
CREATE INDEX idx_experiment_stats_created_at ON experiment_stats(created_at DESC);
