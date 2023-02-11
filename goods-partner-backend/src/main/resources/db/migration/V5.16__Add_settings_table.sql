CREATE TABLE "settings"
(
    "group_id" VARCHAR(255)  NOT NULL,
    "category_id"   VARCHAR(255)  NOT NULL,
    "properties" VARCHAR(1000)  NOT NULL,

    PRIMARY KEY (group_id, category_id)
);