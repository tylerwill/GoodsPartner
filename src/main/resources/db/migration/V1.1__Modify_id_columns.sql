
CREATE SEQUENCE client_sequence OWNED BY clients.id;
CREATE SEQUENCE addresses_sequence OWNED BY addresses.id;
CREATE SEQUENCE orders_sequence OWNED BY orders.id;
CREATE SEQUENCE products_sequence OWNED BY products.id;
CREATE SEQUENCE orders_products_sequence OWNED BY orders_products.id;
CREATE SEQUENCE stores_sequence OWNED BY stores.id;
CREATE SEQUENCE managers_sequence OWNED BY managers.id;

ALTER TABLE clients ALTER COLUMN id SET DEFAULT nextval('client_sequence');
SELECT setval('client_sequence', COALESCE(max(id), 1)) FROM clients;

ALTER TABLE addresses ALTER COLUMN id SET DEFAULT nextval('addresses_sequence');
SELECT setval('addresses_sequence', COALESCE(max(id), 1)) FROM addresses;

ALTER TABLE orders ALTER COLUMN id SET DEFAULT nextval('orders_sequence');
SELECT setval('orders_sequence', COALESCE(max(id), 1)) FROM orders;

ALTER TABLE products ALTER COLUMN id SET DEFAULT nextval('products_sequence');
SELECT setval('products_sequence', COALESCE(max(id), 1)) FROM products;

ALTER TABLE orders_products ALTER COLUMN id SET DEFAULT nextval('orders_products_sequence');
SELECT setval('orders_products_sequence', COALESCE(max(id), 1)) FROM orders_products;

ALTER TABLE stores ALTER COLUMN id SET DEFAULT nextval('stores_sequence');
SELECT setval('stores_sequence', COALESCE(max(id), 1)) FROM stores;

ALTER TABLE managers ALTER COLUMN id SET DEFAULT nextval('managers_sequence');
SELECT setval('managers_sequence', COALESCE(max(id), 1)) FROM managers;
