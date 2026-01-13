package StudentManagmentSystem;

import StudentManagmentSystem.repository.implementations.*;
import StudentManagmentSystem.repository.interfaces.*;
import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.ConsoleUI;
import javax.swing.*;
import StudentManagmentSystem.ui.gui.auth.LoginFrame;

/**
 * Glavna klasa aplikacije (Bootstrapper).
 * Odgovorna je za podizanje sistema, inicijalizaciju baze podataka,
 * povezivanje svih slojeva (Repository -> Service -> UI) i pokretanje korisničkog interfejsa.
 */
public class Main {

    /**
     * Glavna metoda koja pokreće izvršavanje programa.
     * * @param args Argumenti komandne linije (nisu u upotrebi).
     */
    public static void main(String[] args) {

        // 1. Inicijalizacija baze podataka (Kreiranje tabela ako ne postoje)
        try {
            DbConnection.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("Inicijalizacija baze nije uspjela: " + e.getMessage());
            System.exit(1);
        }

        // 2. Instanciranje Repozitorijuma
        // Koristimo labavo vezivanje (Loose Coupling) preko interfejsa
        StudentInterface studentRepo = new StudentRepository();
        CourseInterface courseRepo = new CourseRepository();
        EnrollmentInterface enrollmentRepo = new EnrollmentRepository();
        ReferentInterface referentRepo = new ReferentRepository();

        // 3. Instanciranje Servisa
        // Ovdje se vrši "Dependency Injection" - servisi dobijaju svoje repozitorijume
        StudentService studentService = new StudentService(studentRepo);
        CourseService courseService = new CourseService(courseRepo);

        // EnrollmentService zahtijeva studentService i courseService radi validacije podataka
        EnrollmentService enrollmentService = new EnrollmentService(
                (EnrollmentRepository) enrollmentRepo,
                studentService,
                courseService
        );

        ReferentService referentService = new ReferentService(referentRepo);

//        // 4. Pokretanje UI-a
//        // Trenutno je aktiviran ConsoleUI (konzolni režim)
//        ConsoleUI ui = new ConsoleUI(
//                studentService,
//                courseService,
//                enrollmentService,
//                referentService
//        );
//        ui.start();

        /* * OPCIONALNO: Pokretanje Grafičkog interfejsa (GUI)
         * Ako želiš GUI umjesto konzole, otkomentariši blok ispod,
         * a zakomentariši gornji dio za ConsoleUI.
         */

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LoginFrame login = new LoginFrame(studentService, courseService, enrollmentService, referentService);
                login.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}