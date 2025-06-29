create table invention_first_seen (
    object_id   bigint       not null,
    object_type varchar(255) not null,
    cost        double       not null,
    primary key (object_id, object_type)
);
