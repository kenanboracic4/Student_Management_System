package org.example.services;
import org.example.models.Enrollment;
import org.example.repository.implementations.EnrollmentRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EnrollmentService {

    private final EnrollmentRepository repository;


    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }


    public Enrollment registerNewEnrollment(Enrollment enrollment) {

        if (enrollment.getStudentIndexNumber() == null || enrollment.getCourseCode() == null || enrollment.getAcademicYear() == null) {
            throw new IllegalArgumentException("Primarni ključni podaci (Indeks, Predmet, Godina) su obavezni.");
        }


        Optional<Enrollment> existing = repository.findByPrimaryKey(
                enrollment.getStudentIndexNumber(),
                enrollment.getCourseCode(),
                enrollment.getAcademicYear()
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("Student je već upisan na ovaj predmet u datoj akademskoj godini.");
        }


        return repository.create(enrollment);
    }


    public boolean enterOrUpdateGrade(String studentIndexNumber, String courseCode, String academicYear,
                                      Integer newGrade, String reason) {


        if (newGrade != null && (newGrade < 5 || newGrade > 10)) {
            throw new IllegalArgumentException("Ocjena mora biti između 5 i 10.");
        }

        // Dohvatanje postojeceg upsia iz baze
        Optional<Enrollment> enrollmentOpt = repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);

        if (enrollmentOpt.isEmpty()) {
            throw new IllegalStateException("Upis za datu kombinaciju ne postoji.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        String currentDate = LocalDate.now().toString();


        if (enrollment.getGrade() == null) {  // Prvi unos ocjene

            enrollment.setGrade(newGrade);
            enrollment.setGradeDate(currentDate);
            enrollment.setChangeReason(null);
            enrollment.setChangeDate(null);
        } else { // Izmjena postojeće ocjene

            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Za izmjenu ocjene mora biti naveden razlog.");
            }
            enrollment.setGrade(newGrade);
            enrollment.setChangeReason(reason);
            enrollment.setChangeDate(currentDate);
        }

        // azuriranje u bazi
        return repository.update(enrollment);
    }


    public boolean deleteEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        if (!repository.delete(studentIndexNumber, courseCode, academicYear)) {
            throw new IllegalStateException("Upis za brisanje nije pronađen ili brisanje nije uspjelo.");
        }
        return true;
    }

    public Optional<Enrollment> getEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        return repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
    }

    public List<Enrollment> getStudentEnrollmentsInYear(String studentIndexNumber, String academicYear) {
        return repository.findByStudentAndYear(studentIndexNumber, academicYear);
    }
}
