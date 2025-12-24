package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.repository.interfaces.EnrollmentInterface;
import StudentManagmentSystem.models.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentRepository implements EnrollmentInterface {

    // Ažurirana pomoćna metoda za mapiranje reda (uključuje referente)
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        return new Enrollment(
                rs.getString("brojIndeksa"),
                rs.getString("sifraPredmeta"),
                rs.getString("akademskaGodina"),
                (Integer) rs.getObject("ocjena"),
                rs.getString("datumOcjene"),
                rs.getString("razlogIzmjeneOcjene"),
                rs.getString("datumIzmjene"),
                rs.getString("referentKojiJeDodao"),    // NOVO
                rs.getString("referentKojiJeIzmijenio") // NOVO
        );
    }

    @Override
    public Enrollment create(Enrollment enrollment) {
        // Dodata kolona referentKojiJeDodao
        String sql = "INSERT INTO Upis (brojIndeksa, sifraPredmeta, akademskaGodina, ocjena, datumOcjene, " +
                "razlogIzmjeneOcjene, datumIzmjene, referentKojiJeDodao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enrollment.getStudentIndexNumber());
            stmt.setString(2, enrollment.getCourseCode());
            stmt.setString(3, enrollment.getAcademicYear());

            if (enrollment.getGrade() != null) {
                stmt.setInt(4, enrollment.getGrade());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, enrollment.getGradeDate());
            stmt.setString(6, enrollment.getChangeReason());
            stmt.setString(7, enrollment.getChangeDate());
            stmt.setString(8, enrollment.getAddedByReferentId()); // NOVO

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Kreiranje upisa nije uspjelo.");
            }
            return enrollment;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri kreiranju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Enrollment enrollment) {
        // Ažurirano da uključuje i referenta koji je izvršio izmjenu
        String sql = "UPDATE Upis SET ocjena = ?, datumOcjene = ?, razlogIzmjeneOcjene = ?, " +
                "datumIzmjene = ?, referentKojiJeIzmijenio = ? " +
                "WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (enrollment.getGrade() != null) {
                stmt.setInt(1, enrollment.getGrade());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, enrollment.getGradeDate());
            stmt.setString(3, enrollment.getChangeReason());
            stmt.setString(4, enrollment.getChangeDate());
            stmt.setString(5, enrollment.getModifiedByReferentId()); // NOVO

            // Primarni ključ za WHERE klauzulu
            stmt.setString(6, enrollment.getStudentIndexNumber());
            stmt.setString(7, enrollment.getCourseCode());
            stmt.setString(8, enrollment.getAcademicYear());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Enrollment> findByPrimaryKey(String studentIndexNumber, String courseCode, String academicYear) {
        String sql = "SELECT * FROM Upis WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";
        try (Connection conn = DbConnection.getConnection();
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
            throw new RuntimeException("Greška pri dohvatu upisa: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Enrollment> findByStudentAndYear(String studentIndexNumber, String academicYear) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Upis WHERE brojIndeksa = ? AND akademskaGodina = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIndexNumber);
            stmt.setString(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu upisa za godinu: " + e.getMessage(), e);
        }
        return enrollments;
    }

    @Override
    public boolean delete(String studentIndexNumber, String courseCode, String academicYear) {
        String sql = "DELETE FROM Upis WHERE brojIndeksa = ? AND sifraPredmeta = ? AND akademskaGodina = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentIndexNumber);
            stmt.setString(2, courseCode);
            stmt.setString(3, academicYear);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju upisa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        String sql = "SELECT * FROM Upis";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                enrollments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu svih upisa: " + e.getMessage(), e);
        }
        return enrollments;
    }
}