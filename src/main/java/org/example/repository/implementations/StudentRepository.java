package org.example.repository.implementations;

import org.example.DbConnection;
import org.example.models.Student;
import org.example.repository.interfaces.StudentInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class StudentRepository implements StudentInterface {

    @Override
    public void addStudent(Student student) {
        String sql = "INSERT INTO STUDENT (brojIndeksa, ime, prezime, studijskiProgram, godinaUpisa) VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, student.getIndexNumber());
            ps.setString(2, student.getFirstName());
            ps.setString(3,student.getLastName());
            ps.setString(4, student.getStudyProgram());
            ps.setInt(5, student.getEnrollmentYear());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dodavanje studenta!");
        }

    }

    @Override
    public void updateStudent(Student student) {

    }

    @Override
    public void updateStudent(Student student, String index) {
        String sql = "UPDATE Student SET ime = ?, prezime = ?, studijskiProgram = ?, godinaUpisa = ? WHERE brojIndeksa = ?";

       try(Connection connection = DbConnection.getConnection();
       PreparedStatement ps = connection.prepareStatement(sql)) {
           ps.setString(1, student.getFirstName());
           ps.setString(2, student.getLastName());
           ps.setString(3, student.getStudyProgram());
           ps.setInt(4, student.getEnrollmentYear());

           ps.setString(5, index);

           ps.executeUpdate();
       } catch (SQLException e) {
           throw new RuntimeException("Nije uspjelo azuriranje studenta!");

       }
    }

    @Override
    public void deleteStudent(Student student) throws SQLException {
        String sql = "DELETE FROM Student WHERE brojIndeksa = ? ";

        try(Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, student.getIndexNumber());
        }catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo brisanje studenta!");

        }

    }

    @Override
    public ArrayList<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM Student";
        ArrayList<Student> students = new ArrayList<>();

        try(Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

             while(rs.next()){
                 Student student = mapRowToStudent(rs);
                 students.add(student);
             }

        }catch (SQLException e) {
            throw new RuntimeException("Nije uspjeo prikaz svih studenata!");
        }
    
        return students;
    }



    @Override
    public Optional<Student> getStudentByIndex(String indexNumber) {
        String sql = "SELECT * FROM Student where brojIndeksa = ?";

        try(Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, indexNumber  );
            try (ResultSet rs = ps.executeQuery()) {


                if (rs.next()) {

                    Student student = mapRowToStudent(rs);
                    return Optional.of(student);
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo brisanje studenta!");

        }
        return Optional.empty();
        
    }



    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        
        return new Student(
                rs.getString("brojIndeksa"),
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getString("studijskiProgram"),
                rs.getInt("godinaUpisa")
        );
    }

}
