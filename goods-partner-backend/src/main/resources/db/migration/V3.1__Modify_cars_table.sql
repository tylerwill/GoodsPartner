ALTER TABLE cars ADD "available" BOOLEAN NOT NULL;

CREATE SEQUENCE cars_id_sequence OWNED BY cars.id;
ALTER TABLE cars ALTER COLUMN id SET DEFAULT nextval('cars_id_sequence');
SELECT setval('cars_id_sequence', COALESCE(max(id), 1)) FROM cars;
ALTER SEQUENCE cars_id_sequence INCREMENT BY 50;