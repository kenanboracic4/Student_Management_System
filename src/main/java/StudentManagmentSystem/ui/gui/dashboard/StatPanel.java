package StudentManagmentSystem.ui.gui.dashboard;

import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.services.StudentService;
import StudentManagmentSystem.services.CourseService;
import StudentManagmentSystem.services.EnrollmentService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel za prikaz globalne statistike sistema.
 * Vizuelno prikazuje broj studenata, broj predmeta i prosječnu ocjenu na nivou cijele institucije.
 */
public class StatPanel extends JPanel {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    /**
     * Konstruktor panela.
     * @param studentService Servis za podatke o studentima.
     * @param courseService Servis za podatke o predmetima.
     * @param enrollmentService Servis za podatke o upisima i ocjenama.
     */
    public StatPanel(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        // Postavljanje prostranih margina za moderan izgled
        setBorder(new EmptyBorder(40, 50, 40, 50));

        initUI();
    }

    /**
     * Inicijalizuje i iscrtava UI komponente.
     * Poziva se pri kreiranju i može se koristiti za osvježavanje prikaza.
     */
    private void initUI() {
        removeAll();

        // --- NASLOV ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.WHITE);
        JLabel title = new JLabel("Statistika Sistema");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.add(title);

        // --- MREŽA SA KARTICAMA ---
        // GridLayout sa jednim redom i tri kolone za simetričan prikaz
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 25, 0));
        cardsGrid.setBackground(Color.WHITE);

        // Prikupljanje podataka putem servisa
        int totalStudents = studentService.getAllStudents().size();
        int totalCourses = courseService.getAllCourses().size();

        // Izračun prosječne ocjene svih položenih ispita
        double averageGrade = calculateSystemAverage();
        String avgDisplay = (averageGrade == 0) ? "0.00" : String.format("%.2f", averageGrade);

        // Dodavanje kartica sa specifičnim akcentnim bojama (Plava, Zelena, Ljubičasta)
        cardsGrid.add(createStatCard("UKUPNO STUDENATA", String.valueOf(totalStudents), new Color(37, 99, 235)));
        cardsGrid.add(createStatCard("AKTIVNIH PREDMETA", String.valueOf(totalCourses), new Color(22, 163, 74)));
        cardsGrid.add(createStatCard("PROSJEČNA OCJENA", avgDisplay, new Color(147, 51, 234)));

        add(header, BorderLayout.NORTH);
        add(cardsGrid, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Izračunava prosječnu ocjenu svih studenata u sistemu.
     * Uzimaju se u obzir samo prolazne ocjene (6-10).
     * @return Prosječna vrijednost ili 0.0 ako nema ocjena.
     */
    private double calculateSystemAverage() {
        List<Enrollment> allEnrollments = enrollmentService.getAllEnrollments();
        if (allEnrollments == null || allEnrollments.isEmpty()) return 0.0;

        double sum = 0;
        int count = 0;

        for (Enrollment e : allEnrollments) {
            // Logika: Računaj samo ako je student položio ispit
            if (e.getGrade() != null && e.getGrade() >= 6) {
                sum += e.getGrade();
                count++;
            }
        }

        return (count > 0) ? sum / count : 0.0;
    }

    /**
     * Fabrička metoda za kreiranje uniformnih statističkih kartica.
     * Koristi BoxLayout za vertikalno poravnanje naslova i vrijednosti.
     */
    private JPanel createStatCard(String title, String val, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblVal.setForeground(accent);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10)); // Razmak između naslova i broja
        card.add(lblVal);

        return card;
    }
}