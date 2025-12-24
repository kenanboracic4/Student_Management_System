package StudentManagmentSystem.ui;

import StudentManagmentSystem.models.*;
import StudentManagmentSystem.services.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService; // NOVO
    private final Scanner scanner;

    public ConsoleUI(StudentService studentService, CourseService courseService,
                     EnrollmentService enrollmentService, ReferentService referentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.referentService = referentService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n========================================");
            System.out.println("   SISTEM STUDENTSKA SLUŽBA (v2.0)");
            System.out.println("========================================");
            System.out.println("1. PRIJAVA REFERENTA (Administracija)");
            System.out.println("2. REGISTRACIJA NOVOG REFERENTA");
            System.out.println("3. PRIJAVA STUDENTA (Pregled ocjena)");
            System.out.println("0. Izlaz");
            System.out.print("Izbor: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleReferentLogin();
                case "2" -> handleReferentRegistration();
                case "3" -> handleStudentLogin();
                case "0" -> exit = true;
                default -> System.out.println("Greška: Nepoznata opcija.");
            }
        }
    }

    // --- AUTENTIFIKACIJA ---

    private void handleReferentLogin() {
        System.out.print("ID Referenta: ");
        String id = scanner.nextLine();
        System.out.print("Lozinka: ");
        String pass = scanner.nextLine();

        if (referentService.login(id, pass)) {
            System.out.println("Sistem: Uspješna prijava. Dobrodošli, " + ReferentService.getCurrentUser().getFirstName());
            showReferentMenu();
        } else {
            System.out.println("Sistem: Pogrešan ID ili lozinka!");
        }
    }

    private void handleReferentRegistration() {
        System.out.print("Željeni ID: ");
        String id = scanner.nextLine();
        System.out.print("Lozinka: ");
        String pass = scanner.nextLine();
        System.out.print("Ime: ");
        String name = scanner.nextLine();
        System.out.print("Prezime: ");
        String surname = scanner.nextLine();

        try {
            referentService.registerReferent(new Referent(id, pass, name, surname));
            System.out.println("Sistem: Referent uspješno registrovan. Možete se prijaviti.");
        } catch (Exception e) {
            System.out.println("GREŠKA: " + e.getMessage());
        }
    }

    private void handleStudentLogin() {
        System.out.print("Unesite broj indeksa: ");
        String index = scanner.nextLine();
        System.out.print("Unesite lozinku: ");
        String pass = scanner.nextLine();

        var studentOpt = studentService.getStudentByIndex(index);
        if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(pass)) {
            System.out.println("\n--- VAŠI PODACI ---");
            handleStudentReport(index);
        } else {
            System.out.println("Sistem: Neuspješna prijava studenta.");
        }
    }

    // --- MENIJI ---

    private void showReferentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- RADNI PANEL (Referent: " + ReferentService.getCurrentUser().getReferentId() + ") ---");
            System.out.println("1. Registruj studenta      5. Unos/Izmjena ocjene");
            System.out.println("2. Dodaj novi predmet     6. Pregled svih studenata");
            System.out.println("3. Pretraga studenata     7. Pregled svih predmeta");
            System.out.println("4. Upis na predmet        8. Pregled svih upisa");
            System.out.println("9. Karton studenta        0. Odjava");
            System.out.print("Izbor: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> handleAddStudent();
                    case "2" -> handleAddCourse();
                    case "3" -> handleSearchStudents();
                    case "4" -> handleEnrollment();
                    case "5" -> handleGrading();
                    case "6" -> { System.out.println("\nLISTA STUDENATA:"); studentService.getAllStudents().forEach(System.out::println); }
                    case "7" -> { System.out.println("\nLISTA PREDMETA:"); courseService.getAllCourses().forEach(System.out::println); }
                    case "8" -> { System.out.println("\nSVI UPISI U SISTEMU:"); enrollmentService.getAllEnrollments().forEach(System.out::println); }
                    case "9" -> {
                        System.out.print("Indeks: ");
                        handleStudentReport(scanner.nextLine());
                    }
                    case "0" -> { referentService.logout(); back = true; }
                    default -> System.out.println("Sistem: Nepostojeća opcija.");
                }
            } catch (Exception e) {
                System.out.println("GREŠKA: " + e.getMessage());
            }
        }
    }

    // --- POMOĆNE METODE ZA RAD ---

    private void handleAddStudent() {
        System.out.print("Broj indeksa: "); String index = scanner.nextLine();
        System.out.print("Lozinka za studenta: "); String pass = scanner.nextLine();
        System.out.print("Ime: "); String fName = scanner.nextLine();
        System.out.print("Prezime: "); String lName = scanner.nextLine();
        System.out.print("Program: "); String prog = scanner.nextLine();
        System.out.print("Godina upisa: "); int year = Integer.parseInt(scanner.nextLine());

        // Proslijeđujemo ID ulogovanog referenta
        Student s = new Student(index, pass, fName, lName, prog, year, ReferentService.getCurrentUser().getReferentId());
        studentService.addStudent(s);
        System.out.println("Sistem: Student registrovan.");
    }

    private void handleAddCourse() {
        System.out.print("Šifra: "); String code = scanner.nextLine();
        System.out.print("Naziv: "); String name = scanner.nextLine();
        System.out.print("ECTS: "); int ects = Integer.parseInt(scanner.nextLine());
        System.out.print("Semestar: "); int sem = Integer.parseInt(scanner.nextLine());

        Course c = new Course(code, name, ects, sem, ReferentService.getCurrentUser().getReferentId());
        courseService.addCourse(c);
        System.out.println("Sistem: Predmet dodan.");
    }

    private void handleEnrollment() {
        System.out.print("Indeks: "); String idx = scanner.nextLine();
        System.out.print("Šifra predmeta: "); String code = scanner.nextLine();
        System.out.print("Akademska godina: "); String yr = scanner.nextLine();

        Enrollment e = new Enrollment(idx, code, yr, ReferentService.getCurrentUser().getReferentId());
        enrollmentService.registerNewEnrollment(e);
        System.out.println("Sistem: Student upisan na predmet.");
    }

    private void handleGrading() {
        System.out.print("Indeks: "); String idx = scanner.nextLine();
        System.out.print("Šifra predmeta: "); String code = scanner.nextLine();
        System.out.print("Akademska godina: "); String yr = scanner.nextLine();
        System.out.print("Ocjena: "); int grade = Integer.parseInt(scanner.nextLine());
        System.out.print("Razlog (ako je izmjena): "); String reason = scanner.nextLine();

        enrollmentService.enterOrUpdateGrade(idx, code, yr, grade, reason, ReferentService.getCurrentUser().getReferentId());
        System.out.println("Sistem: Ocjena procesuirana.");
    }

    private void handleStudentReport(String index) {
        var report = enrollmentService.generateStudentReport(index);
        System.out.println(report.toString());
    }

    private void handleSearchStudents() {
        System.out.print("Početak prezimena: ");
        String prefix = scanner.nextLine();
        studentService.findStudentsByLastNamePrefix(prefix).forEach(System.out::println);
    }
}