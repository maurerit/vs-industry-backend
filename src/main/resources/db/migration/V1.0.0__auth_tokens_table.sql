create table auth_tokens (
    principal varchar(255) not null primary key,
    token text not null,
    refresh_token text not null,
    created_at timestamp not null default now(),
    expires_at timestamp not null
);

