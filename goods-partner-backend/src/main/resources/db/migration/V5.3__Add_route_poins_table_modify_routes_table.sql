CREATE TABLE "route_points"(
    "id" BIGINT NOT NULL,
    "status" VARCHAR(255) NOT NULL,
    "client_name" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "address_total_weight" DECIMAL(8, 2) NOT NULL,
    "route_point_distant_time" INTEGER NOT NULL,
    "completed_at" TIMESTAMP(0) WITHOUT TIME ZONE,
    "expected_arrival" TIME(0) WITHOUT TIME ZONE,
    "expected_completion" TIME(0) WITHOUT TIME ZONE,
    "delivery_start" TIME(0) WITHOUT TIME ZONE,
    "delivery_end" TIME(0) WITHOUT TIME ZONE,
    "route_id" INTEGER
);

ALTER TABLE "route_points"
    ADD CONSTRAINT FK_ROUTE_POINTS_ON_ROUTE_ID FOREIGN KEY (route_id) REFERENCES "routes" (id);

ALTER TABLE "route_points" ADD PRIMARY KEY("id");

CREATE SEQUENCE route_points_sequence OWNED BY route_points.id;
ALTER TABLE "route_points" ALTER COLUMN id SET DEFAULT nextval('route_points_sequence');
SELECT setval('route_points_sequence', COALESCE(max(id), 1)) FROM "route_points";
ALTER SEQUENCE route_points_sequence INCREMENT BY 50;

ALTER TABLE "grandedolce_orders" ADD COLUMN "route_point_id" BIGINT;
ALTER TABLE "grandedolce_orders"
    ADD CONSTRAINT FK_GRANDEDOLCE_ORDERS_ON_ROUTE_POINT_ID FOREIGN KEY (route_point_id) REFERENCES "route_points" (id);

ALTER TABLE "routes" DROP COLUMN "route_points";
