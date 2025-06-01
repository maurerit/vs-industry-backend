CREATE TABLE market_stats (
    item_id BIGINT NOT NULL,
    system_id BIGINT NOT NULL,
    sell_minimum DECIMAL(20, 2),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE market_stats
    ADD CONSTRAINT pk_market_stats PRIMARY KEY (item_id, system_id);

-- Create an index on item_id for faster lookups
CREATE INDEX idx_market_stats_item_id ON market_stats (item_id);

-- Create an index on system_id for faster lookups
CREATE INDEX idx_market_stats_system_id ON market_stats (system_id);