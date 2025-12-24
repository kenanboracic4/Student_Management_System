package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.models.StudentReport;
import StudentManagmentSystem.repository.implementations.EnrollmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EnrollmentService {

    private final EnrollmentRepository repository;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository repository,
                             StudentService studentService,
                             CourseService courseService) {
        this.repository = repository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public Enrollment registerNewEnrollment(Enrollment enrollment) {
        // Osnovna validacija
        if (enrollment.getStudentIndexNumber() == null || enrollment.getCourseCode() == null || enrollment.getAcademicYear() == null) {
            throw new IllegalArgumentException("Podaci Indeks, Predmet i Akademska godina su obavezni.");
        }

        // NOVO: Provjera referenta koji vrši upis
        if (enrollment.getAddedByReferentId() == null || enrollment.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta koji vrši upis je obavezan.");
        }

        // Provjera postojanja studenta i predmeta
        if (studentService.getStudentByIndex(enrollment.getStudentIndexNumber()).isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom '" + enrollment.getStudentIndexNumber() + "' ne postoji.");
        }

        if (courseService.getCourseByCode(enrollment.getCourseCode()).isEmpty()) {
            throw new IllegalArgumentException("Predmet sa šifrom '" + enrollment.getCourseCode() + "' ne postoji.");
        }

        // Provjera duplikata
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

    /**
     * @param referentId ID referenta koji unosi ili mijenja ocjenu
     */
    public boolean enterOrUpdateGrade(String studentIndexNumber, String courseCode, String academicYear,
                                      Integer newGrade, String reason, String referentId) {

        if (referentId == null || referentId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta je obavezan za unos/izmjenu ocjene.");
        }

        if (newGrade != null && (newGrade < 5 || newGrade > 10)) {
            throw new IllegalArgumentException("Ocjena mora biti između 5 i 10.");
        }

        Optional<Enrollment> enrollmentOpt = repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
        if (enrollmentOpt.isEmpty()) {
            throw new IllegalStateException("Upis ne postoji. Ocjena se ne može unijeti.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        String currentDate = LocalDate.now().toString();

        // Slučaj 1: Prvi unos ocjene
        if (enrollment.getGrade() == null || enrollment.getGrade() == 0) {
            enrollment.setGrade(newGrade);
            enrollment.setGradeDate(currentDate);
            // Referent koji je dodao ocjenu se ovdje bilježi kao onaj koji je "izvršio izmjenu" u odnosu na prazan upis
            enrollment.setModifiedByReferentId(referentId);
        }
        // Slučaj 2: Izmjena postojeće ocjene
        else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Za izmjenu postojeće ocjene mora biti naveden razlog.");
            }
            enrollment.setGrade(newGrade);
            enrollment.setChangeReason(reason);
            enrollment.setChangeDate(currentDate);
            enrollment.setModifiedByReferentId(referentId);
        }

        return repository.update(enrollment);
    }

    public boolean deleteEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        Optional<Enrollment> enrollmentOpt = repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);

        if (enrollmentOpt.isPresent() && enrollmentOpt.get().getGrade() != null && enrollmentOpt.get().getGrade() >= 6) {
            throw new IllegalStateException("Nije dozvoljeno brisanje položenog predmeta (ocjena >= 6).");
        }

        if (!repository.delete(studentIndexNumber, courseCode, academicYear)) {
            throw new IllegalStateException("Upis za brisanje nije pronađen.");
        }
        return true;
    }

    // --- Ostale metode ostaju slične, ali koriste ažurirani Repository ---

    public Optional<Enrollment> getEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        return repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
    }

    public List<Enrollment> getAllEnrollments(){
        return repository.findAll();
    }

    public StudentReport generateStudentReport(String indexNumber) {
        Optional<Student> studentOpt = studentService.getStudentByIndex(indexNumber);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom '" + indexNumber + "' nije pronađen.");
        }

        List<Enrollment> studentEnrollments = repository.findAll().stream()
                .filter(e -> e.getStudentIndexNumber().equals(indexNumber))
                .toList();

        int totalEcts = 0;
        int gradeSum = 0;
        int gradedCount = 0;

        for (Enrollment e : studentEnrollments) {
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
        return new StudentReport(studentOpt.get(), studentEnrollments, totalEcts, averageGrade);
    }
}