package StudentManagmentSystem.ui.gui.dashboard;

import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.components.CourseTablePanel;
import StudentManagmentSystem.ui.gui.components.EnrollmentPanel;
import StudentManagmentSystem.ui.gui.components.StudentTablePanel;
import StudentManagmentSystem.ui.gui.components.StudentReportPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {
    private static MainDashboard instance;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // --- MODERNA PALETA BOJA ---
    private final Color sidebarColor = new Color(33, 47, 61);
    private final Color activeColor = new Color(52, 152, 219);
    private final Color contentBg = Color.WHITE;
    private final Color textColor = new Color(236, 240, 241);

    private JPanel pnlContent;
    private JPanel pnlSidebar; // Izvučeno kao polje da bi ga mogli sakriti
    private JLabel lblSectionTitle;

    public MainDashboard(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        instance = this;
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;

        initUI();
    }

    public static MainDashboard getInstance() {
        return instance;
    }

    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0 - Kontrolni Panel");
        setSize(1350, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // --- SIDEBAR ---
        pnlSidebar = new JPanel();
        pnlSidebar.setBackground(sidebarColor);
        pnlSidebar.setPreferredSize(new Dimension(280, 800));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));

        // Profil sekcija
        JPanel pnlProfile = new JPanel(new GridBagLayout());
        pnlProfile.setOpaque(false);
        pnlProfile.setBorder(new EmptyBorder(40, 10, 40, 10));

        // Dinamički prikaz imena (Radi i za studente i za referente)
        String ime = "Gost";
        if (ReferentService.getCurrentUser() != null) {
            ime = ReferentService.getCurrentUser().getFirstName();
        }

        JLabel lblUser = new JLabel("<html><center>Prijavljeni korisnik:<br><b style='font-size:14px; color:white;'>"
                + ime.toUpperCase() + "</b></center></html>");
        lblUser.setForeground(new Color(171, 178, 185));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlProfile.add(lblUser);
        pnlSidebar.add(pnlProfile);

        // Meni dugmići
        pnlSidebar.add(createMenuButton("📊  STATISTIKA", e -> showStatPanel()));
        pnlSidebar.add(createMenuButton("👤  STUDENTI", e -> showStudentPanel()));
        pnlSidebar.add(createMenuButton("📚  PREDMETI", e -> showCoursePanel()));
        pnlSidebar.add(createMenuButton("📝  UPISI I OCJENE", e -> showEnrollmentPanel()));

        pnlSidebar.add(Box.createVerticalGlue());
        pnlSidebar.add(createMenuButton("🚪  ODJAVI SE", e -> handleLogout()));

        // --- GLAVNI RADNI PANEL ---
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(contentBg);

        // Gornji bar
        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);
        pnlTopBar.setPreferredSize(new Dimension(0, 65));
        pnlTopBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 232, 232)));

        lblSectionTitle = new JLabel("  Kontrolna Tabla");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSectionTitle.setForeground(sidebarColor);
        pnlTopBar.add(lblSectionTitle, BorderLayout.WEST);

        JPanel pnlRightSide = new JPanel(new BorderLayout());
        pnlRightSide.add(pnlTopBar, BorderLayout.NORTH);
        pnlRightSide.add(pnlContent, BorderLayout.CENTER);

        mainPanel.add(pnlSidebar, BorderLayout.WEST);
        mainPanel.add(pnlRightSide, BorderLayout.CENTER);
        add(mainPanel);

        // LOGIKA ZA STUDENTA: Ako nema referenta, znači da je student ulogovan
        if (ReferentService.getCurrentUser() == null) {
            setupStudentView();
        } else {
            showStatPanel();
        }
    }

    private void setupStudentView() {
        // Sakrij navigaciju jer student ne smije vidjeti tuđe podatke
        pnlSidebar.setVisible(false);
        lblSectionTitle.setText("  Moj Studentski Dosije");

        // Napomena: LoginFrame će nakon ovoga pozvati dashboard.updateContent(new StudentReportPanel(...))
        // tako da će se odmah prikazati karton tog studenta.
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(280, 55));
        btn.setPreferredSize(new Dimension(280, 55));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(sidebarColor);
        btn.setForeground(textColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

    // --- NAVIGACIJA ---

    public void showStatPanel() {
        lblSectionTitle.setText("  Pregled Statistike");
        updateContent(new StatPanel(studentService, courseService, enrollmentService));
    }

    public void showStudentPanel() {
        lblSectionTitle.setText("  Upravljanje Studentima");
        updateContent(new StudentTablePanel(studentService, enrollmentService));
    }

    private void showCoursePanel() {
        lblSectionTitle.setText("  Upravljanje Predmetima");
        updateContent(new CourseTablePanel(courseService));
    }

    private void showEnrollmentPanel() {
        lblSectionTitle.setText("  Upis Ocjena");
        updateContent(new EnrollmentPanel(studentService, courseService, enrollmentService));
    }

    // Metoda za dinamičku zamjenu panela
    public void updateContent(Component c) {
        pnlContent.removeAll();
        pnlContent.add(c, BorderLayout.CENTER);
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Da li želite da se odjavite?", "Odjava", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            referentService.logout(); // Očisti sesiju
            this.dispose();
            // Ovdje bi se idealno trebao vratiti LoginFrame,
            // što možeš uraditi u tvojoj Main klasi.
        }
    }
}