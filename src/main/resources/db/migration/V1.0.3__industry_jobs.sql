create table industry_jobs (
    job_id bigint primary key,
    blueprint_type_id bigint,
    product_type_id bigint,
    activity_id integer,
    licensed_runs integer,
    runs integer,
    probability real,
    cost real,
    status varchar(50),
    successful_runs bigint,
    start_date timestamp,
    end_date timestamp
)
