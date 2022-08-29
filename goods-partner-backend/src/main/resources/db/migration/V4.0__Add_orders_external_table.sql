CREATE TABLE "grandedolce_orders"(
"id" INTEGER PRIMARY KEY,
"order_number" VARCHAR(255) NOT NULL,
"created_date" DATE NOT NULL,
"client_name" VARCHAR(255) NOT NULL,
"address" VARCHAR(255) NOT NULL,
"manager" VARCHAR(255) NOT NULL,
"order_weight" DECIMAL(8, 2) NOT NULL,
"products" JSONB NOT NULL,
"valid_address" BOOLEAN NOT NULL
);

CREATE SEQUENCE orders_external_id_sequence OWNED BY grandedolce_orders.id;
ALTER TABLE grandedolce_orders ALTER COLUMN id SET DEFAULT nextval('orders_external_id_sequence');
ALTER SEQUENCE orders_external_id_sequence INCREMENT BY 50;