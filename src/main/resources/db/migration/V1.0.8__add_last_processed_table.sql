create table last_processed (
    object_id   bigint       not null,
    object_type varchar(255) not null,
    primary key (object_id, object_type)
);
