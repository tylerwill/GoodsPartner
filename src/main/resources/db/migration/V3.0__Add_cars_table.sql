CREATE TABLE "cars"(
   "id" INTEGER PRIMARY KEY,
   "name" VARCHAR(255) NOT NULL,
   "licence_plate" VARCHAR(255) NOT NULL,
   "driver" VARCHAR(255) NOT NULL,
   "weight_capacity" INTEGER NOT NULL,
   "cooler" BOOLEAN NOT NULL,
   "travel_cost" INTEGER NOT NULL
);

CREATE SEQUENCE cars_sequence OWNED BY cars.id;

ALTER TABLE cars ALTER COLUMN id SET DEFAULT nextval('cars_sequence');