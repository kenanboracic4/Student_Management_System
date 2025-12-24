package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.repository.interfaces.CourseInterface;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class CourseRepository implements CourseInterface {

    // Pomoćna metoda za mapiranje redova iz baze u objekt Course
    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("sifraPredmeta"),
                rs.getString("naziv"),
                rs.getInt("ects"),
                rs.getInt("semestar"), // Provjeri da li je u bazi semestar ili semester
                rs.getString("datumKreiranja"),
                rs.getString("datumAzuriranja"),
                rs.getString("referentId") // USKLAĐENO SA BAZOM
        );
    }

    @Override
    public void addCourse(Course course) {
        // Koristimo "referentId" jer je tako definisano u tvom DbConnection
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
            // Ispisujemo punu poruku greške da bismo znali ako fali neka kolona
            throw new RuntimeException("Greška u bazi: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateCourse(Course course, String courseCode, String referentId) {
        // Prilikom update-a ručno postavljamo datumAzuriranja na trenutno vrijeme
        String sql = "UPDATE Predmet SET naziv = ?, ects = ?, semestar = ?, datumAzuriranja = ?, referentKojiJeDodao = ? WHERE sifraPredmeta = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, course.getName());
            ps.setInt(2, course.getEcts());
            ps.setInt(3, course.getSemester());
            ps.setString(4, LocalDateTime.now().toString()); // Postavljamo trenutno vrijeme
            ps.setString(5, referentId); // Referent koji je zadnji mijenjao
            ps.setString(6, courseCode);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju predmeta: " + courseCode, e);
        }
    }

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