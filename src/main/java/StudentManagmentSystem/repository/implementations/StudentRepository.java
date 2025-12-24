package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.repository.interfaces.StudentInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class StudentRepository implements StudentInterface {

    @Override
    public void addStudent(Student student) {
        // Uključujemo šifru i referenta. datumKreiranja puni SQLite (DEFAULT CURRENT_TIMESTAMP)
        String sql = "INSERT INTO Student (brojIndeksa, sifra, ime, prezime, studijskiProgram, godinaUpisa, referentKojiJeDodao) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, student.getIndexNumber());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getFirstName());
            ps.setString(4, student.getLastName());
            ps.setString(5, student.getStudyProgram());
            ps.setInt(6, student.getEnrollmentYear());
            ps.setString(7, student.getAddedByReferentId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dodavanje studenta: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStudent(Student student, String index) {
        // Ažuriramo i šifru i datumAzuriranja
        String sql = "UPDATE Student SET sifra = ?, ime = ?, prezime = ?, studijskiProgram = ?, godinaUpisa = ?, datumAzuriranja = ? WHERE brojIndeksa = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getPassword());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getStudyProgram());
            ps.setInt(5, student.getEnrollmentYear());
            ps.setString(6, student.getUpdatedAt()); // Postavljeno u servisu
            ps.setString(7, index);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo ažuriranje studenta!", e);
        }
    }

    @Override
    public boolean deleteStudent(Student student) {
        String sql = "DELETE FROM Student WHERE brojIndeksa = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, student.getIndexNumber());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju studenta!", e);
        }
    }

    @Override
    public ArrayList<Student> getAllStudents() {
        String sql = "SELECT * FROM Student";
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspio prikaz svih studenata!", e);
        }
        return students;
    }

    @Override
    public Optional<Student> getStudentByIndex(String indexNumber) {
        String sql = "SELECT * FROM Student WHERE brojIndeksa = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, indexNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvaćanju studenta!", e);
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Student> findByLastNamePrefix(String prefix) {
        String sql = "SELECT * FROM Student WHERE prezime LIKE ?";
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri pretrazi studenata!", e);
        }
        return students;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("brojIndeksa"),
                rs.getString("sifra"),             // Mapiramo šifru
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getString("studijskiProgram"),
                rs.getInt("godinaUpisa"),
                rs.getString("datumKreiranja"),   // Mapiramo audit polja
                rs.getString("datumAzuriranja"),
                rs.getString("referentKojiJeDodao")
        );
    }
}