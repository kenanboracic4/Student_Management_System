package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Referent;
import StudentManagmentSystem.repository.interfaces.ReferentInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * SQL implementacija {@link ReferentInterface} interfejsa.
 * Klasa omogućava upravljanje nalozima referenata i vrši autentifikaciju korisnika
 * direktno nad tabelom {@code Referent} u bazi podataka.
 */
public class ReferentRepository implements ReferentInterface {

    /**
     * Registruje novog referenta u bazu podataka.
     * Snima osnovne podatke i lozinku u plain-text formatu.
     * * @param referent Objekat koji sadrži podatke za novi nalog.
     * @throws RuntimeException Ukoliko dođe do greške pri radu sa bazom.
     */
    @Override
    public void addReferent(Referent referent) {
        String sql = "INSERT INTO Referent (sifraReferenta, password, ime, prezime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referent.getReferentId());
            ps.setString(2, referent.getPassword());
            ps.setString(3, referent.getFirstName());
            ps.setString(4, referent.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dodavanje referenta: " + e.getMessage(), e);
        }
    }

    /**
     * Ažurira postojeće podatke referenta na osnovu primarnog ključa {@code sifraReferenta}.
     * * @param referent Objekat sa novim podacima (ime, prezime, lozinka).
     * @return {@code true} ako je nalog uspješno ažuriran.
     */
    @Override
    public boolean updateReferent(Referent referent) {
        String sql = "UPDATE Referent SET password = ?, ime = ?, prezime = ? WHERE sifraReferenta = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referent.getPassword());
            ps.setString(2, referent.getFirstName());
            ps.setString(3, referent.getLastName());
            ps.setString(4, referent.getReferentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo ažuriranje podataka referenta!", e);
        }
    }

    /**
     * Briše nalog referenta iz baze podataka.
     * * @param referentId Jedinstveni identifikator referenta.
     * @return {@code true} ako je brisanje uspješno izvršeno.
     */
    @Override
    public boolean deleteReferent(String referentId) {
        String sql = "DELETE FROM Referent WHERE sifraReferenta = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju referenta!", e);
        }
    }

    /**
     * Pronalazi referenta prema njegovom identifikatoru.
     * * @param referentId Šifra referenta koja se pretražuje.
     * @return {@link Optional} sa referentom ili {@link Optional#empty()} ako nije pronađen.
     */
    @Override
    public Optional<Referent> getReferentById(String referentId) {
        String sql = "SELECT * FROM Referent WHERE sifraReferenta = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToReferent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvaćanju referenta!", e);
        }
        return Optional.empty();
    }

    /**
     * Vraća listu svih referenata u sistemu.
     * * @return {@link ArrayList} sa svim registrovanim referentima.
     */
    @Override
    public ArrayList<Referent> getAllReferents() {
        String sql = "SELECT * FROM Referent";
        ArrayList<Referent> referents = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                referents.add(mapRowToReferent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri listanju svih referenata!", e);
        }
        return referents;
    }

    /**
     * Provjerava ispravnost korisničkog imena i lozinke.
     * Metoda se poziva prilikom prijave na sistem.
     * * @param referentId Korisničko ime (šifra) referenta.
     * @param password Lozinka.
     * @return {@link Optional} sa objektom {@link Referent} ukoliko su podaci tačni.
     */
    @Override
    public Optional<Referent> login(String referentId, String password) {
        String sql = "SELECT * FROM Referent WHERE sifraReferenta = ? AND password = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referentId);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToReferent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška prilikom prijave na sistem!", e);
        }
        return Optional.empty();
    }

    /**
     * Pomoćna metoda koja mapira podatke iz baze (ResultSet) u Java objekat {@link Referent}.
     * * @param rs ResultSet pozicioniran na željeni red.
     * @return Popunjen objekat tipa {@link Referent}.
     * @throws SQLException Ukoliko kolone u bazi ne odgovaraju očekivanim nazivima.
     */
    private Referent mapRowToReferent(ResultSet rs) throws SQLException {
        return new Referent(
                rs.getString("sifraReferenta"),
                rs.getString("password"),
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getString("datumKreiranja")
        );
    }
}