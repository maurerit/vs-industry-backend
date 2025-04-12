alter table warehouse_items
  drop column cost_per_item;

alter table warehouse_items
  add cost_per_item decimal(20, 2) not null default 0;
