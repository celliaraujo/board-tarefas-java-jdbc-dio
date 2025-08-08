--liquibase formatted sql
--changeset marcelli:202508061445
--comment: cards table create

CREATE TABLE CARDS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    boards_columns_id BIGINT NOT NULL,
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (boards_columns_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE
)ENGINE=InnoDB;


--rollback DROP TABLE CARDS