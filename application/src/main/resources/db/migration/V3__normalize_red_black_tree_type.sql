UPDATE experiment_stats
SET tree_type = 'red-black'
WHERE tree_type = 'red_black';

ALTER TABLE experiment_stats
    DROP CONSTRAINT IF EXISTS experiment_stats_tree_type_chk;

ALTER TABLE experiment_stats
    ADD CONSTRAINT experiment_stats_tree_type_chk
    CHECK (tree_type IN ('avl', 'red-black', 'splay'));
