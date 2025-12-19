package org.example.services;

import org.example.models.Course;
import org.example.models.Enrollment;
import org.example.models.Student;
import org.example.models.StudentReport;
import org.example.repository.implementations.EnrollmentRepository;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentService {

    private final EnrollmentRepository repository;
    private final StudentService studentService; // Nova zavisnost
    private final CourseService courseService;   // Nova zavisnost



    public EnrollmentService(EnrollmentRepository repository,
                             StudentService studentService,
                             CourseService courseService) {
        this.repository = repository;
        this.studentService = studentService;
        this.courseService = courseService;
    }


    public Enrollment registerNewEnrollment(Enrollment enrollment) {


        if (enrollment.getStudentIndexNumber() == null || enrollment.getCourseCode() == null || enrollment.getAcademicYear() == null) {
            throw new IllegalArgumentException("Podaci Indeks, Predmet, Godina su obavezni.");
        }

      // provjera za studenta
        if (studentService.getStudentByIndex(enrollment.getStudentIndexNumber()).isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom '" + enrollment.getStudentIndexNumber() + "' ne postoji u sistemu. Upis nije moguć.");
        }

        // provjera za predmet
        if (courseService.getCourseByCode(enrollment.getCourseCode()).isEmpty()) {
            throw new IllegalArgumentException("Predmet sa šifrom '" + enrollment.getCourseCode() + "' ne postoji u sistemu. Upis nije moguć.");
        }


        //  Provjera Duplikata
        Optional<Enrollment> existing = repository.findByPrimaryKey(
                enrollment.getStudentIndexNumber(),
                enrollment.getCourseCode(),
                enrollment.getAcademicYear()
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("Student je već upisan na ovaj predmet u datoj akademskoj godini (Duplikat PK).");
        }



        return repository.create(enrollment);
    }


    public boolean enterOrUpdateGrade(String studentIndexNumber, String courseCode, String academicYear,
                                      Integer newGrade, String reason) {

        if(!academicYear.contains("/")){
            throw new IllegalArgumentException("Akademska godina mora biti u formatu YYYY/YYYY");
        }
        if (newGrade != null && (newGrade < 5 || newGrade > 10)) {
            throw new IllegalArgumentException("Ocjena mora biti cijeli broj između 5 i 10.");
        }

        Optional<Enrollment> enrollmentOpt = repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);

        if (enrollmentOpt.isEmpty()) {
            throw new IllegalStateException("Upis za datu kombinaciju ne postoji. Ocjena se ne može unijeti/ažurirati.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        String currentDate = LocalDate.now().toString();


        if (enrollment.getGrade() == null) {

            enrollment.setGrade(newGrade);
            enrollment.setGradeDate(currentDate);
            enrollment.setChangeReason(null);
            enrollment.setChangeDate(null);
        } else {


            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Za izmjenu postojeće ocjene mora biti naveden razlog.");
            }

            enrollment.setGrade(newGrade);
            enrollment.setChangeReason(reason);
            enrollment.setChangeDate(currentDate);
        }


        return repository.update(enrollment);
    }


    public boolean deleteEnrollment(String studentIndexNumber, String courseCode, String academicYear) {


        Optional<Enrollment> enrollmentOpt = repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
        if (enrollmentOpt.isPresent() && enrollmentOpt.get().getGrade() != null && enrollmentOpt.get().getGrade() >= 6) {
            throw new IllegalStateException("Nije dozvoljeno brisanje upisa za predmet koji je student već položio (ocjena >= 6).");
        }


        if (!repository.delete(studentIndexNumber, courseCode, academicYear)) {

            throw new IllegalStateException("Upis za brisanje nije pronađen.");
        }
        return true;
    }

    public Optional<Enrollment> getEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        return repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
    }

    public List<Enrollment> getStudentEnrollmentsInYear(String studentIndexNumber, String academicYear) {
        return repository.findByStudentAndYear(studentIndexNumber, academicYear);
    }

    public List<Enrollment> getAllEnrollments(){
        return  repository.findAll();
    }


    public StudentReport generateStudentReport(String indexNumber) {

        Optional<Student> studentOpt = studentService.getStudentByIndex(indexNumber);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom '" + indexNumber + "' nije pronađen.");
        }
        Student student = studentOpt.get();


        List<Enrollment> allEnrollments = repository.findAll().stream()
                .filter(e -> e.getStudentIndexNumber().equals(indexNumber))
                .toList();

        int totalEcts = 0;
        int gradeSum = 0;
        int gradedCount = 0;

        for (Enrollment e : allEnrollments) {
            Optional<Course> courseOpt = courseService.getCourseByCode(e.getCourseCode());

            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();

                if (e.getGrade() != null && e.getGrade() > 5) {
                    totalEcts += course.getEcts();
                    gradeSum += e.getGrade();
                    gradedCount++;
                }
            }
        }

        double averageGrade = (gradedCount > 0) ? (double) gradeSum / gradedCount : 5.0;

        return new StudentReport(student, allEnrollments, totalEcts, averageGrade);
    }
}