package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Course;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfejs koji definiše operacije nad podacima o kursevima (predmetima).
 * Služi kao apstrakcija za pristup bazi podataka ili drugom izvoru podataka.
 */
public interface CourseInterface {

    /**
     * Dodaje novi predmet u sistem.
     * Objekat kursa treba da sadrži identifikator referenta koji ga kreira.
     *
     * @param course Objekat tipa {@link Course} koji sadrži podatke o novom predmetu.
     */
    void addCourse(Course course);

    /**
     * Ažurira podatke o postojećem predmetu.
     * * @param course Objekat sa novim podacima koji se snimaju.
     * @param courseCode Originalni kod predmeta (primarni ključ) prije izmjene.
     * @param referentId ID referenta koji vrši izmjenu (koristi se za praćenje izmjena).
     * @return {@code true} ako je ažuriranje uspješno, {@code false} u suprotnom.
     */
    boolean updateCourse(Course course, String courseCode, String referentId);

    /**
     * Briše predmet iz sistema na osnovu njegove jedinstvene šifre.
     *
     * @param courseCode Šifra predmeta koji se briše.
     * @return {@code true} ako je predmet obrisan, {@code false} ako nije pronađen ili brisanje nije uspjelo.
     */
    boolean deleteCourse(String courseCode);

    /**
     * Vraća listu svih predmeta koji postoje u sistemu.
     *
     * @return {@link ArrayList} koji sadrži sve objekte {@link Course}.
     */
    ArrayList<Course> getAllCourses();

    /**
     * Pronalazi specifičan predmet na osnovu njegove šifre.
     * Koristi {@link Optional} kako bi se izbjegao NullPointerException ako predmet ne postoji.
     *
     * @param courseCode Šifra predmeta koji se traži.
     * @return {@link Optional} koji sadrži pronađeni predmet ili je prazan.
     */
    Optional<Course> getCourseByCode(String courseCode);

    /**
     * Pretražuje predmete čiji naziv počinje zadatim prefiksom.
     * Korisno za funkcionalnost pretrage u realnom vremenu (autocomplete).
     *
     * @param prefix Početna slova naziva predmeta.
     * @return Lista predmeta koji zadovoljavaju kriterij pretrage.
     */
    ArrayList<Course> findCoursesByNamePrefix(String prefix);

    /**
     * Provjerava da li na navedeni predmet ima upisanih studenata.
     * Korisno prije brisanja predmeta radi očuvanja integriteta podataka.
     *
     * @param courseCode Šifra predmeta za koji se vrši provjera.
     * @return {@code true} ako postoje upisi (enrollments), inače {@code false}.
     */
    boolean hasEnrollments(String courseCode);

    /**
     * Pronalazi sve predmete koje je u sistem dodao određeni referent.
     *
     * @param referentId ID referenta čiji se predmeti traže.
     * @return Lista predmeta koje je kreirao navedeni referent.
     */
    ArrayList<Course> getCoursesByReferent(String referentId);
}