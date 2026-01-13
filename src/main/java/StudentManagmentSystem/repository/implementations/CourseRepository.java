package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.repository.interfaces.CourseInterface;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * SQL implementacija {@link CourseInterface} interfejsa.
 * Ova klasa upravlja perzistencijom podataka o predmetima koristeći JDBC i
 * komunicira direktno sa tabelom {@code Predmet} u bazi podataka.
 */
public class CourseRepository implements CourseInterface {

    /**
     * Pomoćna metoda koja mapira trenutni red iz {@link ResultSet}-a u objekat {@link Course}.
     * * @param rs ResultSet koji se trenutno obrađuje.
     * @return Instanca klase {@link Course} sa podacima iz baze.
     * @throws SQLException Ako dođe do greške pri čitanju kolona iz baze.
     */
    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("sifraPredmeta"),
                rs.getString("naziv"),
                rs.getInt("ects"),
                rs.getInt("semestar"),
                rs.getString("datumKreiranja"),
                rs.getString("datumAzuriranja"),
                rs.getString("referentId")
        );
    }

    /**
     * Ubacuje novi zapis o predmetu u tabelu {@code Predmet}.
     * Datum kreiranja i ažuriranja se obično postavljaju automatski putem baze (trigger/default).
     * * @param course Objekat kursa koji se spašava.
     * @throws RuntimeException Ukoliko dođe do SQL greške.
     */
    @Override
    public void addCourse(Course course) {
        String sql = "INSERT INTO Predmet (sifraPredmeta, naziv, ects, semestar, referentId) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getName());
            ps.setInt(3, course.getEcts());
            ps.setInt(4, course.getSemester());
            ps.setString(5, course.getAddedByReferentId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška u bazi pri dodavanju predmeta: " + e.getMessage(), e);
        }
    }

    /**
     * Ažurira postojeći zapis u bazi podataka.
     * Ručno postavlja kolonu {@code datumAzuriranja} na trenutno sistemsko vrijeme.
     * * @param course Objekat sa novim podacima.
     * @param courseCode Originalna šifra predmeta koji se mijenja.
     * @param referentId ID referenta koji vrši izmjenu.
     * @return true ako je red u bazi uspješno izmijenjen.
     */
    @Override
    public boolean updateCourse(Course course, String courseCode, String referentId) {
        String sql = "UPDATE Predmet SET naziv = ?, ects = ?, semestar = ?, datumAzuriranja = ?, referentKojiJeDodao = ? WHERE sifraPredmeta = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, course.getName());
            ps.setInt(2, course.getEcts());
            ps.setInt(3, course.getSemester());
            ps.setString(4, LocalDateTime.now().toString());
            ps.setString(5, referentId);
            ps.setString(6, courseCode);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju predmeta: " + courseCode, e);
        }
    }

    /**
     * Briše red iz tabele {@code Predmet}.
     * Napomena: Brisanje može baciti grešku ako postoje strani ključevi u tabeli {@code Upis}.
     * * @param courseCode Šifra predmeta za brisanje.
     * @return true ako je zapis obrisan.
     */
    @Override
    public boolean deleteCourse(String courseCode) {
        String sql = "DELETE FROM Predmet WHERE sifraPredmeta = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo brisanje. Provjerite zavisnosti za: " + courseCode, e);
        }
    }

    /**
     * Provjerava postojanje zapisa u tabeli {@code Upis} za zadati predmet.
     * Koristi se za validaciju prije brisanja predmeta.
     * * @param courseCode Šifra predmeta.
     * @return true ako postoji bar jedan upis.
     */
    @Override
    public boolean hasEnrollments(String courseCode) {
        String sql = "SELECT COUNT(*) FROM Upis WHERE sifraPredmeta = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri provjeri upisa.", e);
        }
        return false;
    }

    /**
     * Vraća sve zapise iz tabele {@code Predmet}.
     * * @return Lista svih predmeta u sistemu.
     */
    @Override
    public ArrayList<Course> getAllCourses() {
        String sql = "SELECT * FROM Predmet";
        ArrayList<Course> courses = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu svih predmeta: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Pronalazi predmet po primarnom ključu.
     * * @param courseCode Šifra predmeta.
     * @return {@link Optional} sa predmetom ili prazan ako nije pronađen.
     */
    @Override
    public Optional<Course> getCourseByCode(String courseCode) {
        String sql = "SELECT * FROM Predmet WHERE sifraPredmeta = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu predmeta po šifri.", e);
        }
        return Optional.empty();
    }

    /**
     * Pretraga predmeta pomoću SQL operatora {@code LIKE}.
     * * @param prefix Početak naziva predmeta.
     * @return Lista predmeta koji odgovaraju pretrazi.
     */
    @Override
    public ArrayList<Course> findCoursesByNamePrefix(String prefix) {
        String sql = "SELECT * FROM Predmet WHERE naziv LIKE ?";
        ArrayList<Course> courses = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri pretrazi predmeta.", e);
        }
        return courses;
    }

    /**
     * Filtrira predmete na osnovu kolone {@code referentKojiJeDodao}.
     * * @param referentId ID referenta.
     * @return Lista predmeta koje je kreirao određeni referent.
     */
    @Override
    public ArrayList<Course> getCoursesByReferent(String referentId) {
        String sql = "SELECT * FROM Predmet WHERE referentKojiJeDodao = ?";
        ArrayList<Course> courses = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, referentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu predmeta za referenta.", e);
        }
        return courses;
    }
}