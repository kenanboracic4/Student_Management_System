package org.example;

import org.example.DbConnection;
import org.example.repository.implementations.CourseRepository;
import org.example.repository.implementations.EnrollmentRepository;
import org.example.repository.implementations.StudentRepository;
import org.example.repository.interfaces.CourseInterface;
import org.example.repository.interfaces.EnrollmentInterface;
import org.example.repository.interfaces.StudentInterface;
import org.example.services.CourseService;
import org.example.services.EnrollmentService;
import org.example.services.StudentService;
import org.example.ui.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        try {
            DbConnection.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("FATALNA GREŠKA: Inicijalizacija baze nije uspjela.");
            System.exit(1);
        }


        StudentInterface studentRepo = new StudentRepository();
        CourseInterface courseRepo = new CourseRepository();
        EnrollmentInterface enrollmentRepo = new EnrollmentRepository();


        StudentService studentService = new StudentService(studentRepo);
        CourseService courseService = new CourseService(courseRepo);
        EnrollmentService enrollmentService = new EnrollmentService(
                (EnrollmentRepository) enrollmentRepo,
                studentService,
                courseService
        );


        ConsoleUI ui = new ConsoleUI(studentService, courseService, enrollmentService);
        ui.start();
    }
}
