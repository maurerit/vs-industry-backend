create table market_prices (
    type_id bigint not null,
    adjusted_price decimal(19, 4),
    average_price decimal(19, 4)
);

alter table market_prices add constraint market_prices_id primary key (type_id);