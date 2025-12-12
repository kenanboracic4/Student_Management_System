package org.example.repository.implementations;

import org.example.repository.interfaces.EnrollmentInterface;
import org.example.models.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentRepository implements EnrollmentInterface {
    private Connection getConnection() throws SQLException {
        // Implementacija konekcije ovisi o bazi koju koristite.
        // Ovdje bi se trebala nalaziti logika za DriverManager.getConnection() ili DataSource.getConnection().
        // Radi primjera, koristi se apstraktna metoda.
        throw new UnsupportedOperationException("Implementacija getConnection() mora biti dodana.");
    }

    // Helper metoda za mapiranje ResultSet-a na Enrollment objekt
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        // Koristimo prošireni Enrollment model
        return new Enrollment(
                rs.getString("brojIndeksa"),
                rs.getString("sifraPredmeta"),
                rs.getString("akademskaGodina"),
                // INTEGER može vratiti null u bazi, stoga koristimo Integer objekt
                (Integer) rs.getObject("ocjena"),
                rs.getString("datumOcjene"),
                rs.getString("razlogIzmjeneOcjene"),
                rs.getString("datumIzmjene")
        );
    }

    @Override
    public Enrollment create(Enrollment enrollment) {
        // Upis/Enrollment nema generirani ID jer se koristi kompozitni primarni ključ.
        String sql = "INSERT INTO Upis (brojIndeksa, sifraPredmeta, akademskaGodina, ocjena, datumOcjene, razlogIzmjeneOcjene, datumIzmjene) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enrollment.getStudentIndexNumber());
            stmt.setString(2, enrollment.getCourseCode());
            stmt.setString(3, enrollment.getAcademicYear());

            // Postavljanje ocjene (može biti NULL)
            if (enrollment.getGrade() != null) {
                stmt.setInt(4, enrollment.getGrade());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, enrollment.getGradeDate());
            stmt.setString(6, enrollment.getChangeReason());
            stmt.setString(7, enrollment.getChangeDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                // Može se desiti zbog kršenja Foreign Key ili Unique Constraint-a
                throw new SQLException("Kreiranje upisa nije uspjelo, nijedan red nije dodan.");
            }
            return enrollment;

        } catch (SQLException e) {
            // Logirajte grešku: e.getMessage()
            throw new RuntimeException("Greška pri kreiranju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Enrollment> findByPrimaryKey(String studentIndexNumber, String courseCode, String academicYear) {
        String sql = "SELECT * FROM Upis WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIndexNumber);
            stmt.setString(2, courseCode);
            stmt.setString(3, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu upisa po PK: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Enrollment> findByStudentAndYear(String studentIndexNumber, String academicYear) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Upis WHERE brojIndeksa = ? AND akademskaGodina = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIndexNumber);
            stmt.setString(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu upisa za studenta i godinu: " + e.getMessage(), e);
        }
        return enrollments;
    }

    @Override
    public boolean update(Enrollment enrollment) {
        // Primarno se koristi za ažuriranje ocjene i datuma
        String sql = "UPDATE Upis SET ocjena = ?, datumOcjene = ?, razlogIzmjeneOcjene = ?, datumIzmjene = ? " +
                "WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Postavljanje ocjene (može biti NULL)
            if (enrollment.getGrade() != null) {
                stmt.setInt(1, enrollment.getGrade());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, enrollment.getGradeDate());
            stmt.setString(3, enrollment.getChangeReason());
            stmt.setString(4, enrollment.getChangeDate());

            // Kompozitni ključ za WHERE klauzulu
            stmt.setString(5, enrollment.getStudentIndexNumber());
            stmt.setString(6, enrollment.getCourseCode());
            stmt.setString(7, enrollment.getAcademicYear());

            // Vraćamo broj redova pogođenih upitom (0 = neuspjeh, 1 = uspjeh)
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String studentIndexNumber, String courseCode, String academicYear) {
        String sql = "DELETE FROM Upis WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIndexNumber);
            stmt.setString(2, courseCode);
            stmt.setString(3, academicYear);

            // Vraćamo boolean baziran na broju obrisanih redova
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        // Implementacija za dohvat svih upisa
        // ... (Slično kao findByStudentAndYear, ali bez WHERE klauzule)
        return new ArrayList<>(); // Može se implementirati po potrebi
    }
}
