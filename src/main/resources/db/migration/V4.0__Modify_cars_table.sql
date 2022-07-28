CREATE TYPE car_status AS ENUM ('ENABLE', 'DISABLE');
ALTER TABLE cars ADD "status" car_status;
