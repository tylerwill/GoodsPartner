ALTER TABLE grandedolce_orders
    DROP COLUMN valid_address;
ALTER TABLE grandedolce_orders
    ADD COLUMN is_frozen BOOLEAN NOT NULL default false;
ALTER TABLE grandedolce_orders
    ADD COLUMN delivery_start TIME WITHOUT TIME ZONE;
ALTER TABLE grandedolce_orders
    ADD COLUMN delivery_finish TIME WITHOUT TIME ZONE;

ALTER TABLE grandedolce_orders ALTER COLUMN ref_key SET NOT NULL;
ALTER TABLE grandedolce_orders ADD CONSTRAINT ref_key_unique UNIQUE (ref_key);

ALTER TABLE grandedolce_orders
    ADD CONSTRAINT FK_GD_ORDERS_ON_ADDR_AND_CLIENT FOREIGN KEY (address, client_name)
        REFERENCES grandedolce_addresses (order_address, client_name);