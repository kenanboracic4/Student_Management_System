package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Course;
import java.util.ArrayList;
import java.util.Optional;

public interface CourseInterface {

    /**
     * Dodaje novi predmet.
     * Model 'course' bi trebao sadržati 'addedByReferentId'.
     */
    void addCourse(Course course);

    /**
     * Ažurira postojeći predmet.
     * @param course Model sa novim podacima.
     * @param courseCode Originalni kod predmeta (u slučaju da se šifra mijenja).
     * @param referentId ID referenta koji vrši izmjenu (za audit log/datumAzuriranja).
     */
    boolean updateCourse(Course course, String courseCode, String referentId);

    boolean deleteCourse(String courseCode);

    ArrayList<Course> getAllCourses();

    Optional<Course> getCourseByCode(String courseCode);

    ArrayList<Course> findCoursesByNamePrefix(String prefix);

    boolean hasEnrollments(String courseCode);

    /**
     * Pronalazi sve predmete koje je dodao određeni referent.
     */
    ArrayList<Course> getCoursesByReferent(String referentId);
}