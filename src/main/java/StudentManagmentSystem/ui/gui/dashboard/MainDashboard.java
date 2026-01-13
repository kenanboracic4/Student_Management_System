package StudentManagmentSystem.ui.gui.dashboard;

import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.components.CourseTablePanel;
import StudentManagmentSystem.ui.gui.components.EnrollmentPanel;
import StudentManagmentSystem.ui.gui.components.StudentTablePanel;
import StudentManagmentSystem.ui.gui.components.StudentReportPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Glavni radni prozor aplikacije (Dashboard).
 * Upravlja Sidebar navigacijom, dinamičkom zamjenom sadržaja (Content switching)
 * i prilagođavanjem interfejsa u zavisnosti od toga da li je ulogovan student ili referent.
 */
public class MainDashboard extends JFrame {
    private static MainDashboard instance; // Singleton referenca
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // --- MODERNA PALETA BOJA ---
    private final Color sidebarColor = new Color(33, 47, 61); // Tamna teget
    private final Color activeColor = new Color(52, 152, 219);  // Svijetlo plava
    private final Color contentBg = Color.WHITE;
    private final Color textColor = new Color(236, 240, 241);

    private JPanel pnlContent; // Centralni panel koji se mijenja
    private JPanel pnlSidebar; // Bočni meni
    private JLabel lblSectionTitle; // Naslov trenutne sekcije

    /**
     * Konstruktor dashboarda. Postavlja Singleton instancu i inicijalizuje UI.
     */
    public MainDashboard(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        instance = this;
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;

        initUI();
    }

    /**
     * Omogućava pristup glavnom prozoru iz bilo koje podkomponente.
     */
    public static MainDashboard getInstance() {
        return instance;
    }

    /**
     * Postavlja osnovni raspored (BorderLayout), kreira sidebar i content area.
     */
    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0 - Kontrolni Panel");
        setSize(1350, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // --- SIDEBAR NAVIGACIJA ---
        pnlSidebar = new JPanel();
        pnlSidebar.setBackground(sidebarColor);
        pnlSidebar.setPreferredSize(new Dimension(280, 800));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));

        // Profil sekcija: Prikazuje ime trenutno ulogovanog korisnika
        JPanel pnlProfile = new JPanel(new GridBagLayout());
        pnlProfile.setOpaque(false);
        pnlProfile.setBorder(new EmptyBorder(40, 10, 40, 10));

        String ime = (ReferentService.getCurrentUser() != null)
                ? ReferentService.getCurrentUser().getFirstName()
                : "Gost";

        JLabel lblUser = new JLabel("<html><center>Prijavljeni korisnik:<br><b style='font-size:14px; color:white;'>"
                + ime.toUpperCase() + "</b></center></html>");
        lblUser.setForeground(new Color(171, 178, 185));
        pnlProfile.add(lblUser);
        pnlSidebar.add(pnlProfile);

        // Dodavanje navigacionih dugmića
        pnlSidebar.add(createMenuButton("📊  STATISTIKA", e -> showStatPanel()));
        pnlSidebar.add(createMenuButton("👤  STUDENTI", e -> showStudentPanel()));
        pnlSidebar.add(createMenuButton("📚  PREDMETI", e -> showCoursePanel()));
        pnlSidebar.add(createMenuButton("📝  UPISI I OCJENE", e -> showEnrollmentPanel()));

        pnlSidebar.add(Box.createVerticalGlue()); // Gura odjavu na dno
        pnlSidebar.add(createMenuButton("🚪  ODJAVI SE", e -> handleLogout()));

        // --- CENTRALNI RADNI PANEL ---
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(contentBg);

        // Top bar (Naslovna traka sekcije)
        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);
        pnlTopBar.setPreferredSize(new Dimension(0, 65));
        pnlTopBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 232, 232)));

        lblSectionTitle = new JLabel("  Kontrolna Tabla");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pnlTopBar.add(lblSectionTitle, BorderLayout.WEST);

        // Sastavljanje desne strane
        JPanel pnlRightSide = new JPanel(new BorderLayout());
        pnlRightSide.add(pnlTopBar, BorderLayout.NORTH);
        pnlRightSide.add(pnlContent, BorderLayout.CENTER);

        mainPanel.add(pnlSidebar, BorderLayout.WEST);
        mainPanel.add(pnlRightSide, BorderLayout.CENTER);
        add(mainPanel);

        // PROVJERA ULOGE: Ako je student ulogovan, sakrij meni
        if (ReferentService.getCurrentUser() == null) {
            setupStudentView();
        } else {
            showStatPanel(); // Referentu po defaultu prikaži statistiku
        }
    }

    /**
     * Ograničava interfejs ukoliko je ulogovan student.
     */
    private void setupStudentView() {
        pnlSidebar.setVisible(false);
        lblSectionTitle.setText("  Moj Studentski Dosije");
    }

    /**
     * Pomoćna metoda za kreiranje stilizovanih navigacionih dugmića sa Hover efektom.
     */
    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(280, 55));
        btn.setBackground(sidebarColor);
        btn.setForeground(textColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

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

    // --- METODE ZA ZAMJENU SADRŽAJA ---

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

    /**
     * Ključna metoda koja uklanja stari panel i postavlja novi u centar ekrana.
     * Poziva revalidate() i repaint() kako bi Java Swing osvježio prikaz.
     */
    public void updateContent(Component c) {
        pnlContent.removeAll();
        pnlContent.add(c, BorderLayout.CENTER);
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Da li želite da se odjavite?", "Odjava", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            referentService.logout();
            this.dispose();
        }
    }
}