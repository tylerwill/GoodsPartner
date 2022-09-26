CREATE TABLE "deliveries"(
    "id" UUID PRIMARY KEY,
    "delivery_date" DATE NOT NULL,
    "status" VARCHAR(9) NOT NULL
);

CREATE TABLE "car_loads"(
     "id" UUID PRIMARY KEY,
     "delivery_id" UUID NOT NULL,
     "car_id" INTEGER NOT NULL
);

ALTER TABLE routes ADD COLUMN "delivery_id" UUID;
ALTER TABLE grandedolce_orders ADD COLUMN "delivery_id" UUID;
