package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Student;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfejs koji definiše ugovor za rad sa podacima o studentima.
 * Pruža metode za osnovne CRUD operacije, kao i specifične funkcionalnosti
 * poput pretrage po prefiksu prezimena.
 */
public interface StudentInterface {

    /**
     * Registruje novog studenta u sistemu.
     * Implementacija treba da osigura perzistenciju svih polja modela {@link Student},
     * uključujući lozinku i ID referenta koji je izvršio unos.
     *
     * @param student Objekat koji sadrži sve neophodne informacije o novom studentu.
     */
    void addStudent(Student student);

    /**
     * Ažurira podatke o postojećem studentu.
     * Metoda dozvoljava izmjenu svih polja, uključujući i broj indeksa, zbog čega
     * je potrebno proslijediti originalni indeks radi identifikacije zapisa u bazi.
     *
     * @param student Objekt sa novim podacima (ime, prezime, program, lozinka...).
     * @param index Originalni broj indeksa prije nego što je eventualno promijenjen.
     * @return {@code true} ako je ažuriranje u bazi podataka uspješno, inače {@code false}.
     */
    boolean updateStudent(Student student, String index);

    /**
     * Briše studenta iz baze podataka na osnovu dostavljenog objekta.
     * Obično se brisanje vrši prema broju indeksa koji se nalazi unutar objekta.
     *
     * @param student Objekat studenta kojeg je potrebno obrisati.
     * @return {@code true} ako je brisanje izvršeno, inače {@code false}.
     */
    boolean deleteStudent(Student student);

    /**
     * Vraća listu svih studenata registrovanih u bazi podataka.
     *
     * @return {@link ArrayList} koji sadrži sve objekte tipa {@link Student}.
     */
    ArrayList<Student> getAllStudents();

    /**
     * Pronalazi studenta na osnovu njegovog jedinstvenog broja indeksa.
     *
     * @param indexNumber Broj indeksa studenta koji se traži.
     * @return {@link Optional} sa pronađenim studentom ili {@link Optional#empty()} ako student ne postoji.
     */
    Optional<Student> getStudentByIndex(String indexNumber);

    /**
     * Vrši pretragu studenata čije prezime počinje sa određenim slovima (prefiks).
     * Ova funkcionalnost je optimizovana za filtriranje prikaza u tabelama ili pretragu.
     *
     * @param prefix Početna slova prezimena (npr. "Me" za Mehić, Memić...).
     * @return Lista studenata čije prezime odgovara zadatom prefiksu.
     */
    ArrayList<Student> findByLastNamePrefix(String prefix);
}