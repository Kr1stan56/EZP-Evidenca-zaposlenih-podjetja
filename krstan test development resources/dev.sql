--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Najprej izbrišemo obstoječe tabele, če obstajajo (opcijsko)
DROP TABLE IF EXISTS public.delovno_mesto_oddelek CASCADE;
DROP TABLE IF EXISTS public.zaposleni CASCADE;
DROP TABLE IF EXISTS public.settings CASCADE;
DROP TABLE IF EXISTS public.oddelki CASCADE;
DROP TABLE IF EXISTS public.log_akcij CASCADE;
DROP TABLE IF EXISTS public.kraji CASCADE;
DROP TABLE IF EXISTS public.delovna_mesta CASCADE;
DROP TABLE IF EXISTS public."Izobrazba" CASCADE;
DROP TABLE IF EXISTS public.admin_users CASCADE;

DROP SEQUENCE IF EXISTS public.zaposleni_id_seq;
DROP SEQUENCE IF EXISTS public.settings_id_seq;
DROP SEQUENCE IF EXISTS public.oddelki_id_seq;
DROP SEQUENCE IF EXISTS public.log_akcij_id_seq;
DROP SEQUENCE IF EXISTS public.kraji_id_seq;
DROP SEQUENCE IF EXISTS public.delovna_mesta_id_seq;
DROP SEQUENCE IF EXISTS public."Izobrazba_id_seq";
DROP SEQUENCE IF EXISTS public.admin_users_id_seq;

-- Funkcije (dodamo OR REPLACE)
CREATE OR REPLACE FUNCTION public.add_employee(p_ime text, p_priimek text, p_email text, p_telefon text, p_placa numeric, p_datum date, p_delovno_mesto_id integer, p_kraj_id integer, p_izobrazba_id integer) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO zaposleni(ime, priimek, email, telefon, placa, datum_zaposlitve, delovno_mesto_id, kraj_id, izobrazba_id)
VALUES (p_ime, p_priimek, p_email, p_telefon, p_placa, p_datum, p_delovno_mesto_id, p_kraj_id, p_izobrazba_id)
RETURNING id;
$$;


CREATE OR REPLACE FUNCTION public.add_employee(p_ime text, p_priimek text, p_email text, p_telefon text, p_placa numeric, p_datum date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer, p_izobrazba_id integer) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO zaposleni(ime, priimek, email, telefon, placa, datum_zaposlitve, delovno_mesto_id, oddelek_id, kraj_id, izobrazba_id)
VALUES (p_ime, p_priimek, p_email, p_telefon, p_placa, p_datum, p_delovno_mesto_id, p_oddelek_id, p_kraj_id, p_izobrazba_id)
RETURNING id;
$$;


