CREATE TABLE "store"(
    "id" UUID PRIMARY KEY,
    "name" VARCHAR (255) NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "latitude" DECIMAL(8, 2) NOT NULL,
    "longitude" DECIMAL(8, 2) NOT NULL
);

ALTER TABLE routes DROP COLUMN "store_name";
ALTER TABLE routes DROP COLUMN "store_address";

ALTER TABLE routes ADD COLUMN "store_id" UUID;

ALTER TABLE routes
    ADD CONSTRAINT FK_ROUTES_ON_STORE_ID FOREIGN KEY (store_id) REFERENCES store (id);