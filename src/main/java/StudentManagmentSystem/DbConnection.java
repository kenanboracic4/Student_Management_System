package StudentManagmentSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Infrastrukturna klasa zadužena za upravljanje konekcijom sa SQLite bazom podataka.
 * Klasa implementira Singleton-like pristup dobavljanju konekcije i
 * sadrži logiku za inicijalno kreiranje šeme baze podataka (DDL skripte).
 */
public class DbConnection {

    /** Putanja do SQLite fajla baze podataka unutar resursa projekta. */
    private static final String JDBC_URL = "jdbc:sqlite:resources/database.db";

    /**
     * Uspostavlja konekciju sa bazom podataka.
     * Prije uspostavljanja konekcije, provjerava dostupnost SQLite JDBC drajvera.
     * * @return {@link Connection} objekat spreman za SQL operacije.
     * @throws SQLException Ukoliko drajver nije pronađen ili konekcija ne uspije.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC drajver nije pronađen.", e);
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    /**
     * Inicijalizuje bazu podataka kreiranjem svih potrebnih tabela ukoliko one ne postoje.
     * Tabele su strukturirane tako da podržavaju audit trail (trag o izmjenama) i
     * referencijalni integritet putem stranih ključeva.
     * * Redoslijed kreiranja:
     * 1. Referent (nema zavisnosti)
     * 2. Student & Predmet (zavise od Referenta)
     * 3. Upis (zavisi od Studenta, Predmeta i Referenta)
     */
    public static void initializeDatabase() {
        // SQL za tabelu Referent - osnova sistema autentifikacije
        String sqlReferent = "CREATE TABLE IF NOT EXISTS Referent (" +
                "sifraReferenta TEXT PRIMARY KEY NOT NULL, " +
                "password TEXT NOT NULL, " +
                "ime TEXT NOT NULL, " +
                "prezime TEXT NOT NULL, " +
                "datumKreiranja TEXT DEFAULT (datetime('now','localtime')));";

        // SQL za tabelu Student - uključuje audit polja i vezu sa referentom
        String sqlStudent = "CREATE TABLE IF NOT EXISTS Student (" +
                "brojIndeksa TEXT PRIMARY KEY NOT NULL, " +
                "sifra TEXT NOT NULL, " +
                "ime TEXT NOT NULL, " +
                "prezime TEXT NOT NULL, " +
                "studijskiProgram TEXT NOT NULL, " +
                "godinaUpisa INTEGER NOT NULL, " +
                "datumKreiranja TEXT DEFAULT (datetime('now','localtime')), " +
                "datumAzuriranja TEXT, " +
                "referentKojiJeDodao TEXT, " +
                "FOREIGN KEY (referentKojiJeDodao) REFERENCES Referent(sifraReferenta));";

        // SQL za tabelu Predmet - sadrži ECTS bodove i semestar
        String sqlPredmet = "CREATE TABLE IF NOT EXISTS Predmet (" +
                "sifraPredmeta TEXT PRIMARY KEY NOT NULL, " +
                "naziv TEXT NOT NULL, " +
                "ects INTEGER NOT NULL, " +
                "semestar INTEGER NOT NULL, " +
                "referentId TEXT, " +
                "datumKreiranja TEXT DEFAULT (datetime('now','localtime')), " +
                "datumAzuriranja TEXT, " +
                "FOREIGN KEY (referentId) REFERENCES Referent(sifraReferenta));";

        // SQL za tabelu Upis - veza N:M između Studenta i Predmeta sa dodatnim atributima (ocjena)
        String sqlUpis = "CREATE TABLE IF NOT EXISTS Upis (" +
                "brojIndeksa TEXT NOT NULL, " +
                "sifraPredmeta TEXT NOT NULL, " +
                "akademskaGodina TEXT NOT NULL, " +
                "ocjena INTEGER, " +
                "datumOcjene TEXT, " +
                "razlogIzmjeneOcjene TEXT, " +
                "datumIzmjene TEXT, " +
                "referentKojiJeDodao TEXT, " +
                "referentKojiJeIzmijenio TEXT, " +
                "PRIMARY KEY (brojIndeksa, sifraPredmeta, akademskaGodina), " +
                "FOREIGN KEY (brojIndeksa) REFERENCES Student(brojIndeksa) ON DELETE CASCADE, " +
                "FOREIGN KEY (sifraPredmeta) REFERENCES Predmet(sifraPredmeta) ON DELETE CASCADE, " +
                "FOREIGN KEY (referentKojiJeDodao) REFERENCES Referent(sifraReferenta), " +
                "FOREIGN KEY (referentKojiJeIzmijenio) REFERENCES Referent(sifraReferenta));";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Eksplicitno omogućavanje provjere stranih ključeva (SQLite specifičnost)
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute(sqlReferent);
            stmt.execute(sqlStudent);
            stmt.execute(sqlPredmet);
            stmt.execute(sqlUpis);

            System.out.println("Sistem: Baza podataka je uspješno inicijalizovana.");

        } catch (SQLException e) {
            System.err.println("GREŠKA pri inicijalizaciji: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}