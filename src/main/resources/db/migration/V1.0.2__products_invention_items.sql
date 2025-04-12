create table product_invention_items (
    product_id bigint,
    item_id bigint,
    quantity integer,
    primary key (product_id, item_id),
    foreign key (product_id) references products(item_id),
    foreign key (item_id) references items(item_id)
);
