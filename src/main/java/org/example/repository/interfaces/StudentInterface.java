package org.example.repository.interfaces;

import org.example.models.Student;


import java.util.ArrayList;
import java.util.Optional;

public interface StudentInterface {

    void addStudent(Student student);


    boolean updateStudent(Student student, String index);


    boolean deleteStudent(Student student);

    ArrayList<Student> getAllStudents();

    Optional<Student> getStudentByIndex(String indexNumber);
}