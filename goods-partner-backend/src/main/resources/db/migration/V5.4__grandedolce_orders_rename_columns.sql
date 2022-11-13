ALTER TABLE grandedolce_orders
    RENAME COLUMN delivery_date TO reschedule_date;

ALTER TABLE grandedolce_orders
    RENAME COLUMN created_date TO shipping_date;