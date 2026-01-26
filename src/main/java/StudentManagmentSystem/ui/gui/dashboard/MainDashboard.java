package StudentManagmentSystem.ui.gui.dashboard;

import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Glavni radni prozor aplikacije (Dashboard).
 * Redizajniran: Flat UI, veći fontovi i bolji raspored.
 */
public class MainDashboard extends JFrame {
    private static MainDashboard instance; // Singleton referenca
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // --- MODERNA PALETA BOJA ---
    private final Color sidebarColor = new Color(33, 47, 61);   // Tamna teget (Pozadina menija)
    private final Color sidebarHoverColor = new Color(44, 62, 80); // Malo svjetlija za hover
    private final Color activeColor = new Color(52, 152, 219);  // Svijetlo plava (Akcent)
    private final Color contentBg = Color.WHITE;
    private final Color textColor = new Color(236, 240, 241);   // Bijela/Sivkasta za tekst

    private JPanel pnlContent; // Centralni panel koji se mijenja
    private JPanel pnlSidebar; // Bočni meni
    private JLabel lblSectionTitle; // Naslov trenutne sekcije

    /**
     * Konstruktor dashboarda.
     */
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

        // --- SIDEBAR NAVIGACIJA ---
        pnlSidebar = new JPanel();
        pnlSidebar.setBackground(sidebarColor);
        pnlSidebar.setPreferredSize(new Dimension(280, 800));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));

        // 1. PROFIL SEKCIJA
        JPanel pnlProfile = new JPanel(new GridBagLayout());
        pnlProfile.setOpaque(false);
        pnlProfile.setBorder(new EmptyBorder(40, 10, 40, 10));

        String ime = (ReferentService.getCurrentUser() != null)
                ? ReferentService.getCurrentUser().getFirstName()
                : "Gost";

        // Koristimo HTML za formatiranje teksta u labeli
        JLabel lblUser = new JLabel("<html><center><span style='font-size:12px; color:#bdc3c7;'>Prijavljeni korisnik:</span><br>" +
                "<span style='font-size:16px; color:white; font-weight:bold;'>" + ime.toUpperCase() + "</span></center></html>");
        pnlProfile.add(lblUser);
        pnlSidebar.add(pnlProfile);

        // 2. NAVIGACIONI DUGMIĆI (Sa razmacima)
        pnlSidebar.add(createMenuButton("📊  STATISTIKA", e -> showStatPanel()));
        pnlSidebar.add(Box.createVerticalStrut(10)); // Razmak

        pnlSidebar.add(createMenuButton("👤  STUDENTI", e -> showStudentPanel()));
        pnlSidebar.add(Box.createVerticalStrut(10)); // Razmak

        pnlSidebar.add(createMenuButton("📚  PREDMETI", e -> showCoursePanel()));
        pnlSidebar.add(Box.createVerticalStrut(10)); // Razmak

        pnlSidebar.add(createMenuButton("📝  UPISI I OCJENE", e -> showEnrollmentPanel()));

        pnlSidebar.add(Box.createVerticalGlue()); // Gura dugme za odjavu na dno

        // 3. ODJAVA
        pnlSidebar.add(createMenuButton("🚪  ODJAVI SE", e -> handleLogout()));
        pnlSidebar.add(Box.createVerticalStrut(20)); // Malo prostora od dna

        // --- CENTRALNI RADNI PANEL ---
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(contentBg);

        // Top bar (Naslovna traka)
        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);
        pnlTopBar.setPreferredSize(new Dimension(0, 65));
        pnlTopBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 232, 232)));

        lblSectionTitle = new JLabel("  Kontrolna Tabla");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSectionTitle.setForeground(new Color(44, 62, 80));
        pnlTopBar.add(lblSectionTitle, BorderLayout.WEST);

        // Sastavljanje desne strane
        JPanel pnlRightSide = new JPanel(new BorderLayout());
        pnlRightSide.add(pnlTopBar, BorderLayout.NORTH);
        pnlRightSide.add(pnlContent, BorderLayout.CENTER);

        mainPanel.add(pnlSidebar, BorderLayout.WEST);
        mainPanel.add(pnlRightSide, BorderLayout.CENTER);
        add(mainPanel);

        // Logika prikaza zavisno od role
        if (ReferentService.getCurrentUser() == null) {
            setupStudentView();
        } else {
            showStatPanel();
        }
    }

    private void setupStudentView() {
        pnlSidebar.setVisible(false);
        lblSectionTitle.setText("  Moj Studentski Dosije");
    }

    /**
     * Kreira moderno Flat dugme za meni.
     */
    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);

        // Dimenzije i Font
        btn.setMaximumSize(new Dimension(280, 55));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Boje i Izgled (Flat stil)
        btn.setBackground(sidebarColor);
        btn.setForeground(textColor);
        btn.setFocusPainted(false);       // Uklanja tačkasti okvir na klik
        btn.setBorderPainted(false);      // Uklanja 3D border
        btn.setContentAreaFilled(false);  // Omogućava custom painting pozadine ako treba
        btn.setOpaque(true);              // Bitno da bi se vidjela boja pozadine

        // Poravnanje teksta
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 30, 0, 0)); // Padding lijevo

        // Hover Efekti (Miš iznad dugmeta)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(sidebarHoverColor);
                btn.setForeground(activeColor);
                // Dodajemo plavu crtu lijevo kao indikator
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, activeColor),
                        new EmptyBorder(0, 25, 0, 0) // Smanjujemo padding za širinu bordera
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(sidebarColor);
                btn.setForeground(textColor);
                // Vraćamo originalni border
                btn.setBorder(new EmptyBorder(0, 30, 0, 0));
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

    public void updateContent(Component c) {
        pnlContent.removeAll();
        pnlContent.add(c, BorderLayout.CENTER);
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da se želite odjaviti?",
                "Odjava",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            referentService.logout();
            this.dispose();

        }
    }
}