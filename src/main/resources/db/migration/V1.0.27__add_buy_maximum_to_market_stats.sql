-- Add buy_maximum column to market_stats table
ALTER TABLE market_stats
    ADD COLUMN buy_maximum DECIMAL(20, 2);