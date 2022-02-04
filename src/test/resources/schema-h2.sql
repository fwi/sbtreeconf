-- Note on table- and column-names:
-- Postgresql converts unquoted names to lowercase,
-- everybody else converts unquoted names to UPPERCASE.
-- To prevent maintenance hell, always use lower_case names with under_scores.
-- For H2 database, mimic Postgresql with jdbc-url parameter "DATABASE_TO_LOWER=TRUE"
-- so that unquoted names can be used in queries and DDL statements. 

create table ice_cream (
id         identity,
created    timestamp not null default current_timestamp,
modified   timestamp not null default current_timestamp on update current_timestamp,
flavor     varchar(128),
shape      varchar(128)
);
