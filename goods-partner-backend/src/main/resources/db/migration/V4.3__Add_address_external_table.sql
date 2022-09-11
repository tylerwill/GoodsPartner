CREATE TABLE "grandedolce_addresses" (
    "order_address" VARCHAR(255) NOT NULL,
    "client_name" VARCHAR (255) NOT NULL,
    "valid_address" VARCHAR(255) NOT NULL,
    "latitude" DECIMAL(8, 2) NOT NULL,
    "longitude" DECIMAL(8, 2)NOT NULL,

    PRIMARY KEY (order_address, client_name)
);