package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.repository.interfaces.StudentInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Servis koji upravlja poslovnom logikom za podatke o studentima.
 * Služi kao validacioni sloj prije nego što se podaci proslijede u {@link StudentInterface}.
 * Osigurava integritet podataka poput ispravnosti godine upisa i dužine lozinke.
 */
public class StudentService {

    private final StudentInterface studentRepository;

    /**
     * Inicijalizuje servis sa odgovarajućim repozitorijumom.
     * @param studentRepository Implementacija repozitorijuma studenata.
     */
    public StudentService(StudentInterface studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Registruje novog studenta u sistem uz validaciju i provjeru duplikata.
     *
     * @param student Objekat studenta koji se dodaje.
     * @throws IllegalArgumentException Ukoliko student sa tim indeksom već postoji,
     * ako nedostaje ID referenta ili ako podaci ne zadovoljavaju validaciju.
     */
    public void addStudent(Student student) {
        validateStudent(student);

        if (studentRepository.getStudentByIndex(student.getIndexNumber()).isPresent()) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " već postoji.");
        }

        if (student.getAddedByReferentId() == null || student.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mora postojati ID referenta koji dodaje studenta.");
        }

        studentRepository.addStudent(student);
    }

    /**
     * Ažurira podatke postojećeg studenta.
     * Metoda automatski postavlja trenutno sistemsko vrijeme kao datum posljednjeg ažuriranja.
     *
     * @param student Objekat sa novim podacima.
     * @param index Originalni broj indeksa za identifikaciju u bazi.
     * @param referentId ID referenta koji vrši izmjenu.
     * @throws IllegalArgumentException Ako student nije pronađen ili su parametri nevalidni.
     */
    public void updateStudent(Student student, String index, String referentId) {
        validateStudent(student);

        if (referentId == null || referentId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta je obavezan za ažuriranje podataka.");
        }

        // Automatsko postavljanje audit podatka
        student.setUpdatedAt(LocalDateTime.now().toString());

        if (!studentRepository.updateStudent(student, index)) {
            throw new IllegalArgumentException("Student sa indeksom " + index + " nije pronađen za ažuriranje.");
        }
    }

    /**
     * Briše studenta iz sistema.
     * @param student Objekat studenta kojeg treba ukloniti.
     * @throws IllegalArgumentException Ukoliko student sa navedenim indeksom ne postoji.
     */
    public void deleteStudent(Student student) {
        if (!studentRepository.deleteStudent(student)) {
            throw new IllegalArgumentException("Student sa indeksom " + student.getIndexNumber() + " nije pronađen za brisanje.");
        }
    }

    /**
     * Dobavlja listu svih studenata.
     * @return Lista svih objekata tipa {@link Student}.
     */
    public ArrayList<Student> getAllStudents() {
        return studentRepository.getAllStudents();
    }

    /**
     * Pronalazi studenta na osnovu broja indeksa.
     * @param indexNumber Broj indeksa studenta.
     * @return {@link Optional} sa studentom ili prazan ako nije pronađen.
     * @throws IllegalArgumentException Ako je proslijeđeni indeks prazan.
     */
    public Optional<Student> getStudentByIndex(String indexNumber) {
        if (indexNumber == null || indexNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Broj indeksa ne smije biti prazan.");
        }
        return studentRepository.getStudentByIndex(indexNumber);
    }

    /**
     * Pretražuje studente prema početnim slovima prezimena.
     * @param prefix Početna slova za pretragu.
     * @return Lista studenata koji odgovaraju kriteriju.
     */
    public ArrayList<Student> findStudentsByLastNamePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return studentRepository.findByLastNamePrefix(prefix);
    }

    /**
     * Centralna metoda za validaciju podataka o studentu.
     * Pravila uključuju:
     * - Minimalna dužina lozinke (4 znaka)
     * - Ime i prezime ne smiju biti prazni
     * - Dužina imena između 2 i 50 znakova
     * - Godina upisa u realnom opsegu (2010 - tekuća godina + 1)
     *
     * @param student Objekat koji se validira.
     * @throws IllegalArgumentException Ukoliko bilo koji parametar nije validan.
     */
    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student objekt ne smije biti null.");
        }

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

        int currentYear = java.time.Year.now().getValue();
        if (student.getEnrollmentYear() < 2010 || student.getEnrollmentYear() > currentYear + 1) {
            throw new IllegalArgumentException("Godina upisa (" + student.getEnrollmentYear() + ") nije validna.");
        }
    }
}