create table users (
    character_id bigint not null,
    character_name varchar(255) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

alter table users add constraint users_PK primary key (character_id);

create table roles (
    role_id bigint not null,
    role_name varchar(255) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

alter table roles add constraint roles_PK primary key (role_id);

create table user_roles (
    character_id bigint not null,
    role_id bigint not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

alter table user_roles add constraint user_roles_PK primary key (character_id, role_id);
alter table user_roles add constraint user_roles_FK1 foreign key (character_id) references users (character_id);
alter table user_roles add constraint user_roles_FK2 foreign key (role_id) references roles (role_id);

insert into roles (role_id, role_name) values
(1, 'ADMIN'),
(2, 'USER');