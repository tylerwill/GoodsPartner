CREATE TABLE "deliveries_history"(
     "id" UUID PRIMARY KEY,
     "delivery_id" UUID NOT NULL,
     "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
     "role" VARCHAR(255) NOT NULL,
     "user_email" VARCHAR(255) NOT NULL,
     "action" VARCHAR(255) NOT NULL
);

ALTER TABLE deliveries_history
    ADD CONSTRAINT FK_DELIVERIES_HISTORY_ON_DELIVERY_ID FOREIGN KEY (delivery_id) REFERENCES deliveries (id);