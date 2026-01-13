package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.repository.interfaces.CourseInterface;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Servis koji upravlja poslovnom logikom vezanom za predmete (kurseve).
 * Ova klasa služi kao posrednik između korisničkog interfejsa i repozitorijuma,
 * osiguravajući da su svi podaci validni prije nego što se proslijede u bazu podataka.
 */
public class CourseService {

    private final CourseInterface courseRepository;

    /**
     * Konstruktor za Dependency Injection.
     *
     * @param courseRepository Implementacija repozitorijuma koja će se koristiti za pristup podacima.
     */
    public CourseService(CourseInterface courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Dodaje novi predmet u sistem uz prethodnu validaciju.
     * Provjerava da li šifra već postoji i da li je ID referenta postavljen.
     *
     * @param course Objekat predmeta koji se dodaje.
     * @throws IllegalArgumentException Ako predmet već postoji, ako nedostaje ID referenta ili su podaci neispravni.
     */
    public void addCourse(Course course) {
        // Validacija osnovnih podataka (ECTS, semestar, naziv)
        validateCourse(course, course.getCourseCode());

        // Provjera jedinstvenosti šifre predmeta
        if (courseRepository.getCourseByCode(course.getCourseCode()).isPresent()) {
            throw new IllegalArgumentException("Predmet sa šifrom " + course.getCourseCode() + " već postoji.");
        }

        // Provjera audit polja (ko dodaje predmet)
        if (course.getAddedByReferentId() == null || course.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mora postojati ID referenta koji dodaje predmet.");
        }

        courseRepository.addCourse(course);
    }

    /**
     * Ažurira podatke o postojećem predmetu.
     *
     * @param course Objekat sa novim podacima.
     * @param courseCode Šifra predmeta koji se ažurira.
     * @param referentId ID referenta koji vrši izmjenu (za audit log).
     * @throws IllegalArgumentException Ako predmet nije pronađen ili su ulazni parametri neispravni.
     */
    public void updateCourse(Course course, String courseCode, String referentId) {
        validateCourse(course, courseCode);

        if (referentId == null || referentId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta je obavezan za ažuriranje podataka.");
        }

        if (!courseRepository.updateCourse(course, courseCode, referentId)) {
            throw new IllegalArgumentException("Predmet sa šifrom " + courseCode + " nije pronađen za ažuriranje.");
        }
    }

    /**
     * Sigurno briše predmet iz sistema.
     * Prije brisanja provjerava postojanje predmeta i da li na njemu postoje aktivni upisi studenata
     * kako bi se spriječilo narušavanje integriteta baze podataka.
     *
     * @param courseCode Šifra predmeta za brisanje.
     * @throws IllegalArgumentException Ako predmet ne postoji.
     * @throws IllegalStateException Ako predmet ima upisane studente.
     */
    public void deleteCourse(String courseCode) {
        // Provjera postojanja
        Optional<Course> course = courseRepository.getCourseByCode(courseCode);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Predmet sa šifrom " + courseCode + " ne postoji.");
        }

        // Provjera integriteta podataka (strani ključevi)
        if (courseRepository.hasEnrollments(courseCode)) {
            throw new IllegalStateException("Nije dozvoljeno brisanje: Predmet ima aktivne upise studenata.");
        }

        courseRepository.deleteCourse(courseCode);
    }

    /**
     * Dobavlja sve predmete iz baze podataka.
     *
     * @return Lista svih dostupnih predmeta.
     */
    public ArrayList<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    /**
     * Pronalazi predmet na osnovu njegove jedinstvene šifre.
     *
     * @param courseCode Šifra predmeta.
     * @return {@link Optional} sa predmetom ili prazan ako nije pronađen.
     * @throws IllegalArgumentException Ako je šifra null ili prazna.
     */
    public Optional<Course> getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Šifra predmeta je obavezna.");
        }
        return courseRepository.getCourseByCode(courseCode);
    }

    /**
     * Pretražuje predmete po početnim slovima naziva.
     *
     * @param prefix Tekstualni prefiks za pretragu.
     * @return Lista predmeta čiji naziv počinje sa prefix.
     */
    public ArrayList<Course> findCoursesByNamePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return courseRepository.findCoursesByNamePrefix(prefix);
    }

    /**
     * Interna pomoćna metoda za detaljnu validaciju integriteta podataka predmeta.
     * Provjerava opsege ECTS bodova (1-15) i semestara (1-10).
     *
     * @param course Objekat koji se validira.
     * @param courseCode Šifra koja se provjerava.
     * @throws IllegalArgumentException Ukoliko bilo koji od parametara ne zadovoljava poslovna pravila.
     */
    private void validateCourse(Course course, String courseCode) {
        if (course == null) {
            throw new IllegalArgumentException("Course objekat ne smije biti null.");
        }

        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Šifra predmeta je obavezna.");
        }

        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Naziv predmeta je obavezan.");
        }

        if (course.getEcts() < 1 || course.getEcts() > 15) {
            throw new IllegalArgumentException("ECTS krediti moraju biti u opsegu 1-15.");
        }

        if (course.getSemester() < 1 || course.getSemester() > 10) {
            throw new IllegalArgumentException("Semestar mora biti u opsegu 1-10.");
        }
    }
}