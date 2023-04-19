create table if not exists attachments
(
    id                              uuid            not null
    primary key,
    file_type                       varchar(30),
    full_path                       varchar(255)    not null,
    file_name_original              varchar(255)    not null,
    created_at                      timestamp(0)    not null,
    task_id                         bigint
    constraint fk_attachments_on_task_id
    references tasks
    );



