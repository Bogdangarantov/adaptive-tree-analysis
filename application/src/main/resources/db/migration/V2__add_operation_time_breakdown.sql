ALTER TABLE experiment_stats
    ADD COLUMN insert_time_ns BIGINT,
    ADD COLUMN search_time_ns BIGINT,
    ADD COLUMN delete_time_ns BIGINT;

ALTER TABLE experiment_stats
    ADD CONSTRAINT experiment_stats_insert_time_ns_chk
        CHECK (insert_time_ns IS NULL OR insert_time_ns >= 0),
    ADD CONSTRAINT experiment_stats_search_time_ns_chk
        CHECK (search_time_ns IS NULL OR search_time_ns >= 0),
    ADD CONSTRAINT experiment_stats_delete_time_ns_chk
        CHECK (delete_time_ns IS NULL OR delete_time_ns >= 0);