CREATE OR REPLACE FUNCTION public.delete_employee(p_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM zaposleni WHERE id = p_id;
END;
$$;


CREATE OR REPLACE FUNCTION public.enforce_employee_department_match() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.oddelek_id IS NULL THEN
    RETURN NEW;
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM delovno_mesto_oddelek dmo
    WHERE dmo.delovno_mesto_id = NEW.delovno_mesto_id
      AND dmo.oddelek_id = NEW.oddelek_id
  ) THEN
    RAISE EXCEPTION 'Oddelek % ni dovoljen za delovno mesto %', NEW.oddelek_id, NEW.delovno_mesto_id;
  END IF;

  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.get_all_employees() RETURNS TABLE(id integer, ime character varying, priimek character varying, email character varying, telefon character varying, placa real, datum_zaposlitve date, delovno_mesto character varying, oddelek character varying, kraj character varying, izobrazba character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
  RETURN QUERY
  SELECT
    z.id,
    z.ime,
    z.priimek,
    z.email,
    z.telefon,
    z.placa,
    z.datum_zaposlitve,
    dm.naziv AS delovno_mesto,
    o.ime    AS oddelek,
    k.ime    AS kraj,
    i.naziv  AS izobrazba
  FROM zaposleni z
  JOIN delovna_mesta dm ON z.delovno_mesto_id = dm.id
  LEFT JOIN oddelki o   ON z.oddelek_id = o.id
  LEFT JOIN kraji k     ON z.kraj_id = k.id
  LEFT JOIN "Izobrazba" i ON z.izobrazba_id = i.id
  ORDER BY z.priimek, z.ime;
END;
$$;


CREATE OR REPLACE FUNCTION public.get_delovna_mesta() RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
    SELECT id, naziv FROM delovna_mesta ORDER BY naziv;
$$;


CREATE OR REPLACE FUNCTION public.get_employee_by_id(p_id integer) RETURNS TABLE(ime text, priimek text, email text, telefon text, placa numeric, datum_zaposlitve date, delovno_mesto_id integer, oddelek_id integer, kraj_id integer)
    LANGUAGE sql
    AS $$
SELECT
  z.ime,
  z.priimek,
  z.email,
  z.telefon,
  z.placa,
  z.datum_zaposlitve,
  z.delovno_mesto_id,
  z.oddelek_id,
  z.kraj_id
FROM zaposleni z
WHERE z.id = p_id;
$$;


CREATE OR REPLACE FUNCTION public.get_izobrazba() RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
SELECT i.id, i.naziv::text AS naziv
FROM "Izobrazba" i
ORDER BY i.id;
$$;


CREATE OR REPLACE FUNCTION public.get_kraji() RETURNS TABLE(id integer, ime text)
    LANGUAGE sql
    AS $$
SELECT k.id, k.ime::text AS ime
FROM kraji k
ORDER BY k.ime;
$$;


CREATE OR REPLACE FUNCTION public.get_oddelki(p_delovno_mesto_id integer) RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
SELECT o.id, o.ime::text AS naziv
FROM delovno_mesto_oddelek dmo
JOIN oddelki o ON o.id = dmo.oddelek_id
WHERE dmo.delovno_mesto_id = p_delovno_mesto_id
ORDER BY o.ime;
$$;


CREATE OR REPLACE FUNCTION public.get_settings(p_prefix text) RETURNS TABLE(name text, value text)
    LANGUAGE sql
    AS $$
    SELECT s.name, s.value
    FROM public.settings s
    WHERE s.name LIKE (p_prefix || '%')
    ORDER BY s.name;
$$;


CREATE OR REPLACE FUNCTION public.log_zaposleni_changes() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  INSERT INTO log_akcij(akcija, uporabnik, "timestamp")
  VALUES (
    TG_OP || ' zaposleni id=' || COALESCE(NEW.id, OLD.id),
    current_user,
    now()
  );

  IF TG_OP = 'DELETE' THEN
    RETURN OLD;
  END IF;

  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.login_admin(p_username character varying) RETURNS TABLE(password character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT u.password
    FROM admin_users u
    WHERE u.username = p_username;
END;
$$;


CREATE OR REPLACE FUNCTION public."register_user_začasno"(p_username text, p_password_hash text, p_email text) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM admin_users WHERE username = p_username) THEN
        RETURN FALSE;
    END IF;

    INSERT INTO admin_users(username, password, email)
    VALUES (p_username, p_password_hash, p_email);

    RETURN TRUE;
END;
$$;


CREATE OR REPLACE FUNCTION public.trg_check_datum_zaposlitve() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.datum_zaposlitve > CURRENT_DATE THEN
    RAISE EXCEPTION USING
      ERRCODE = 'P0001',
      MESSAGE = 'Datum zaposlitve ne sme biti v prihodnosti 6.';
  END IF;
  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.trg_check_placa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.placa < 0 THEN
    RAISE EXCEPTION 'Plača ne sme biti negativna!';
  END IF;

  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.trg_fix_future_datum_zaposlitve() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.datum_zaposlitve > CURRENT_DATE THEN
  NEW.datum_zaposlitve := CURRENT_DATE;

  INSERT INTO log_akcij(akcija, uporabnik, "timestamp")
  VALUES ('POPRAAVLJEN DATUM ZAPOSLITVE (AUTO)', current_user, now());
END IF;


  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.trg_fix_negative_placa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.placa < 0 THEN
    -- nastavi na 1000
    NEW.placa := 1000;

    -- zapiši v log
    INSERT INTO log_akcij(akcija, uporabnik, "timestamp")
    VALUES (
      'NEGATIVNA PLAČA – nastavljena na 1000 (zaposleni id=' || COALESCE(NEW.id, OLD.id) || ')',
      current_user,
      now()
    );
  END IF;

  RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION public.update_employee(p_id integer, p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa numeric, p_datum date, p_delovno_mesto_id integer, p_kraj_id integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE zaposleni
SET ime = p_ime,
    priimek = p_priimek,
    email = p_email,
    telefon = p_telefon,
    placa = p_placa,
    datum_zaposlitve = p_datum,
    delovno_mesto_id = p_delovno_mesto_id,
    kraj_id = p_kraj_id
WHERE id = p_id;
$$;


CREATE OR REPLACE FUNCTION public.update_employee(p_id integer, p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa numeric, p_datum date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE zaposleni
SET ime = p_ime,
    priimek = p_priimek,
    email = p_email,
    telefon = p_telefon,
    placa = p_placa,
    datum_zaposlitve = p_datum,
    delovno_mesto_id = p_delovno_mesto_id,
    oddelek_id = p_oddelek_id,
    kraj_id = p_kraj_id
WHERE id = p_id;
$$;


CREATE OR REPLACE FUNCTION public.update_employee(p_id integer, p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa numeric, p_datum date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer, p_izobrazba_id integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE zaposleni
SET ime = p_ime,
    priimek = p_priimek,
    email = p_email,
    telefon = p_telefon,
    placa = p_placa,
    datum_zaposlitve = p_datum,
    delovno_mesto_id = p_delovno_mesto_id,
    oddelek_id = p_oddelek_id,
    kraj_id = p_kraj_id,
    izobrazba_id = p_izobrazba_id
WHERE id = p_id;
$$;

SET default_tablespace = '';
SET default_table_access_method = heap;

-- Tabele
CREATE TABLE public."Izobrazba" (
    id integer NOT NULL,
    naziv character varying(50) NOT NULL,
    opis text
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public."Izobrazba_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."Izobrazba_id_seq" OWNED BY public."Izobrazba".id;

CREATE TABLE public.admin_users (
    id integer NOT NULL,
    username character varying(40) NOT NULL,
    password character varying(100) NOT NULL,
    email character varying(100) NOT NULL
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.admin_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_users_id_seq OWNED BY public.admin_users.id;

CREATE TABLE public.delovna_mesta (
    id integer NOT NULL,
    naziv character varying(50) NOT NULL
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.delovna_mesta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.delovna_mesta_id_seq OWNED BY public.delovna_mesta.id;

CREATE TABLE public.delovno_mesto_oddelek (
    delovno_mesto_id integer NOT NULL,
    oddelek_id integer NOT NULL
);

CREATE TABLE public.kraji (
    id integer NOT NULL,
    ime character varying(50) NOT NULL,
    postna_stevilka character varying(10) NOT NULL
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.kraji_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.kraji_id_seq OWNED BY public.kraji.id;

CREATE TABLE public.log_akcij (
    id integer NOT NULL,
    akcija text,
    uporabnik character varying(50),
    "timestamp" timestamp without time zone
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.log_akcij_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.log_akcij_id_seq OWNED BY public.log_akcij.id;

CREATE TABLE public.oddelki (
    id integer NOT NULL,
    ime character varying(40) NOT NULL
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.oddelki_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.oddelki_id_seq OWNED BY public.oddelki.id;

CREATE TABLE public.settings (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    value character varying(100) NOT NULL,
    opis text
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.settings_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.settings_id_seq OWNED BY public.settings.id;

CREATE TABLE public.zaposleni (
    id integer NOT NULL,
    ime character varying(50) NOT NULL,
    priimek character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    telefon character varying(20),
    placa real NOT NULL,
    datum_zaposlitve date,
    kraj_id integer NOT NULL,
    delovno_mesto_id integer NOT NULL,
    izobrazba_id integer,
    oddelek_id integer
)
WITH (autovacuum_enabled='true');

CREATE SEQUENCE public.zaposleni_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.zaposleni_id_seq OWNED BY public.zaposleni.id;

-- DEFAULT vrednosti
ALTER TABLE ONLY public."Izobrazba" ALTER COLUMN id SET DEFAULT nextval('public."Izobrazba_id_seq"'::regclass);
ALTER TABLE ONLY public.admin_users ALTER COLUMN id SET DEFAULT nextval('public.admin_users_id_seq'::regclass);
ALTER TABLE ONLY public.delovna_mesta ALTER COLUMN id SET DEFAULT nextval('public.delovna_mesta_id_seq'::regclass);
ALTER TABLE ONLY public.kraji ALTER COLUMN id SET DEFAULT nextval('public.kraji_id_seq'::regclass);
ALTER TABLE ONLY public.log_akcij ALTER COLUMN id SET DEFAULT nextval('public.log_akcij_id_seq'::regclass);
ALTER TABLE ONLY public.oddelki ALTER COLUMN id SET DEFAULT nextval('public.oddelki_id_seq'::regclass);
ALTER TABLE ONLY public.settings ALTER COLUMN id SET DEFAULT nextval('public.settings_id_seq'::regclass);
ALTER TABLE ONLY public.zaposleni ALTER COLUMN id SET DEFAULT nextval('public.zaposleni_id_seq'::regclass);

-- Podatki (brez ON CONFLICT - najprej vstavimo, potem nastavimo primarne ključe)
-- Izobrazba
INSERT INTO public."Izobrazba" (id, naziv, opis) VALUES
(1, 'Srednja šola', 'Zaključena srednješolska izobrazba'),
(2, 'Višja šola', 'Višješolski strokovni program'),
(3, 'Visoka šola', 'Visokošolski strokovni program'),
(4, 'Univerzitetna', 'Univerzitetni študijski program'),
(5, 'Magisterij', 'Podiplomski magistrski študij');

-- admin_users
INSERT INTO public.admin_users (id, username, password, email) VALUES
(4, 'admin', '$2a$10$wwOuKeujDsk550QSdqz.ge5CvZ25RKnZVaERILmihxXK3ydXVpXQm', 'admin'),
(5, 'Kristan', '$2a$10$XnLyr.RPaB/IrjVbpH/pM.hIwSXt8HgRuoIM22vm56Wu2oTq04l/C', 'blaz.kristan@scv.si');

-- delovna_mesta
INSERT INTO public.delovna_mesta (id, naziv) VALUES
(1, 'Programer'),
(2, 'Sistemski administrator'),
(5, 'Tehnik'),
(4, 'Vodja projekta'),
(3, 'Analitik');

-- kraji
INSERT INTO public.kraji (id, ime, postna_stevilka) VALUES
(1, 'Ljubljana', '1000'),
(2, 'Maribor', '2000'),
(3, 'Celje', '3000'),
(4, 'Kranj', '4000'),
(5, 'Velenje', '3320');

-- log_akcij
INSERT INTO public.log_akcij (id, akcija, uporabnik, "timestamp") VALUES
(1, 'UPDATE zaposleni id=11', 'postgres', '2026-02-02 19:15:18.696493'),
(2, 'NEGATIVNA PLAČA – nastavljena na 1000 (zaposleni id=6)', 'postgres', '2026-02-02 19:22:22.103338'),
(3, 'UPDATE zaposleni id=6', 'postgres', '2026-02-02 19:22:22.103338'),
(4, 'POPRAAVLJEN DATUM ZAPOSLITVE (AUTO)', 'postgres', '2026-02-02 19:22:34.258359'),
(5, 'UPDATE zaposleni id=11', 'postgres', '2026-02-02 19:22:34.258359');

-- oddelki
INSERT INTO public.oddelki (id, ime) VALUES
(1, 'IT'),
(2, 'Računovodstvo'),
(3, 'Kadrovska služba'),
(4, 'Prodaja'),
(5, 'Marketing'),
(6, 'Razvoj'),
(7, 'Informacijska varnost'),
(8, 'Podpora uporabnikom'),
(9, 'Infrastruktura'),
(10, 'Raziskave in razvoj'),
(11, 'Upravljanje projektov'),
(12, 'Operativa');

-- settings
INSERT INTO public.settings (id, name, value, opis) VALUES
(1, 'ui.font.family', 'Segoe UI', 'Primarna pisava UI'),
(2, 'ui.font.size.base', '12', 'Osnovna velikost pisave'),
(3, 'ui.font.size.h2', '14', 'Velikost pisave za manjše naslove'),
(4, 'ui.font.size.h1', '20', 'Velikost pisave za glavne naslove'),
(5, 'ui.color.bg.app', '#F0F4FA', 'Ozadje aplikacije'),
(6, 'ui.color.bg.bar', '#EBF0F8', 'Ozadje header/top bar'),
(7, 'ui.color.bg.card', '#FFFFFF', 'Ozadje kartic'),
(8, 'ui.color.border', '#D2DCEB', 'Standardni border'),
(9, 'ui.color.text', '#1E3250', 'Primarno besedilo'),
(10, 'ui.color.text.muted', '#143C82', 'Naslovi/poudarki'),
(11, 'ui.color.primary', '#1E5FBE', 'Primarna akcijska barva'),
(12, 'ui.color.primary.text', '#ffffff', 'Tekst na primarni barvi'),
(13, 'ui.color.success', '#2EA043', 'Success barva'),
(14, 'ui.color.danger', '#C83232', 'Danger barva'),
(15, 'ui.color.table.header.bg', '#B4BDD6', 'Header ozadje tabele'),
(16, 'ui.color.table.header.fg', '#1E5FBE', 'Header tekst tabele'),
(17, 'ui.color.table.grid', '#68B0BD', 'Grid barva tabele'),
(18, 'ui.pad.outer', '12', 'Zunanji padding'),
(19, 'ui.pad.inner', '10', 'Notranji padding'),
(20, 'ui.btn.w', '110', 'Širina gumba'),
(21, 'ui.btn.h', '30', 'Višina gumba'),
(22, 'ui.btn.icon.w', '44', 'Širina icon gumba'),
(23, 'ui.btn.icon.h', '28', 'Višina icon gumba'),
(24, 'ui.table.row.h', '26', 'Višina vrstice tabele'),
(37, 'ui.color.table.bg', '#C3D8DE', 'barva ozadja Grid barva tabele');

-- zaposleni
INSERT INTO public.zaposleni (id, ime, priimek, email, telefon, placa, datum_zaposlitve, kraj_id, delovno_mesto_id, izobrazba_id, oddelek_id) VALUES
(9, 'Luka', 'Horvat', 'luka.horvat@podjetje.si', '0313333333', 2101, '2020-09-15', 4, 1, 1, 1),
(8, 'Ana', 'Kovač', 'ana.kovac@podjetje.si', '031222222', 1950, '2021-05-03', 4, 1, 1, 10),
(12, 'blaz', 'kristan', 'blaz.kristan@scv.si', '070551055', 4500, '2000-12-12', 3, 5, 1, 12),
(4, 'Marko', 'Novak', 'marko.novak@podjetje.si', '031111111', 1800, '2022-01-10', 1, 5, 1, 12),
(15, 'martin', 'tuk', 'tuk@scv.si', '070 222 222', 1000000, '2000-12-12', 5, 5, 1, 8),
(6, 'Marko', 'Novak', 'marko.novak@podjetje.si', '031111111', 1000, '2022-01-10', 4, 2, 1, 9),
(11, 'Miha', 'Vidmar', 'miha.vidmar@podjetje.si', '031555555', 1750, '2026-02-02', 1, 2, 1, 9);

-- delovno_mesto_oddelek (POMEMBNO - ta tabela mora biti po drugih tabelah)
INSERT INTO public.delovno_mesto_oddelek (delovno_mesto_id, oddelek_id) VALUES
(1, 1),
(1, 6),
(1, 10),
(2, 9),
(2, 1),
(5, 8),
(5, 12);

-- Nastavitev sekvenc
SELECT pg_catalog.setval('public."Izobrazba_id_seq"', 5, true);
SELECT pg_catalog.setval('public.admin_users_id_seq', 5, true);
SELECT pg_catalog.setval('public.delovna_mesta_id_seq', 5, true);
SELECT pg_catalog.setval('public.kraji_id_seq', 5, true);
SELECT pg_catalog.setval('public.log_akcij_id_seq', 5, true);
SELECT pg_catalog.setval('public.oddelki_id_seq', 12, true);
SELECT pg_catalog.setval('public.settings_id_seq', 40, true);
SELECT pg_catalog.setval('public.zaposleni_id_seq', 15, true);

-- Primarni ključi in indeksi
ALTER TABLE ONLY public."Izobrazba"
    ADD CONSTRAINT "PK_Izobrazba" PRIMARY KEY (id);

ALTER TABLE ONLY public.admin_users
    ADD CONSTRAINT "PK_admin_users" PRIMARY KEY (id);

ALTER TABLE ONLY public.delovna_mesta
    ADD CONSTRAINT "PK_delovna_mesta" PRIMARY KEY (id);

ALTER TABLE ONLY public.kraji
    ADD CONSTRAINT "PK_kraji" PRIMARY KEY (id);

ALTER TABLE ONLY public.log_akcij
    ADD CONSTRAINT "PK_log_akcij" PRIMARY KEY (id);

ALTER TABLE ONLY public.oddelki
    ADD CONSTRAINT "PK_oddelki" PRIMARY KEY (id);

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT "PK_settings" PRIMARY KEY (id);

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "PK_zaposleni" PRIMARY KEY (id);

ALTER TABLE ONLY public.delovno_mesto_oddelek
    ADD CONSTRAINT delovno_mesto_oddelek_pkey PRIMARY KEY (delovno_mesto_id, oddelek_id);

CREATE INDEX "IX_Relationship1" ON public.zaposleni USING btree (kraj_id);
CREATE INDEX "IX_Relationship3" ON public.zaposleni USING btree (delovno_mesto_id);
CREATE INDEX "IX_Relationship4" ON public.zaposleni USING btree (izobrazba_id);

-- Tuji ključi
ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship1" FOREIGN KEY (kraj_id) REFERENCES public.kraji(id);

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship3" FOREIGN KEY (delovno_mesto_id) REFERENCES public.delovna_mesta(id);

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship4" FOREIGN KEY (izobrazba_id) REFERENCES public."Izobrazba"(id);

ALTER TABLE ONLY public.delovno_mesto_oddelek
    ADD CONSTRAINT delovno_mesto_oddelek_delovno_mesto_id_fkey FOREIGN KEY (delovno_mesto_id) REFERENCES public.delovna_mesta(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.delovno_mesto_oddelek
    ADD CONSTRAINT delovno_mesto_oddelek_oddelek_id_fkey FOREIGN KEY (oddelek_id) REFERENCES public.oddelki(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT fk_zaposleni_oddelki FOREIGN KEY (oddelek_id) REFERENCES public.oddelki(id);

-- Triggers
CREATE TRIGGER fix_future_datum_zaposlitve BEFORE INSERT OR UPDATE ON public.zaposleni FOR EACH ROW EXECUTE FUNCTION public.trg_fix_future_datum_zaposlitve();
CREATE TRIGGER fix_negative_placa BEFORE INSERT OR UPDATE ON public.zaposleni FOR EACH ROW EXECUTE FUNCTION public.trg_fix_negative_placa();
CREATE TRIGGER trg_employee_department_match BEFORE INSERT OR UPDATE OF delovno_mesto_id, oddelek_id ON public.zaposleni FOR EACH ROW EXECUTE FUNCTION public.enforce_employee_department_match();
CREATE TRIGGER trg_log_zaposleni AFTER INSERT OR DELETE OR UPDATE ON public.zaposleni FOR EACH ROW EXECUTE FUNCTION public.log_zaposleni_changes();

-- ACL (dodano na koncu)
REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

--
-- PostgreSQL database dump complete
--