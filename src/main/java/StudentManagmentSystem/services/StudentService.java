package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.repository.interfaces.StudentInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class StudentService {

    private final StudentInterface studentRepository;

    public StudentService(StudentInterface studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void addStudent(Student student) {
        validateStudent(student);

        // Provjera da li student već postoji
        if (studentRepository.getStudentByIndex(student.getIndexNumber()).isPresent()) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " već postoji.");
        }

        // Provjera da li je naveden referent koji dodaje studenta
        if (student.getAddedByReferentId() == null || student.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mora postojati ID referenta koji dodaje studenta.");
        }

        studentRepository.addStudent(student);
    }

    public void updateStudent(Student student, String index, String referentId) {
        validateStudent(student);

        if (referentId == null || referentId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta je obavezan za ažuriranje podataka.");
        }

        // Postavljamo datum ažuriranja prije slanja u repozitorij
        student.setUpdatedAt(LocalDateTime.now().toString());

        if (!studentRepository.updateStudent(student, index)) {
            throw new IllegalArgumentException("Student sa indeksom " + index + " nije pronađen za ažuriranje.");
        }
    }

    public void deleteStudent(Student student) {
        // Ovdje bi bilo dobro dodati provjeru: ako student ima upisane predmete (Enrollments),
        // ne bi smio biti obrisan dok se upisi ne uklone.
        if (!studentRepository.deleteStudent(student)) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " nije pronađen za brisanje.");
        }
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

    public ArrayList<Student> findStudentsByLastNamePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return studentRepository.findByLastNamePrefix(prefix);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student objekt ne smije biti null.");
        }

        // Validacija šifre (Novo polje)
        if (student.getPassword() == null || student.getPassword().trim().length() < 4) {
            throw new IllegalArgumentException("Šifra studenta mora imati najmanje 4 znaka.");
        }

        if (student.getIndexNumber() == null || student.getIndexNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Broj indeksa je obavezan.");
        }

        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ime studenta je obavezno.");
        }

        if (student.getFirstName().length() < 2 || student.getFirstName().length() > 50) {
            throw new IllegalArgumentException("Ime mora imati između 2 i 50 znakova.");
        }

        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Prezime studenta je obavezno.");
        }

        if (student.getStudyProgram() == null || student.getStudyProgram().trim().isEmpty()) {
            throw new IllegalArgumentException("Studijski program je obavezan.");
        }

        // Provjera godine upisa
        int currentYear = java.time.Year.now().getValue();
        if (student.getEnrollmentYear() < 2010 || student.getEnrollmentYear() > currentYear + 1) {
            throw new IllegalArgumentException("Godina upisa (" + student.getEnrollmentYear() + ") nije validna.");
        }
    }
}