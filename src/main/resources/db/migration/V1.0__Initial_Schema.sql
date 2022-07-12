CREATE TABLE "clients"(
    "id" INTEGER PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE "addresses"(
    "id" INTEGER PRIMARY KEY,
    "address" VARCHAR(255) NOT NULL,
    "client_id" INTEGER NOT NULL
);

CREATE TABLE "orders"(
    "id" INTEGER PRIMARY KEY,
    "number" INTEGER NOT NULL,
    "created_date" DATE NOT NULL,
	"shipping_date" DATE NOT NULL,
    "address_id" INTEGER NOT NULL,
    "manager_id" INTEGER NOT NULL
);

CREATE TABLE "products"(
    "id" INTEGER PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "kg" DECIMAL(8, 2) NOT NULL,
    "store_id" INTEGER NOT NULL
);

CREATE TABLE "orders_products"(
	"id" INTEGER PRIMARY KEY,
    "order_id" INTEGER NOT NULL,
    "product_id" INTEGER NOT NULL,
    "count" INTEGER NOT NULL
);


CREATE TABLE "stores"(
    "id" INTEGER PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255) NOT NULL
);

CREATE TABLE "managers"(
    "id" INTEGER PRIMARY KEY,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name" VARCHAR(255) NOT NULL,
    "phone_number" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) NOT NULL
);


ALTER TABLE
    "orders_products" ADD CONSTRAINT "order_products_orders_id_foreign" FOREIGN KEY("order_id") REFERENCES "orders"("id");
ALTER TABLE
    "orders_products" ADD CONSTRAINT "orders_products_products_id_foreign" FOREIGN KEY("product_id") REFERENCES "products"("id");
ALTER TABLE
    "addresses" ADD CONSTRAINT "addresses_client_id_foreign" FOREIGN KEY("client_id") REFERENCES "clients"("id");
ALTER TABLE
    "orders" ADD CONSTRAINT "orders_address_id_foreign" FOREIGN KEY("address_id") REFERENCES "addresses"("id");
ALTER TABLE
    "orders" ADD CONSTRAINT "orders_manager_id_foreign" FOREIGN KEY("manager_id") REFERENCES "managers"("id");