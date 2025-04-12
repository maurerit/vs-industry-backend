create table journal_entries (
    id bigint primary key,
    amount real,
    balance real,
    date timestamp,
    description text,
    first_party_id bigint,
    second_party_id bigint,
    reason text,
    ref_type text,
    division_id integer
);
