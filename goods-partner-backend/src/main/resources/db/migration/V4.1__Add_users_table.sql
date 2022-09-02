CREATE TABLE "users"(
   "id" INTEGER PRIMARY KEY,
   "user_name" VARCHAR(255) NOT NULL,
   "email" VARCHAR(255) NOT NULL UNIQUE,
   "role" VARCHAR(255) NOT NULL,
   "enabled" BOOLEAN NOT NULL
);

CREATE SEQUENCE users_id_sequence OWNED BY users.id;
ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_id_sequence');
ALTER SEQUENCE users_id_sequence INCREMENT BY 50;