create table products (
    item_id bigint primary key,
    name varchar(255),
    cost decimal(10, 2),
    description varchar(255),
    make_type integer not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table items (
    item_id bigint primary key,
    name varchar(255),
    cost decimal(10, 2),
    description varchar(255),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table product_items (
    product_id bigint,
    item_id bigint,
    quantity bigint,
    primary key (product_id, item_id),
    foreign key (product_id) references products(item_id),
    foreign key (item_id) references items(item_id)
);
