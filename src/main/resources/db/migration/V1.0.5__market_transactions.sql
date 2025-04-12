create table market_transactions(
    transaction_id bigint primary key,
    client_id bigint,
    date timestamp,
    is_buy bit,
    journal_ref_id bigint,
    location_id bigint,
    quantity int,
    type_id bigint,
    unit_price decimal(20, 4),
    division_id integer
)
