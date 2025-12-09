package org.example.repository.interfaces;

import org.example.models.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public interface StudentInterface {

    void addStudent(Student student);

    void updateStudent(Student student, String index);

    void deleteStudent(Student student);

    ArrayList<Student> getAllStudents();


    Optional<Student> getStudentByIndex(String indexNumber);
}
