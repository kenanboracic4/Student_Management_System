package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.services.StudentService;
import StudentManagmentSystem.services.CourseService;
import StudentManagmentSystem.services.EnrollmentService;
import StudentManagmentSystem.services.ReferentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentPanel extends JPanel {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService; // DODANO

    private JComboBox<String> comboStudents;
    private JComboBox<String> comboCourses;
    private JComboBox<Integer> comboGrades;
    private JButton btnSubmit;

    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_ACCENT = new Color(37, 99, 235);
    private final Color COLOR_TEXT = new Color(15, 23, 42);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    // Konstruktor sada prima i EnrollmentService
    public EnrollmentPanel(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;

        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(40, 50, 40, 50));

        initHeader();
        initForm();
    }

    private void initHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(0, 0, 40, 0));

        JLabel title = new JLabel("Upis Ocjena i Predmeta");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(COLOR_TEXT);

        JLabel sub = new JLabel("Povežite studenta sa položenim predmetom i unesite ocjenu.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(100, 116, 139));

        header.add(title);
        header.add(sub);
        add(header, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formGrid = new JPanel(new GridLayout(3, 1, 0, 25));
        formGrid.setBackground(COLOR_BG);

        comboStudents = new JComboBox<>();
        loadStudents();
        formGrid.add(createFieldWrapper("IZABERITE STUDENTA", comboStudents));

        comboCourses = new JComboBox<>();
        loadCourses();
        formGrid.add(createFieldWrapper("IZABERITE PREDMET", comboCourses));

        Integer[] grades = {6, 7, 8, 9, 10};
        comboGrades = new JComboBox<>(grades);
        formGrid.add(createFieldWrapper("OCJENA", comboGrades));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(30, 0, 0, 0));

        btnSubmit = new JButton("EVIDENTIRAJ OCJENU U BAZU");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(COLOR_ACCENT);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(280, 55));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSubmit.addActionListener(e -> handleEnrollment());

        footer.add(btnSubmit, BorderLayout.WEST);
        add(formGrid, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createFieldWrapper(String labelText, JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(COLOR_BG);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 116, 139));
        component.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        component.setPreferredSize(new Dimension(0, 50));
        if (component instanceof JComboBox) {
            component.setBackground(Color.WHITE);
            ((JComboBox<?>) component).setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 2));
        }
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private void loadStudents() {
        studentService.getAllStudents().forEach(s ->
                comboStudents.addItem(s.getIndexNumber() + " - " + s.getFirstName() + " " + s.getLastName()));
    }

    private void loadCourses() {
        courseService.getAllCourses().forEach(c ->
                comboCourses.addItem(c.getCourseCode() + " - " + c.getName()));
    }

    private void handleEnrollment() {
        try {
            String studentData = (String) comboStudents.getSelectedItem();
            String courseData = (String) comboCourses.getSelectedItem();
            Integer grade = (Integer) comboGrades.getSelectedItem();

            if (studentData == null || courseData == null) return;

            String studentId = studentData.split(" - ")[0];
            String courseCode = courseData.split(" - ")[0];

            // Trenutna akademska godina (možeš dodati i polje u GUI za ovo)
            String academicYear = "2024/2025";

            // Dobavljanje referenta iz sesije (tvoja ReferentService klasa)
            String refId = (ReferentService.getCurrentUser() != null)
                    ? ReferentService.getCurrentUser().getReferentId() : "SISTEM";

            // 1. Kreiramo Enrollment objekat
            Enrollment enrollment = new Enrollment(
                    studentId,
                    courseCode,
                    academicYear,
                    grade,
                    LocalDate.now().toString(),
                    null, null,
                    refId, // referentKojiJeDodao
                    null
            );

            // 2. Pozivamo servis za upis u bazu
            enrollmentService.registerNewEnrollment(enrollment);

            JOptionPane.showMessageDialog(this, "Ocjena uspješno upisana u bazu podataka!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage(), "Greška pri upisu", JOptionPane.ERROR_MESSAGE);
        }
    }
}