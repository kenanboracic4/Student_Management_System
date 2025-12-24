package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentInterface {

    /**
     * Kreira novi zapis o upisu.
     * Implementacija mora spasiti 'addedByReferentId'.
     */
    Enrollment create(Enrollment enrollment);

    /**
     * Pronalazi specifičan upis na osnovu kompozitnog ključa.
     */
    Optional<Enrollment> findByPrimaryKey(String studentIndexNumber, String courseCode, String academicYear);

    /**
     * Vraća listu svih upisa za konkretnog studenta u određenoj akademskoj godini.
     */
    List<Enrollment> findByStudentAndYear(String studentIndexNumber, String academicYear);

    /**
     * Ažurira postojeći upis (ocjenu, datum ili razlog izmjene).
     * Implementacija mora osigurati upisivanje 'modifiedByReferentId' i 'changeDate'.
     */
    boolean update(Enrollment enrollment);

    /**
     * Briše zapis o upisu iz baze.
     */
    boolean delete(String studentIndexNumber, String courseCode, String academicYear);

    /**
     * Vraća sve upise u sistemu.
     */
    List<Enrollment> findAll();
}