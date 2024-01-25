CREATE SEQUENCE IF NOT EXISTS item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE item
(
    id                BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    stock_available BIGINT       NOT NULL,
    stock_reserved  BIGINT       NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id)
);