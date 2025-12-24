package StudentManagmentSystem.ui.gui.dashboard;

import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.components.CourseTablePanel;
import StudentManagmentSystem.ui.gui.components.EnrollmentPanel;
import StudentManagmentSystem.ui.gui.components.StudentTablePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // --- MODERNA PALETA BOJA ---
    private final Color sidebarColor = new Color(33, 47, 61);    // Deep Midnight
    private final Color activeColor = new Color(52, 152, 219);   // Bright Blue
    private final Color contentBg = Color.WHITE;                 // Čista bijela za radni prostor
    private final Color textColor = new Color(236, 240, 241);    // Off-white za tekst

    private JPanel pnlContent;

    public MainDashboard(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;

        initUI();
    }

    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0 - Kontrolni Panel");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // --- SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setBackground(sidebarColor);
        pnlSidebar.setPreferredSize(new Dimension(280, 800));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));

        // Profil sekcija u sidebaru
        JPanel pnlProfile = new JPanel(new GridBagLayout());
        pnlProfile.setOpaque(false);
        pnlProfile.setBorder(new EmptyBorder(40, 10, 40, 10));

        String ime = ReferentService.getCurrentUser() != null ?
                ReferentService.getCurrentUser().getFirstName() : "Gost";

        JLabel lblUser = new JLabel("<html><center>Prijavljeni referent:<br><b style='font-size:14px; color:white;'>"
                + ime.toUpperCase() + "</b></center></html>");
        lblUser.setForeground(new Color(171, 178, 185));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlProfile.add(lblUser);
        pnlSidebar.add(pnlProfile);

        // Meni dugmići
        pnlSidebar.add(createMenuButton("👤  STUDENTI", e -> showStudentPanel()));
        pnlSidebar.add(createMenuButton("📚  PREDMETI", e -> showCoursePanel()));
        pnlSidebar.add(createMenuButton("📝  UPISI I OCJENE", e -> showEnrollmentPanel()));
        pnlSidebar.add(Box.createVerticalGlue()); // Gura odjavu na dno
        pnlSidebar.add(createMenuButton("🚪  ODJAVI SE", e -> handleLogout()));

        // --- GLAVNI RADNI PANEL ---
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(contentBg);

        // Gornji bar za naslov trenutne sekcije
        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);
        pnlTopBar.setPreferredSize(new Dimension(0, 60));
        pnlTopBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 232, 232)));

        JLabel lblSectionTitle = new JLabel("  Kontrolna Tabla");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSectionTitle.setForeground(sidebarColor);
        pnlTopBar.add(lblSectionTitle, BorderLayout.WEST);

        // Sastavljanje
        JPanel pnlRightSide = new JPanel(new BorderLayout());
        pnlRightSide.add(pnlTopBar, BorderLayout.NORTH);
        pnlRightSide.add(pnlContent, BorderLayout.CENTER);

        mainPanel.add(pnlSidebar, BorderLayout.WEST);
        mainPanel.add(pnlRightSide, BorderLayout.CENTER);
        add(mainPanel);

        showWelcomeScreen();
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(280, 50));
        btn.setPreferredSize(new Dimension(280, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setBackground(sidebarColor);
        btn.setForeground(textColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(44, 62, 80));
                btn.setForeground(activeColor);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(sidebarColor);
                btn.setForeground(textColor);
            }
        });

        btn.addActionListener(action);
        return btn;
    }

    // --- FUNKCIJE ZA PRIKAZ PANELA ---

    private void showWelcomeScreen() {
        updateContent(new JLabel("<html><center><h1>Dobrodošli nazad!</h1><p>Izaberite opciju iz menija za upravljanje bazom podataka.</p></center></html>", SwingConstants.CENTER));
    }

    private void showStudentPanel() {
        // Ovdje ćemo u sljedećem koraku staviti:
        StudentTablePanel studentPanel = new StudentTablePanel(studentService);
        updateContent(studentPanel);
    }

    private void showCoursePanel() {
        CourseTablePanel coursePanel = new CourseTablePanel(courseService);
        updateContent(coursePanel);
    }
    private void showEnrollmentPanel() {
        updateContent(new EnrollmentPanel(studentService, courseService));
    }



    // Pomoćna metoda za zamjenu panela
    private void updateContent(Component c) {
        pnlContent.removeAll();
        pnlContent.add(c, BorderLayout.CENTER);
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite napustiti sistem?", "Odjava", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Ovdje bi se vratio na Login prozor
        }
    }
}