package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Student;
import java.util.ArrayList;
import java.util.Optional;

public interface StudentInterface {

    /**
     * Dodaje novog studenta u bazu podataka.
     * Model studenta treba sadržati sifru i ID referenta koji ga dodaje.
     */
    void addStudent(Student student);

    /**
     * Ažurira podatke o studentu.
     * @param student Objekt sa novim podacima (ime, prezime, program, sifra...).
     * @param index Originalni broj indeksa (u slučaju da se on mijenja).
     * @return true ako je ažuriranje uspješno.
     */
    boolean updateStudent(Student student, String index);

    /**
     * Briše studenta iz baze podataka.
     */
    boolean deleteStudent(Student student);

    /**
     * Vraća listu svih studenata.
     */
    ArrayList<Student> getAllStudents();

    /**
     * Pronalazi studenta na osnovu broja indeksa.
     */
    Optional<Student> getStudentByIndex(String indexNumber);

    /**
     * Pretraga studenata prema početnim slovima prezimena.
     */
    ArrayList<Student> findByLastNamePrefix(String prefix);
}