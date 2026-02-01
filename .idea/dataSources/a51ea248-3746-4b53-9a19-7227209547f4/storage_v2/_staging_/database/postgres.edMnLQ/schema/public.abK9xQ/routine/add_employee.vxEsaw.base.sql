create function add_employee(p_ime character varying, p_priimek character varying, p_email character varying, p_telefon character varying, p_placa real, p_datum date, p_kraj_id integer, p_oddelek_id integer) returns void
    language plpgsql
as
$$
BEGIN
    INSERT INTO zaposleni (ime, priimek, email, telefon, placa, datum_zasposlitve, kraj_id, oddelek_id, delovno_mesto_id)
    VALUES (p_ime, p_priimek, p_email, p_telefon, p_placa, p_datum, p_kraj_id, p_oddelek_id, 1);
END;
$$;

alter function add_employee(varchar, varchar, varchar, varchar, real, date, integer, integer) owner to postgres;

