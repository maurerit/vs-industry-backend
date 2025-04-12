create table warehouse_items(
    item_id bigint primary key,
    quantity bigint not null,
    cost_per_item decimal(10, 2) not null
)
