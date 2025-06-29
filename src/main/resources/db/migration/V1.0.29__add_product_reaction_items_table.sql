create table product_reaction_items (
    product_id bigint not null,
    item_id bigint not null,
    quantity bigint not null
);

alter table product_reaction_items add constraint product_reaction_items_PK primary key (product_id, item_id);
alter table product_reaction_items add constraint product_reaction_items_FK1 foreign key (product_id) references products(item_id);
alter table product_reaction_items add constraint product_reaction_items_FK2 foreign key (item_id) references inv_types(type_id);