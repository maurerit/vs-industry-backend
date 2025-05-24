create table extra_costs_tmp as select * from extra_costs;

drop table extra_costs;

create table extra_costs (
     item_id bigint not null,
     cost_type varchar(255) not null,
     cost double not null
);

alter table extra_costs add constraint extra_costs_PK primary key (item_id, cost_type);
alter table extra_costs add constraint extra_costs_FK1 foreign key (item_id) references inv_types(type_id);

insert into extra_costs (item_id, cost_type, cost)
select item_id, 'default' as cost_type, cost from extra_costs_tmp;

drop table extra_costs_tmp;
