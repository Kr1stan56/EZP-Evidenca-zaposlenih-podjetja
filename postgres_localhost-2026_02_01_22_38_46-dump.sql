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

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: add_employee(character varying, character varying, character varying, character varying, real, date, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.add_employee(p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa real, p_datum_zaposlitve date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer, p_izobrazba_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    new_id int;
BEGIN
    INSERT INTO zaposleni
        (ime, priimek, email, telefon, placa, datum_zaposlitve,
         delovno_mesto_id, oddelek_id, kraj_id, izobrazba_id)
    VALUES
        (p_ime, p_priimek, p_email, p_telefon, p_placa, p_datum_zaposlitve,
         p_delovno_mesto_id, p_oddelek_id, p_kraj_id, p_izobrazba_id)
    RETURNING id INTO new_id;

    RETURN new_id;
END;
$$;


ALTER FUNCTION public.add_employee(p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa real, p_datum_zaposlitve date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer, p_izobrazba_id integer) OWNER TO postgres;

--
-- Name: delete_employee(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_employee(p_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM zaposleni WHERE id = p_id;
END;
$$;


ALTER FUNCTION public.delete_employee(p_id integer) OWNER TO postgres;

--
-- Name: get_all_employees(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_employees() RETURNS TABLE(id integer, ime character varying, priimek character varying, email character varying, telefon character varying, placa real, datum_zaposlitve date, delovno_mesto character varying, oddelek character varying, kraj character varying, izobrazba character varying)
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
    JOIN oddelki o        ON z.oddelek_id = o.id
    LEFT JOIN kraji k     ON z.kraj_id = k.id
    LEFT JOIN "Izobrazba" i ON z.izobrazba_id = i.id
    ORDER BY z.priimek, z.ime;
END;
$$;


ALTER FUNCTION public.get_all_employees() OWNER TO postgres;

--
-- Name: get_delovna_mesta(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_delovna_mesta() RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
    SELECT id, naziv FROM delovna_mesta ORDER BY naziv;
$$;


ALTER FUNCTION public.get_delovna_mesta() OWNER TO postgres;

--
-- Name: get_employee_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_employee_by_id(p_id integer) RETURNS TABLE(ime text, priimek text, email text, telefon text, placa numeric, datum_zaposlitve date, delovno_mesto_id integer, oddelek_id integer, kraj_id integer)
    LANGUAGE sql
    AS $$
    SELECT ime, priimek, email, telefon, placa, datum_zaposlitve,
           delovno_mesto_id, oddelek_id, kraj_id
    FROM zaposleni
    WHERE id = p_id;
$$;


ALTER FUNCTION public.get_employee_by_id(p_id integer) OWNER TO postgres;

--
-- Name: get_kraji(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_kraji() RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
    SELECT id, ime FROM kraji ORDER BY ime;
$$;


ALTER FUNCTION public.get_kraji() OWNER TO postgres;

--
-- Name: get_oddelki(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_oddelki() RETURNS TABLE(id integer, naziv text)
    LANGUAGE sql
    AS $$
    SELECT id, ime FROM oddelki ORDER BY ime;
$$;


ALTER FUNCTION public.get_oddelki() OWNER TO postgres;

--
-- Name: get_settings(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_settings(p_prefix text) RETURNS TABLE(name text, value text)
    LANGUAGE sql
    AS $$
    SELECT s.name, s.value
    FROM public.settings s
    WHERE s.name LIKE (p_prefix || '%')
    ORDER BY s.name;
$$;


ALTER FUNCTION public.get_settings(p_prefix text) OWNER TO postgres;

--
-- Name: login_admin(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.login_admin(p_username character varying) RETURNS TABLE(password character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT u.password
    FROM admin_users u
    WHERE u.username = p_username;
END;
$$;


ALTER FUNCTION public.login_admin(p_username character varying) OWNER TO postgres;

--
-- Name: register_user_začasno(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."register_user_začasno"(p_username text, p_password_hash text, p_email text) RETURNS boolean
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


ALTER FUNCTION public."register_user_začasno"(p_username text, p_password_hash text, p_email text) OWNER TO postgres;

--
-- Name: update_employee(integer, text, text, text, text, numeric, date, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_employee(p_id integer, p_ime text, p_priimek text, p_email text, p_telefon text, p_placa numeric, p_datum_zaposlitve date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE zaposleni
    SET ime = p_ime,
        priimek = p_priimek,
        email = p_email,
        telefon = p_telefon,
        placa = p_placa,
        datum_zaposlitve = p_datum_zaposlitve,
        delovno_mesto_id = p_delovno_mesto_id,
        oddelek_id = p_oddelek_id,
        kraj_id = p_kraj_id
    WHERE id = p_id;
END;
$$;


ALTER FUNCTION public.update_employee(p_id integer, p_ime text, p_priimek text, p_email text, p_telefon text, p_placa numeric, p_datum_zaposlitve date, p_delovno_mesto_id integer, p_oddelek_id integer, p_kraj_id integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: Izobrazba; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Izobrazba" (
    id integer NOT NULL,
    naziv character varying(50) NOT NULL,
    opis text
)
WITH (autovacuum_enabled='true');


ALTER TABLE public."Izobrazba" OWNER TO postgres;

--
-- Name: Izobrazba_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Izobrazba_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Izobrazba_id_seq" OWNER TO postgres;

--
-- Name: Izobrazba_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Izobrazba_id_seq" OWNED BY public."Izobrazba".id;


--
-- Name: admin_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin_users (
    id integer NOT NULL,
    username character varying(40) NOT NULL,
    password character varying(100) NOT NULL,
    email character varying(100) NOT NULL
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.admin_users OWNER TO postgres;

--
-- Name: admin_users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admin_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admin_users_id_seq OWNER TO postgres;

--
-- Name: admin_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admin_users_id_seq OWNED BY public.admin_users.id;


--
-- Name: delovna_mesta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delovna_mesta (
    id integer NOT NULL,
    naziv character varying(50) NOT NULL
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.delovna_mesta OWNER TO postgres;

--
-- Name: delovna_mesta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.delovna_mesta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.delovna_mesta_id_seq OWNER TO postgres;

--
-- Name: delovna_mesta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.delovna_mesta_id_seq OWNED BY public.delovna_mesta.id;


--
-- Name: kraji; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kraji (
    id integer NOT NULL,
    ime character varying(50) NOT NULL,
    postna_stevilka character varying(10) NOT NULL
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.kraji OWNER TO postgres;

--
-- Name: kraji_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.kraji_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.kraji_id_seq OWNER TO postgres;

--
-- Name: kraji_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.kraji_id_seq OWNED BY public.kraji.id;


--
-- Name: log_akcij; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.log_akcij (
    id integer NOT NULL,
    akcija text,
    uporabnik character varying(50),
    "timestamp" timestamp without time zone
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.log_akcij OWNER TO postgres;

--
-- Name: log_akcij_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.log_akcij_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.log_akcij_id_seq OWNER TO postgres;

--
-- Name: log_akcij_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.log_akcij_id_seq OWNED BY public.log_akcij.id;


--
-- Name: oddelki; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.oddelki (
    id integer NOT NULL,
    ime character varying(40) NOT NULL
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.oddelki OWNER TO postgres;

--
-- Name: oddelki_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.oddelki_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.oddelki_id_seq OWNER TO postgres;

--
-- Name: oddelki_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.oddelki_id_seq OWNED BY public.oddelki.id;


--
-- Name: settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.settings (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    value character varying(100) NOT NULL,
    opis text
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.settings OWNER TO postgres;

--
-- Name: settings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.settings_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.settings_id_seq OWNER TO postgres;

--
-- Name: settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.settings_id_seq OWNED BY public.settings.id;


--
-- Name: zaposleni; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zaposleni (
    id integer NOT NULL,
    ime character varying(50) NOT NULL,
    priimek character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    telefon character varying(20),
    placa real NOT NULL,
    datum_zaposlitve date,
    kraj_id integer NOT NULL,
    oddelek_id integer NOT NULL,
    delovno_mesto_id integer NOT NULL,
    izobrazba_id integer
)
WITH (autovacuum_enabled='true');


ALTER TABLE public.zaposleni OWNER TO postgres;

--
-- Name: zaposleni_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.zaposleni_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.zaposleni_id_seq OWNER TO postgres;

--
-- Name: zaposleni_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.zaposleni_id_seq OWNED BY public.zaposleni.id;


--
-- Name: Izobrazba id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Izobrazba" ALTER COLUMN id SET DEFAULT nextval('public."Izobrazba_id_seq"'::regclass);


--
-- Name: admin_users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_users ALTER COLUMN id SET DEFAULT nextval('public.admin_users_id_seq'::regclass);


--
-- Name: delovna_mesta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delovna_mesta ALTER COLUMN id SET DEFAULT nextval('public.delovna_mesta_id_seq'::regclass);


--
-- Name: kraji id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kraji ALTER COLUMN id SET DEFAULT nextval('public.kraji_id_seq'::regclass);


--
-- Name: log_akcij id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_akcij ALTER COLUMN id SET DEFAULT nextval('public.log_akcij_id_seq'::regclass);


--
-- Name: oddelki id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oddelki ALTER COLUMN id SET DEFAULT nextval('public.oddelki_id_seq'::regclass);


--
-- Name: settings id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.settings ALTER COLUMN id SET DEFAULT nextval('public.settings_id_seq'::regclass);


--
-- Name: zaposleni id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni ALTER COLUMN id SET DEFAULT nextval('public.zaposleni_id_seq'::regclass);


--
-- Data for Name: Izobrazba; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Izobrazba" (id, naziv, opis) FROM stdin;
1	Srednja šola	Zaključena srednješolska izobrazba
2	Višja šola	Višješolski strokovni program
3	Visoka šola	Visokošolski strokovni program
4	Univerzitetna	Univerzitetni študijski program
5	Magisterij	Podiplomski magistrski študij
\.


--
-- Data for Name: admin_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admin_users (id, username, password, email) FROM stdin;
4	admin	$2a$10$wwOuKeujDsk550QSdqz.ge5CvZ25RKnZVaERILmihxXK3ydXVpXQm	admin
5	Kristan	$2a$10$XnLyr.RPaB/IrjVbpH/pM.hIwSXt8HgRuoIM22vm56Wu2oTq04l/C	blaz.kristan@scv.si
\.


--
-- Data for Name: delovna_mesta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.delovna_mesta (id, naziv) FROM stdin;
1	Programer
2	Sistemski administrator
3	Analitik
4	Vodja projekta
5	Tehnik
\.


--
-- Data for Name: kraji; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.kraji (id, ime, postna_stevilka) FROM stdin;
1	Ljubljana	1000
2	Maribor	2000
3	Celje	3000
4	Kranj	4000
5	Velenje	3320
\.


--
-- Data for Name: log_akcij; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.log_akcij (id, akcija, uporabnik, "timestamp") FROM stdin;
\.


--
-- Data for Name: oddelki; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.oddelki (id, ime) FROM stdin;
1	IT
2	Računovodstvo
3	Kadrovska služba
4	Prodaja
5	Marketing
\.


--
-- Data for Name: settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.settings (id, name, value, opis) FROM stdin;
2	ui.font.size.base	12	Osnovna velikost pisave
3	ui.font.size.h2	14	Velikost pisave za manjše naslove
5	ui.color.bg.app	#F0F4FA	Ozadje aplikacije
6	ui.color.bg.bar	#EBF0F8	Ozadje header/top bar
7	ui.color.bg.card	#FFFFFF	Ozadje kartic
8	ui.color.border	#D2DCEB	Standardni border
9	ui.color.text	#1E3250	Primarno besedilo
10	ui.color.text.muted	#143C82	Naslovi/poudarki
11	ui.color.primary	#1E5FBE	Primarna akcijska barva
13	ui.color.success	#2EA043	Success barva
14	ui.color.danger	#C83232	Danger barva
19	ui.pad.inner	10	Notranji padding
21	ui.btn.h	30	Višina gumba
22	ui.btn.icon.w	44	Širina icon gumba
23	ui.btn.icon.h	28	Višina icon gumba
24	ui.table.row.h	26	Višina vrstice tabele
18	ui.pad.outer	12	Zunanji padding
20	ui.btn.w	110	Širina gumba
4	ui.font.size.h1	20	Velikost pisave za glavne naslove
15	ui.color.table.header.bg	#B4BDD6	Header ozadje tabele
1	ui.font.family	Segoe UI	Primarna pisava UI 
17	ui.color.table.grid	#68B0BD	Grid barva tabele
16	ui.color.table.header.fg	#1E5FBE	Header tekst tabele
12	ui.color.primary.text	#ffffff	Tekst na primarni barvi
37	ui.color.table.bg	#C3D8DE	barva ozadja Grid barva tabele
\.


--
-- Data for Name: zaposleni; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.zaposleni (id, ime, priimek, email, telefon, placa, datum_zaposlitve, kraj_id, oddelek_id, delovno_mesto_id, izobrazba_id) FROM stdin;
4	Marko	Novak	marko.novak@podjetje.si	031111111	1800	2022-01-10	1	1	1	\N
6	Marko	Novak	marko.novak@podjetje.si	031111111	1800	2022-01-10	1	1	1	\N
8	Ana	Kovač	ana.kovac@podjetje.si	031222222	1950	2021-05-03	4	2	2	\N
11	Miha	Vidmar	miha.vidmar@podjetje.si	031555555	1750	2023-06-20	5	2	1	\N
15	martin	tuk	tuk@scv.si	070 222 222	1e+06	2000-12-12	5	1	1	1
12	blaz	kristan	blaz.kristan@scv.si	070551055	4500	2000-12-12	3	5	4	1
9	Luka	Horvat	luka.horvat@podjetje.si	031333333	2101	2020-09-15	1	5	2	\N
\.


--
-- Name: Izobrazba_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Izobrazba_id_seq"', 5, true);


--
-- Name: admin_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admin_users_id_seq', 5, true);


--
-- Name: delovna_mesta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.delovna_mesta_id_seq', 5, true);


--
-- Name: kraji_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.kraji_id_seq', 5, true);


--
-- Name: log_akcij_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.log_akcij_id_seq', 1, false);


--
-- Name: oddelki_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.oddelki_id_seq', 5, true);


--
-- Name: settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.settings_id_seq', 40, true);


--
-- Name: zaposleni_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.zaposleni_id_seq', 15, true);


--
-- Name: Izobrazba PK_Izobrazba; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Izobrazba"
    ADD CONSTRAINT "PK_Izobrazba" PRIMARY KEY (id);


--
-- Name: admin_users PK_admin_users; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_users
    ADD CONSTRAINT "PK_admin_users" PRIMARY KEY (id);


--
-- Name: delovna_mesta PK_delovna_mesta; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delovna_mesta
    ADD CONSTRAINT "PK_delovna_mesta" PRIMARY KEY (id);


--
-- Name: kraji PK_kraji; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kraji
    ADD CONSTRAINT "PK_kraji" PRIMARY KEY (id);


--
-- Name: log_akcij PK_log_akcij; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_akcij
    ADD CONSTRAINT "PK_log_akcij" PRIMARY KEY (id);


--
-- Name: oddelki PK_oddelki; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oddelki
    ADD CONSTRAINT "PK_oddelki" PRIMARY KEY (id);


--
-- Name: settings PK_settings; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT "PK_settings" PRIMARY KEY (id);


--
-- Name: zaposleni PK_zaposleni; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "PK_zaposleni" PRIMARY KEY (id);


--
-- Name: IX_Relationship1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "IX_Relationship1" ON public.zaposleni USING btree (kraj_id);


--
-- Name: IX_Relationship2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "IX_Relationship2" ON public.zaposleni USING btree (oddelek_id);


--
-- Name: IX_Relationship3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "IX_Relationship3" ON public.zaposleni USING btree (delovno_mesto_id);


--
-- Name: IX_Relationship4; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "IX_Relationship4" ON public.zaposleni USING btree (izobrazba_id);


--
-- Name: zaposleni Relationship1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship1" FOREIGN KEY (kraj_id) REFERENCES public.kraji(id);


--
-- Name: zaposleni Relationship2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship2" FOREIGN KEY (oddelek_id) REFERENCES public.oddelki(id);


--
-- Name: zaposleni Relationship3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship3" FOREIGN KEY (delovno_mesto_id) REFERENCES public.delovna_mesta(id);


--
-- Name: zaposleni Relationship4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zaposleni
    ADD CONSTRAINT "Relationship4" FOREIGN KEY (izobrazba_id) REFERENCES public."Izobrazba"(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

