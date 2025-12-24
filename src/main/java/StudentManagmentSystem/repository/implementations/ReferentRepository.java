package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Referent;
import StudentManagmentSystem.repository.interfaces.ReferentInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class ReferentRepository implements ReferentInterface {

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

    // Pomoćna metoda za mapiranje rezultata iz baze u objekt
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