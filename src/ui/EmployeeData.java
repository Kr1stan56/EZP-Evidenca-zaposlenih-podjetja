package ui;

import java.sql.Date;

public record EmployeeData(
        int id,
        String ime,
        String priimek,
        String email,
        String telefon,
        float placa,
        Date datumZaposlitve,
        String delovnoMesto,
        String oddelek,
        String kraj
) {}
