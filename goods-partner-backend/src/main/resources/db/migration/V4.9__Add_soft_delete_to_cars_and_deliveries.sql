ALTER TABLE cars ADD COLUMN deleted BOOLEAN NOT NULL default false;
ALTER TABLE deliveries ADD COLUMN deleted BOOLEAN NOT NULL default false;