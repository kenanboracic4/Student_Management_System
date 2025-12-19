package org.example.ui;

import org.example.models.Enrollment;
import org.example.models.Student;
import org.example.models.Course;
import org.example.services.CourseService;
import org.example.services.EnrollmentService;
import org.example.services.StudentService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final Scanner scanner;

    public ConsoleUI(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n========================================");
            System.out.println("   SISTEM STUDENTSKA SLUŽBA (v1.0)");
            System.out.println("========================================");
            System.out.println("Prijavite se kao:");
            System.out.println("1. REFERENT (Administracija)");
            System.out.println("2. STUDENT (Pregled ocjena)");
            System.out.println("0. Izlaz iz aplikacije");
            System.out.print("Izbor: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showReferentMenu();
                case "2" -> showStudentMenu();
                case "0" -> exit = true;
                default -> System.out.println("Greška: Nepoznata opcija.");
            }
        }
    }

    private void showReferentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- MENI ZA REFERENTA ---");
            System.out.println("1. Registruj novog studenta");
            System.out.println("2. Pretraga studenata (prefix prezimena)");
            System.out.println("3. Dodaj novi predmet");
            System.out.println("4. Upis studenta na predmet");
            System.out.println("5. Unos ili izmjena ocjene");
            System.out.println("6. Karton studenta (Generisanje izvještaja)");
            System.out.println("0. Odjava");
            System.out.print("Izbor: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> handleAddStudent();
                    case "2" -> handleSearchStudents();
                    case "3" -> handleAddCourse();
                    case "4" -> handleEnrollment();
                    case "5" -> handleGrading();
                    case "6" -> handleStudentReport();
                    case "0" -> back = true;
                    default -> System.out.println("Sistem: Opcija ne postoji.");
                }
            } catch (Exception e) {
                System.out.println("GREŠKA: " + e.getMessage());
            }
        }
    }

    private void showStudentMenu() {
        System.out.print("\nPrijava studenta (Unesite broj indeksa): ");
        String index = scanner.nextLine();
        try {
            var report = enrollmentService.generateStudentReport(index);
            System.out.println(report.toString());
        } catch (Exception e) {
            System.out.println("Greška pri dohvatu podataka: " + e.getMessage());
        }
    }

    private void handleAddStudent() {
        System.out.print("Broj indeksa (unikatan): ");
        String index = scanner.nextLine();
        System.out.print("Ime: ");
        String firstName = scanner.nextLine();
        System.out.print("Prezime: ");
        String lastName = scanner.nextLine();
        System.out.print("Studijski program: ");
        String program = scanner.nextLine();
        System.out.print("Godina upisa (2020-2050): ");
        int year = Integer.parseInt(scanner.nextLine());

        Student s = new Student(index, firstName, lastName, program, year);
        studentService.addStudent(s);
        System.out.println("Sistem: Student uspješno registrovan.");
    }

    private void handleSearchStudents() {
        System.out.print("Unesite početak prezimena za pretragu: ");
        String prefix = scanner.nextLine();
        // Pretpostavka da je metoda findByLastNamePrefix implementirana u StudentService
        List<Student> results = studentService.findStudentsByLastNamePrefix(prefix);
        if (results.isEmpty()) {
            System.out.println("Nema rezultata za uneseni prefix.");
        } else {
            System.out.println("\nPronađeni studenti:");
            results.forEach(System.out::println);
        }
    }

    private void handleAddCourse() {
        System.out.print("Šifra predmeta: ");
        String code = scanner.nextLine();
        System.out.print("Naziv predmeta: ");
        String name = scanner.nextLine();
        System.out.print("ECTS bodovi (1-15): ");
        int ects = Integer.parseInt(scanner.nextLine());
        System.out.print("Semestar (1-10): ");
        int semester = Integer.parseInt(scanner.nextLine());

        Course c = new Course(code, name, ects, semester);
        courseService.addCourse(c, code);
        System.out.println("Sistem: Predmet uspješno dodan.");
    }

    private void handleEnrollment() {
        System.out.print("Indeks studenta: ");
        String index = scanner.nextLine();
        System.out.print("Šifra predmeta: ");
        String code = scanner.nextLine();
        System.out.print("Akademska godina (YYYY/YY): ");
        String year = scanner.nextLine();

        Enrollment e = new Enrollment(index, code, year);
        enrollmentService.registerNewEnrollment(e);
        System.out.println("Sistem: Upis uspješno izvršen.");
    }

    private void handleGrading() {
        System.out.print("Indeks studenta: ");
        String index = scanner.nextLine();
        System.out.print("Šifra predmeta: ");
        String code = scanner.nextLine();
        System.out.print("Akademska godina: ");
        String year = scanner.nextLine();
        System.out.print("Ocjena (5-10): ");
        int grade = Integer.parseInt(scanner.nextLine());
        System.out.print("Razlog izmjene (ostaviti prazno ako je prvi unos): ");
        String reason = scanner.nextLine();

        enrollmentService.enterOrUpdateGrade(index, code, year, grade, reason);
        System.out.println("Sistem: Ocjena evidentirana.");
    }

    private void handleStudentReport() {
        System.out.print("Unesite broj indeksa studenta: ");
        String index = scanner.nextLine();
        var report = enrollmentService.generateStudentReport(index);
        System.out.println(report.toString());
    }
}