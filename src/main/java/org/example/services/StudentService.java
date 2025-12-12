package org.example.services;

import org.example.models.Student;
import org.example.repository.implementations.StudentRepository;
import org.example.repository.interfaces.StudentInterface;

import java.util.ArrayList;
import java.util.Optional;

public class StudentService {

    private final StudentInterface studentRepository;

    public StudentService(StudentInterface studentRepository) {
        this.studentRepository = studentRepository;
    }


    public void addStudent(Student student) {

        validateStudent(student);


        if (studentRepository.getStudentByIndex(student.getIndexNumber()).isPresent()) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " već postoji.");
        }


        studentRepository.addStudent(student);
    }


    public void updateStudent(Student student, String index) {

        validateStudent(student);


        if (studentRepository.getStudentByIndex(index).isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom " + index + " nije pronađen za ažuriranje.");
        }


        studentRepository.updateStudent(student, index);
    }


    public void deleteStudent(Student student) {

        if (studentRepository.getStudentByIndex(student.getIndexNumber()).isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " nije pronađen za brisanje.");
        }

        studentRepository.deleteStudent(student);
    }


    public ArrayList<Student> getAllStudents() {
        return studentRepository.getAllStudents();
    }


    public Optional<Student> getStudentByIndex(String indexNumber) {
        if (indexNumber == null || indexNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Broj indeksa ne smije biti prazan.");
        }
        return studentRepository.getStudentByIndex(indexNumber);
    }


    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student objekat ne smije biti null.");
        }


        if (student.getIndexNumber() == null || student.getIndexNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Broj indeksa je obavezan.");
        }

        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ime studenta je obavezno.");
        }

        if (student.getFirstName().length() < 2 || student.getFirstName().length() > 50) {
            throw new IllegalArgumentException("Ime studenta mora imati između 2 i 50 znakova.");
        }


        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Prezime studenta je obavezno.");
        }


        if (student.getStudyProgram() == null || student.getStudyProgram().trim().isEmpty()) {
            throw new IllegalArgumentException("Studijski program je obavezan.");
        }


        int currentYear = java.time.Year.now().getValue();
        if ( student.getEnrollmentYear() > currentYear) {
            throw new IllegalArgumentException("Godina upisa (" + student.getEnrollmentYear() + ") nije validna.");
        }
    }
}