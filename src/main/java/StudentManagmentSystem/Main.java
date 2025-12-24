package StudentManagmentSystem;

import StudentManagmentSystem.repository.implementations.*;
import StudentManagmentSystem.repository.interfaces.*;
import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.ConsoleUI;

import javax.swing.*;

import StudentManagmentSystem.ui.gui.auth.LoginFrame;

public class Main {

    public static void main(String[] args) {

        // 1. Inicijalizacija baze podataka
        try {
            DbConnection.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("Inicijalizacija baze nije uspjela: " + e.getMessage());
            System.exit(1);
        }

        // 2. Instanciranje Repozitorijuma (preko interfejsa)
        StudentInterface studentRepo = new StudentRepository();
        CourseInterface courseRepo = new CourseRepository();
        EnrollmentInterface enrollmentRepo = new EnrollmentRepository();
        ReferentInterface referentRepo = new ReferentRepository(); // NOVO

        // 3. Instanciranje Servisa
        StudentService studentService = new StudentService(studentRepo);
        CourseService courseService = new CourseService(courseRepo);

        // EnrollmentService sada ispravno prima interfejs umjesto konkretne klase
        EnrollmentService enrollmentService = new EnrollmentService(
                (EnrollmentRepository) enrollmentRepo,
                studentService,
                courseService
        );

        // Servis za referente koji upravlja prijavom i audit podacima
        ReferentService referentService = new ReferentService(referentRepo); // NOVO

        // 4. Pokretanje UI-a sa SVA ČETIRI potrebna servisa
        // Ovo rješava "actual and formal argument lists differ in length" grešku
//        ConsoleUI ui = new ConsoleUI(
//                studentService,
//                courseService,
//                enrollmentService,
//                referentService
//        );
//        ui.start();
        SwingUtilities.invokeLater(() -> {
            try {
                // Postavljamo "Look and Feel" da aplikacija izgleda kao tvoj operativni sistem
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Kreiramo Login prozor i prosljeđujemo mu servise
                LoginFrame login = new LoginFrame(studentService, courseService, enrollmentService, referentService);
                login.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}