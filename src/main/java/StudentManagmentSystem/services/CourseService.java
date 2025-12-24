package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.repository.interfaces.CourseInterface;

import java.util.ArrayList;
import java.util.Optional;

public class CourseService {

    private final CourseInterface courseRepository;

    public CourseService(CourseInterface courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void addCourse(Course course) {
        // Validacija osnovnih podataka
        validateCourse(course, course.getCourseCode());

        // Provjera da li već postoji
        if (courseRepository.getCourseByCode(course.getCourseCode()).isPresent()) {
            throw new IllegalArgumentException("Predmet sa šifrom " + course.getCourseCode() + " već postoji.");
        }

        // Provjera da li je postavljen referent
        if (course.getAddedByReferentId() == null || course.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mora postojati ID referenta koji dodaje predmet.");
        }

        courseRepository.addCourse(course);
    }

    public void updateCourse(Course course, String courseCode, String referentId) {
        validateCourse(course, courseCode);

        if (referentId == null || referentId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta je obavezan za ažuriranje podataka.");
        }

        if (!courseRepository.updateCourse(course, courseCode, referentId)) {
            throw new IllegalArgumentException("Predmet sa šifrom " + courseCode + " nije pronađen za ažuriranje.");
        }
    }

    public void deleteCourse(String courseCode) {
        // Prvo provjeravamo da li predmet uopšte postoji
        Optional<Course> course = courseRepository.getCourseByCode(courseCode);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Predmet sa šifrom " + courseCode + " ne postoji.");
        }

        // Zatim provjeravamo zavisnosti (upise)
        if (courseRepository.hasEnrollments(courseCode)) {
            throw new IllegalStateException("Nije dozvoljeno brisanje: Predmet ima aktivne upise studenata.");
        }

        // Tek tada brišemo
        courseRepository.deleteCourse(courseCode);
    }

    public ArrayList<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    public Optional<Course> getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Šifra predmeta je obavezna.");
        }
        return courseRepository.getCourseByCode(courseCode);
    }

    public ArrayList<Course> findCoursesByNamePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return courseRepository.findCoursesByNamePrefix(prefix);
    }

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