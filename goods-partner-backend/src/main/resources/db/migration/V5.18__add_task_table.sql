CREATE TABLE tasks (

   "id" BIGINT PRIMARY KEY,
   "description" VARCHAR NOT NULL,
   "execution_date" DATE,
   "map_point" JSONB,
   "car_id" INTEGER NOT NULL

);

CREATE SEQUENCE tasks_id_sequence OWNED BY tasks.id;
ALTER TABLE tasks ALTER COLUMN id SET DEFAULT nextval('tasks_id_sequence');
ALTER SEQUENCE tasks_id_sequence INCREMENT BY 50;

ALTER TABLE tasks ADD CONSTRAINT FK_TASK_ON_CAR_ID FOREIGN KEY (car_id) REFERENCES cars (id);