create table product_items_tmp as select * from product_items;
create table product_invention_items_tmp as select * from product_invention_items;

drop table product_items;
drop table product_invention_items;

create table product_items (
    product_id bigint not null,
    item_id bigint not null,
    quantity bigint not null
);

alter table product_items add constraint product_items_PK primary key (product_id, item_id);
alter table product_items add constraint product_items_FK1 foreign key (product_id) references products(item_id);
alter table product_items add constraint product_items_FD2 foreign key (item_id) references inv_types(type_id);

create table product_invention_items (
    product_id bigint not null,
    item_id bigint not null,
    quantity integer not null
);

alter table product_invention_items add constraint product_invention_items_PK primary key (product_id, item_id);
alter table product_invention_items add constraint product_invention_items_FK1 foreign key (product_id) references products(item_id);
alter table product_invention_items add constraint product_invention_items_FK2 foreign key (item_id) references inv_types(type_id);

insert into product_items (product_id, item_id, quantity)
select product_id, item_id, quantity from product_items_tmp;

insert into product_invention_items (product_id, item_id, quantity)
select product_id, item_id, quantity from product_invention_items_tmp;

drop table product_items_tmp;
drop table product_invention_items_tmp;