create table contract_headers (
    acceptor_id bigint,
    assignee_id bigint,
    availability varchar(255),
    buyout double,
    collateral double,
    contract_id bigint primary key,
    date_accepted varchar(255),
    date_completed varchar(255),
    date_expired varchar(255),
    date_issued varchar(255),
    days_to_complete bigint,
    end_location_id bigint,
    for_corporation boolean,
    issuer_corporation_id bigint,
    issuer_id bigint,
    price double,
    reward double,
    start_location_id bigint,
    status varchar(255),
    title varchar(255),
    type varchar(255),
    volume double,

    PRIMARY KEY (contract_id)
);

create table contract_details (
    contract_id bigint,
    record_id bigint,
    type_id bigint,
    is_included boolean,
    is_singleton boolean,
    quantity bigint,
    raw_quantity bigint,

    primary key (contract_id, record_id)
);