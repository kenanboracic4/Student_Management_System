package org.example.repository.implementations;

import org.example.DbConnection;
import org.example.models.Course;
import org.example.repository.interfaces.CourseInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class CourseRepository implements CourseInterface {

    private Course mapRowToCourse(ResultSet rs) throws SQLException {

        return new Course(
                rs.getString("sifraPredmeta"),
                rs.getString("naziv"),
                rs.getInt("ects"),
                rs.getInt("semestar")
        );
    }

    @Override
    public void addCourse(Course course) {
        String sql = "INSERT INTO Predmet (sifraPredmeta, naziv, ects, semestar) VALUES (?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {


            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getName());
            ps.setInt(3, course.getEcts());
            ps.setInt(4, course.getSemester());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dodavanje predmeta (Šifra: " + course.getCourseCode() + ")!", e);
        }
    }

    @Override
    public boolean updateCourse(Course course, String courseCode) {
        String sql = "UPDATE Predmet SET naziv = ?, ects = ?, semestar = ? WHERE sifraPredmeta = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, course.getName());
            ps.setInt(2, course.getEcts());
            ps.setInt(3, course.getSemester());

            ps.setString(4, courseCode);

            int rowsAffected = ps.executeUpdate();


            return rowsAffected > 0; // true ako je izmijenjeno

        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo ažuriranje predmeta (Šifra: " + courseCode + ")!", e);
        }
    }

    @Override
    public boolean deleteCourse(String courseCode) {
        String sql = "DELETE FROM Predmet WHERE sifraPredmeta = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            int rowsAffected = ps.executeUpdate();


            return rowsAffected > 0; // true ako je izbrisano

        } catch (SQLException e) {

            throw new RuntimeException("Nije uspjelo brisanje predmeta (Šifra: " + courseCode + ")! "
                    + "Provjerite da li predmet ima povezane upise.", e);
        }
    }
    @Override
    public ArrayList<Course> getAllCourses() {
        String sql = "SELECT * FROM Predmet";
        ArrayList<Course> courses = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspio prikaz svih predmeta!", e);
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
                if (rs.next()) {
                    return Optional.of(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dohvaćanje predmeta po šifri!", e);
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
                while (rs.next()) {
                    courses.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjela pretraga predmeta po nazivu!", e);
        }
        return courses;
    }
}