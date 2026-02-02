# EZP Evidenca zaposlenih podjetja
# Employee App (Java Swing + PostgreSQL)

## Zahteve
- JDK 17+ (lahko tudi 11, priporočeno 17)
- 2x `.jar` knjižnici (brez Maven/Gradle):
  - PostgreSQL JDBC driver (`postgresql-42.x.x.jar`)
  - jBCrypt (`jbcrypt-0.4.jar`)

## Knjižnice (kjer so pri tebi)
V projektu imaš mapo:
`potrebna librarija za importat/`

V njej morata biti ti dve datoteki:
- `postgresql-42.x.x.jar`
- `jbcrypt-0.4.jar`

---

## Zagon v IntelliJ IDEA (priporočeno)
1. Odpri projekt v IntelliJ.
2. Pojdi na: **File → Project Structure → Libraries**
3. Klikni **+ → Java** in izberi oba `.jar` iz mape:
   `potrebna librarija za importat/`
4. Klikni **Apply → OK**
5. Zaženi `Main.java` (Run).

Če je pravilno nastavljeno, ne sme biti rdeče:
- `import org.postgresql.Driver;`
- `import org.mindrot.jbcrypt.BCrypt;`



uporabniško ime za testiranje aplikacije je admin. Geslo je admin