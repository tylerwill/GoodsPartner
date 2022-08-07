CREATE TABLE "routes"(
    "id" INTEGER NOT NULL,
    "status" VARCHAR(255) NOT NULL,
    "date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "distance" DECIMAL(8, 2) NOT NULL,
    "estimated_time" TIME(0) WITHOUT TIME ZONE NOT NULL,
    "start_time" TIMESTAMP(0) WITHOUT TIME ZONE,
    "finish_time" TIMESTAMP(0) WITHOUT TIME ZONE,
    "spent_time" TIME(0) WITHOUT TIME ZONE,
    "route_link" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "routes" ADD PRIMARY KEY("id");


ALTER TABLE "orders" ADD COLUMN "route_id" INTEGER;

ALTER TABLE
    "orders" ADD CONSTRAINT "orders_route_id_foreign" FOREIGN KEY("route_id") REFERENCES "routes"("id");

CREATE SEQUENCE routes_sequence OWNED BY routes.id;

ALTER TABLE routes ALTER COLUMN id SET DEFAULT nextval('routes_sequence');