package org.example.repository.interfaces;

import org.example.models.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public interface StudentInterface {

    void addStudent(Student student);
    void updateStudent(Student student);

    void updateStudent(Student student, String index);

    void deleteStudent(Student student) throws SQLException;

    ArrayList<Student> getAllStudents() throws SQLException;


    Optional<Student> getStudentByIndex(String indexNumber);
}
