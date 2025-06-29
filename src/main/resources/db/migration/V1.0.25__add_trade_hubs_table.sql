CREATE TABLE trade_hubs (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    system_id BIGINT NOT NULL,
    region_id BIGINT NOT NULL,
    CONSTRAINT uk_trade_hubs_system_id UNIQUE (system_id)
);

-- Insert the trade hubs from the TradeHub enum
INSERT INTO trade_hubs (id, name, system_id, region_id) VALUES
(1, 'JITA', 30000142, 10000002),
(2, 'AMARR', 30002187, 10000043),
(3, 'RENS', 30002510, 10000030),
(4, 'HEK', 30002053, 10000042),
(5, 'DODIXIE', 30002659, 10000032);
