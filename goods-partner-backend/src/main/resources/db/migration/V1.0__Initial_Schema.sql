create sequence routes_sequence
    increment by 50;

create sequence cars_id_sequence
    increment by 50;

create sequence orders_external_id_sequence
    increment by 50;

create sequence users_id_sequence
    increment by 50;

create sequence route_points_sequence
    increment by 50;

create sequence tasks_id_sequence
    increment by 50;

create table if not exists flyway_schema_history
(
    installed_rank integer                 not null
    constraint flyway_schema_history_pk
    primary key,
    version        varchar(50),
    description    varchar(200)            not null,
    type           varchar(20)             not null,
    script         varchar(1000)           not null,
    checksum       integer,
    installed_by   varchar(100)            not null,
    installed_on   timestamp default now() not null,
    execution_time integer                 not null,
    success        boolean                 not null
    );

create index if not exists flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table if not exists users
(
    id        integer default nextval('users_id_sequence'::regclass) not null
    primary key,
    user_name varchar(255)                                           not null,
    email     varchar(255)                                           not null
    unique,
    role      varchar(255)                                           not null,
    enabled   boolean                                                not null
    );

alter sequence users_id_sequence owned by users.id;

create table if not exists cars
(
    id              integer default nextval('cars_id_sequence'::regclass) not null
    primary key,
    name            varchar(255)                                          not null,
    licence_plate   varchar(255)                                          not null,
    weight_capacity integer                                               not null,
    cooler          boolean                                               not null,
    available       boolean                                               not null,
    travel_cost     integer                                               not null,
    deleted         boolean default false                                 not null,
    user_id         integer
    constraint fk_cars_on_user_id
    references users
    );

alter sequence cars_id_sequence owned by cars.id;

create table if not exists grandedolce_addresses
(
    order_address varchar(255) not null,
    client_name   varchar(255) not null,
    valid_address varchar(255),
    latitude      numeric(17, 13),
    longitude     numeric(17, 13),
    status        varchar(20),
    primary key (order_address, client_name)
    );

create table if not exists deliveries
(
    id               uuid                  not null
    primary key,
    delivery_date    date                  not null,
    status           varchar(9)            not null,
    deleted          boolean default false not null,
    formation_status varchar(50),
    order_count      integer,
    route_count      integer
    );

create table if not exists car_loads
(
    id          uuid    not null
    primary key,
    delivery_id uuid    not null
    constraint fk_car_loads_on_delivery_id
    references deliveries,
    car_id      integer not null
    constraint fk_car_loads_on_car_id
    references cars
);

create table if not exists deliveries_history
(
    id          uuid         not null
    primary key,
    delivery_id uuid         not null
    constraint fk_deliveries_history_on_delivery_id
    references deliveries,
    created_at  timestamp(0) not null,
    role        varchar(255) not null,
    user_email  varchar(255) not null,
    action      varchar(255) not null
    );

create table if not exists store
(
    id        uuid          not null
    primary key,
    name      varchar(255)  not null,
    address   varchar(255)  not null,
    latitude  numeric(8, 2) not null,
    longitude numeric(8, 2) not null
    );

create table if not exists routes
(
    id             integer       default nextval('routes_sequence'::regclass) not null
    primary key,
    status         varchar(255)                                               not null,
    distance       numeric(8, 2)                                              not null,
    start_time     timestamp(0),
    finish_time    timestamp(0),
    total_weight   numeric(8, 2) default 0                                    not null,
    total_points   integer       default 0                                    not null,
    total_orders   integer       default 0                                    not null,
    estimated_time bigint        default 0                                    not null,
    spent_time     bigint        default 0,
    optimization   boolean       default true                                 not null,
    delivery_id    uuid
    constraint fk_routes_on_delivery_id
    references deliveries,
    car_id         integer
    constraint fk_routes_on_car_id
    references cars,
    store_id       uuid
    constraint fk_routes_on_store_id
    references store
    );

alter sequence routes_sequence owned by routes.id;

create table if not exists route_points
(
    id                       bigint  default nextval('route_points_sequence'::regclass) not null
    primary key,
    status                   varchar(255)                                               not null,
    client_name              varchar(255)                                               not null,
    address                  varchar(255)                                               not null,
    address_total_weight     numeric(8, 2)                                              not null,
    route_point_distant_time integer,
    completed_at             timestamp(0),
    expected_arrival         time(0),
    expected_completion      time(0),
    delivery_start           time(0),
    delivery_end             time(0),
    route_id                 integer
    constraint fk_route_points_on_route_id
    references routes,
    matching_time            boolean default false,
    map_point                jsonb                                                      not null
    );

alter sequence route_points_sequence owned by route_points.id;

create table if not exists grandedolce_orders
(
    id              integer     default nextval('orders_external_id_sequence'::regclass) not null
    primary key,
    order_number    varchar(255)                                                         not null,
    shipping_date   date                                                                 not null,
    client_name     varchar(255)                                                         not null,
    address         varchar(255)                                                         not null,
    manager         varchar(255)                                                         not null,
    order_weight    numeric(8, 2)                                                        not null,
    products        jsonb                                                                not null,
    ref_key         varchar(36) default ''::character varying                            not null,
    comment         text        default ''::character varying,
    delivery_id     uuid
    constraint fk_grandedolce_orders_on_delivery_id
    references deliveries,
    car_load_id     uuid
    constraint fk_grandedolce_orders_on_car_load_id
    references car_loads,
    frozen          boolean     default false                                            not null,
    delivery_start  time,
    delivery_finish time,
    excluded        boolean     default false,
    dropped         boolean     default false,
    delivery_type   varchar(20) default 'REGULAR'::character varying                     not null,
    reschedule_date date,
    route_point_id  bigint
    constraint fk_grandedolce_orders_on_route_point_id
    references route_points,
    exclude_reason  varchar(2000),
    map_point       jsonb                                                                not null
    );

alter sequence orders_external_id_sequence owned by grandedolce_orders.id;

create table if not exists settings
(
    group_id    varchar(255)  not null,
    category_id varchar(255)  not null,
    properties  varchar(1000) not null,
    primary key (group_id, category_id)
    );

create table if not exists tasks
(
    id             bigint default nextval('tasks_id_sequence'::regclass) not null
    primary key,
    description    varchar                                               not null,
    execution_date date,
    map_point      jsonb,
    car_id         integer                                               not null
    constraint fk_task_on_car_id
    references cars
    );

alter sequence tasks_id_sequence owned by tasks.id;

