package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.models.StudentReport;
import StudentManagmentSystem.repository.implementations.EnrollmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servis zadužen za upravljanje životnim ciklusom upisa studenata na predmete.
 * Sadrži kompleksnu poslovnu logiku za validaciju ocjena, sprečavanje duplih upisa
 * i generisanje zbirnih izvještaja (kartona) studenata.
 */
public class EnrollmentService {

    private final EnrollmentRepository repository;
    private final StudentService studentService;
    private final CourseService courseService;

    /**
     * Konstruktor koji inicijalizuje servis sa potrebnim repozitorijumom i zavisnim servisima.
     *
     * @param repository Repozitorijum za rad sa upisima.
     * @param studentService Servis za provjeru postojanja studenata.
     * @param courseService Servis za provjeru postojanja predmeta.
     */
    public EnrollmentService(EnrollmentRepository repository,
                             StudentService studentService,
                             CourseService courseService) {
        this.repository = repository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    /**
     * Registruje novi upis studenta na predmet.
     * Provjerava postojanje studenta i kursa, te implementira stroga pravila integriteta:
     * - Student ne može ponovo upisati predmet koji je već položio (ocjena 6-10).
     * - Student ne može biti upisan na isti predmet dva puta u istoj akademskoj godini.
     *
     * @param enrollment Objekat koji sadrži podatke o upisu.
     * @return Spašeni objekat {@link Enrollment}.
     * @throws IllegalArgumentException Ako podaci nedostaju ili student/predmet ne postoje.
     * @throws IllegalStateException Ako student već ima položen predmet ili je već upisan u istoj godini.
     */
    public Enrollment registerNewEnrollment(Enrollment enrollment) {
        if (enrollment.getStudentIndexNumber() == null || enrollment.getCourseCode() == null || enrollment.getAcademicYear() == null) {
            throw new IllegalArgumentException("Podaci Indeks, Predmet i Akademska godina su obavezni.");
        }

        if (enrollment.getAddedByReferentId() == null || enrollment.getAddedByReferentId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID referenta koji vrši upis je obavezan.");
        }

        if (studentService.getStudentByIndex(enrollment.getStudentIndexNumber()).isEmpty()) {
            throw new IllegalArgumentException("Student sa indeksom '" + enrollment.getStudentIndexNumber() + "' ne postoji.");
        }

        if (courseService.getCourseByCode(enrollment.getCourseCode()).isEmpty()) {
            throw new IllegalArgumentException("Predmet sa šifrom '" + enrollment.getCourseCode() + "' ne postoji.");
        }

        List<Enrollment> allStudentEnrollments = repository.findAll().stream()
                .filter(e -> e.getStudentIndexNumber().equals(enrollment.getStudentIndexNumber()))
                .toList();

        for (Enrollment existing : allStudentEnrollments) {
            if (existing.getCourseCode().equals(enrollment.getCourseCode()) &&
                    existing.getGrade() != null && existing.getGrade() >= 6) {
                throw new IllegalStateException("Student je već položio predmet '" + enrollment.getCourseCode() + "'. Nije dozvoljen ponovni upis.");
            }

            if (existing.getCourseCode().equals(enrollment.getCourseCode()) &&
                    existing.getAcademicYear().equals(enrollment.getAcademicYear())) {
                throw new IllegalStateException("Student je već upisan na ovaj predmet u akademskoj godini " + enrollment.getAcademicYear());
            }
        }

        return repository.create(enrollment);
    }

    /**
     * Unosi novu ili ažurira postojeću ocjenu studenta.
     * Ukoliko se vrši izmjena već postojeće ocjene, sistem zahtijeva unos razloga izmjene radi revizije.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param courseCode Šifra predmeta.
     * @param academicYear Akademska godina upisa.
     * @param newGrade Nova ocjena (raspon 5-10).
     * @param reason Razlog izmjene (obavezan samo kod promjene postojeće ocjene).
     * @param referentId ID referenta koji vrši promjenu.
     * @return {@code true} ako je ažuriranje uspješno.
     * @throws IllegalArgumentException Ako je ocjena van opsega ili nedostaje razlog izmjene.
     * @throws IllegalStateException Ako upis nije pronađen u bazi.
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

        if (enrollment.getGrade() == null || enrollment.getGrade() == 0) {
            enrollment.setGrade(newGrade);
            enrollment.setGradeDate(currentDate);
            enrollment.setModifiedByReferentId(referentId);
        } else {
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

    /**
     * Briše zapis o upisu ukoliko predmet nije položen.
     * Brisanje položenih predmeta (ocjena >= 6) je onemogućeno radi očuvanja integriteta dosijea.
     *
     * @return {@code true} ako je brisanje izvršeno.
     * @throws IllegalStateException Ako je predmet položen ili upis nije pronađen.
     */
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

    /**
     * Dobavlja specifičan upis na osnovu ključnih parametara.
     */
    public Optional<Enrollment> getEnrollment(String studentIndexNumber, String courseCode, String academicYear) {
        return repository.findByPrimaryKey(studentIndexNumber, courseCode, academicYear);
    }

    /**
     * Vraća sve upise u sistemu.
     */
    public List<Enrollment> getAllEnrollments(){
        return repository.findAll();
    }

    /**
     * Generiše akademski izvještaj (karton) za studenta.
     * Izračunava ukupne ECTS bodove i prosječnu ocjenu na osnovu položenih predmeta (ocjena > 5).
     *
     * @param indexNumber Broj indeksa studenta za kojeg se generiše izvještaj.
     * @return {@link StudentReport} objekat sa sumiranim podacima.
     * @throws IllegalArgumentException Ako student nije pronađen u sistemu.
     */
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