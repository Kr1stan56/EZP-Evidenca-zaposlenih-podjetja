/*
Created: 6. 01. 2026
Modified: 6. 01. 2026
Model: PostgreSQL 14
Database: PostgreSQL 12
*/

-- Create tables section -------------------------------------------------

-- Table zaposleni

CREATE TABLE "zaposleni"
(
  "id" Serial NOT NULL,
  "ime" Character varying(50) NOT NULL,
  "priimek" Character varying(50) NOT NULL,
  "email" Character varying(100) NOT NULL,
  "telefon" Character varying(20),
  "placa" Real NOT NULL,
  "datum_zasposlitve" Date,
  "kraj_id" Integer NOT NULL,
  "oddelek_id" Integer NOT NULL,
  "delovno_mesto_id" Integer NOT NULL,
  "izobrazba_id" Integer
)
WITH (
  autovacuum_enabled=true)
;

CREATE INDEX "IX_Relationship1" ON "zaposleni" ("kraj_id")
;

CREATE INDEX "IX_Relationship2" ON "zaposleni" ("oddelek_id")
;

CREATE INDEX "IX_Relationship3" ON "zaposleni" ("delovno_mesto_id")
;

CREATE INDEX "IX_Relationship4" ON "zaposleni" ("izobrazba_id")
;

ALTER TABLE "zaposleni" ADD CONSTRAINT "PK_zaposleni" PRIMARY KEY ("id")
;

-- Table kraji

CREATE TABLE "kraji"
(
  "id" Serial NOT NULL,
  "ime" Character varying(50) NOT NULL,
  "postna_stevilka" Character varying(10) NOT NULL
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "kraji" ADD CONSTRAINT "PK_kraji" PRIMARY KEY ("id")
;

-- Table oddelki

CREATE TABLE "oddelki"
(
  "id" Serial NOT NULL,
  "ime" Character varying(40) NOT NULL
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "oddelki" ADD CONSTRAINT "PK_oddelki" PRIMARY KEY ("id")
;

-- Table delovna_mesta

CREATE TABLE "delovna_mesta"
(
  "id" Serial NOT NULL,
  "naziv" Character varying(50) NOT NULL
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "delovna_mesta" ADD CONSTRAINT "PK_delovna_mesta" PRIMARY KEY ("id")
;

-- Table log_akcij

CREATE TABLE "log_akcij"
(
  "id" Serial NOT NULL,
  "akcija" Text,
  "uporabnik" Character varying(50),
  "timestamp" Timestamp
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "log_akcij" ADD CONSTRAINT "PK_log_akcij" PRIMARY KEY ("id")
;

-- Table settings

CREATE TABLE "settings"
(
  "id" Serial NOT NULL,
  "name" Character varying(50) NOT NULL,
  "value" Character varying(100) NOT NULL,
  "opis" Text
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "settings" ADD CONSTRAINT "PK_settings" PRIMARY KEY ("id")
;

-- Table admin_users

CREATE TABLE "admin_users"
(
  "id" Serial NOT NULL,
  "username" Character varying(40) NOT NULL,
  "password" Character varying(100) NOT NULL,
  "email" Character varying(100) NOT NULL
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "admin_users" ADD CONSTRAINT "PK_admin_users" PRIMARY KEY ("id")
;

-- Table Izobrazba

CREATE TABLE "Izobrazba"
(
  "id" Serial NOT NULL,
  "naziv" Character varying(50) NOT NULL,
  "opis" Text
)
WITH (
  autovacuum_enabled=true)
;

ALTER TABLE "Izobrazba" ADD CONSTRAINT "PK_Izobrazba" PRIMARY KEY ("id")
;

-- Create foreign keys (relationships) section -------------------------------------------------

ALTER TABLE "zaposleni"
  ADD CONSTRAINT "Relationship1"
    FOREIGN KEY ("kraj_id")
    REFERENCES "kraji" ("id")
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
;

ALTER TABLE "zaposleni"
  ADD CONSTRAINT "Relationship2"
    FOREIGN KEY ("oddelek_id")
    REFERENCES "oddelki" ("id")
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
;

ALTER TABLE "zaposleni"
  ADD CONSTRAINT "Relationship3"
    FOREIGN KEY ("delovno_mesto_id")
    REFERENCES "delovna_mesta" ("id")
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
;

ALTER TABLE "zaposleni"
  ADD CONSTRAINT "Relationship4"
    FOREIGN KEY ("izobrazba_id")
    REFERENCES "Izobrazba" ("id")
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
;

