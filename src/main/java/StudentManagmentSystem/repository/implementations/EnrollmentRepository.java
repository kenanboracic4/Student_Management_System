package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.repository.interfaces.EnrollmentInterface;
import StudentManagmentSystem.models.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQL implementacija {@link EnrollmentInterface} interfejsa.
 * Upravlja perzistencijom podataka o upisima studenata na predmete.
 * Ova klasa koristi tabelu {@code Upis} i podržava praćenje referenata koji su kreirali ili izmijenili zapis.
 */
public class EnrollmentRepository implements EnrollmentInterface {

    /**
     * Pomoćna metoda za transformaciju reda iz baze podataka u objekat {@link Enrollment}.
     * Posebno rukuje opcionim poljima kao što su ocjena i metapodaci o izmjenama.
     *
     * @param rs ResultSet pozicioniran na trenutni red.
     * @return {@link Enrollment} objekat sa podacima iz baze.
     * @throws SQLException Ukoliko kolone nisu dostupne ili dođe do greške u konekciji.
     */
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        return new Enrollment(
                rs.getString("brojIndeksa"),
                rs.getString("sifraPredmeta"),
                rs.getString("akademskaGodina"),
                (Integer) rs.getObject("ocjena"),
                rs.getString("datumOcjene"),
                rs.getString("razlogIzmjeneOcjene"),
                rs.getString("datumIzmjene"),
                rs.getString("referentKojiJeDodao"),
                rs.getString("referentKojiJeIzmijenio")
        );
    }

    /**
     * Snima novi upis u bazu podataka.
     * Postavlja {@code referentKojiJeDodao} kako bi se osigurao audit trail (trag o kreiranju).
     *
     * @param enrollment Objekat koji sadrži podatke o upisu.
     * @return Spašeni {@link Enrollment} objekat.
     * @throws RuntimeException Ukoliko upis ne uspije zbog SQL ograničenja ili greške.
     */
    @Override
    public Enrollment create(Enrollment enrollment) {
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
            stmt.setString(8, enrollment.getAddedByReferentId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Kreiranje upisa nije uspjelo.");
            }
            return enrollment;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri kreiranju upisa: " + e.getMessage(), e);
        }
    }

    /**
     * Ažurira postojeći upis u bazi.
     * Ova metoda se najčešće koristi za unos ocjene nakon ispita ili naknadnu korekciju podataka.
     * Obavezno snima ID referenta koji vrši izmjenu.
     *
     * @param enrollment Objekat sa novim podacima.
     * @return {@code true} ako je bar jedan red u bazi ažuriran.
     */
    @Override
    public boolean update(Enrollment enrollment) {
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
            stmt.setString(5, enrollment.getModifiedByReferentId());

            stmt.setString(6, enrollment.getStudentIndexNumber());
            stmt.setString(7, enrollment.getCourseCode());
            stmt.setString(8, enrollment.getAcademicYear());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju upisa: " + e.getMessage(), e);
        }
    }

    /**
     * Pronalazi jedinstven zapis o upisu koristeći kompozitni ključ.
     *
     * @param studentIndexNumber Indeks studenta.
     * @param courseCode Šifra predmeta.
     * @param academicYear Akademska godina.
     * @return {@link Optional} sa pronađenim upisom.
     */
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

    /**
     * Vraća listu upisa za određenog studenta u specifičnoj godini.
     *
     * @param studentIndexNumber Indeks studenta.
     * @param academicYear Akademska godina.
     * @return Lista upisa (npr. svi predmeti koje je student slušao 2023/24).
     */
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

    /**
     * Briše zapis o upisu na osnovu primarnog ključa.
     *
     * @return {@code true} ako je brisanje uspješno.
     */
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

    /**
     * Vraća sve upise u bazi podataka bez filtriranja.
     *
     * @return Lista svih {@link Enrollment} objekata.
     */
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