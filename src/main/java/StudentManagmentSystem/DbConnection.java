package StudentManagmentSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    private static final String JDBC_URL = "jdbc:sqlite:resources/database.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC drajver nije pronađen.", e);
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void initializeDatabase() {
        // 1. REFERENT - Osnova za sve ostale tabele (zbog stranih ključeva)
        String sqlReferent = "CREATE TABLE IF NOT EXISTS Referent (" +
                "sifraReferenta TEXT PRIMARY KEY NOT NULL, " +
                "password TEXT NOT NULL, " + // Mora biti NOT NULL za login
                "ime TEXT NOT NULL, " +
                "prezime TEXT NOT NULL, " +
                "datumKreiranja TEXT DEFAULT (datetime('now','localtime')));";

        // 2. STUDENT - Sadrži sifru i audit polja
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

        // 3. PREDMET - Sadrži referenta koji ga je kreirao
        String sqlPredmet = "CREATE TABLE IF NOT EXISTS Predmet (" +
                "sifraPredmeta TEXT PRIMARY KEY NOT NULL, " +
                "naziv TEXT NOT NULL, " +
                "ects INTEGER NOT NULL, " +
                "semestar INTEGER NOT NULL, " +
                "referentId TEXT, " +
                "datumKreiranja TEXT DEFAULT (datetime('now','localtime')), " +
                "datumAzuriranja TEXT, " +
                "FOREIGN KEY (referentId) REFERENCES Referent(sifraReferenta));";

        // 4. UPIS - Kompozitni ključ i audit polja
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

            // Omogućavanje stranih ključeva u SQLite-u
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Izvršavanje upita redom
            stmt.execute(sqlReferent);
            stmt.execute(sqlStudent);
            stmt.execute(sqlPredmet);
            stmt.execute(sqlUpis);

            System.out.println("Baza podataka je uspješno kreirana sa svim kolonama.");

        } catch (SQLException e) {
            System.err.println("GREŠKA pri inicijalizaciji: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}