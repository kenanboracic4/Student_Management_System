package StudentManagmentSystem;

import StudentManagmentSystem.repository.implementations.*;
import StudentManagmentSystem.repository.interfaces.*;
import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.ConsoleUI;

public class ConsoleMain {
    public static void main(String[] args) {
        // 1. Baza
        DbConnection.initializeDatabase();

        // 2. Repozitorijumi
        StudentInterface studentRepo = new StudentRepository();
        CourseInterface courseRepo = new CourseRepository();
        EnrollmentInterface enrollmentRepo = new EnrollmentRepository();
        ReferentInterface referentRepo = new ReferentRepository();

        // 3. Servisi
        StudentService studentService = new StudentService(studentRepo);
        CourseService courseService = new CourseService(courseRepo);
        EnrollmentService enrollmentService = new EnrollmentService(
                (EnrollmentRepository) enrollmentRepo, studentService, courseService);
        ReferentService referentService = new ReferentService(referentRepo);

        // 4. Pokretanje Konzole
        System.out.println("Sistem se pokrece u KONZOLNOM modu...");
        ConsoleUI ui = new ConsoleUI(studentService, courseService, enrollmentService, referentService);
        ui.start();
    }
}