package StudentManagmentSystem.ui.gui.auth;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.dashboard.MainDashboard;
import StudentManagmentSystem.ui.gui.components.StudentReportPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Glavni prozor za autentifikaciju korisnika.
 * Implementira grafički interfejs za prijavu referenata i studenata,
 * kao i registraciju novih referenata koristeći tabbed pane raspored.
 */
public class LoginFrame extends JFrame {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // Definicija palete boja za moderan izgled (UI/UX)
    private final Color primaryColor = new Color(41, 128, 185); // Plava
    private final Color secondaryColor = new Color(52, 73, 94); // Tamno siva
    private final Color backgroundColor = new Color(245, 247, 250); // Svijetlo siva pozadina

    /**
     * Konstruktor klase koji prima sve servise potrebne za rad aplikacije.
     */
    public LoginFrame(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;
        initUI();
    }

    /**
     * Inicijalizuje osnovne postavke prozora i učitava panele.
     */
    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0 - Prijava");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centriranje prozora na ekranu
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // --- HEADER SEKCIJA ---
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(secondaryColor);
        headerPanel.setPreferredSize(new Dimension(450, 100));

        JLabel lblTitle = new JLabel("STUDENTSKA SLUŽBA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABOVI ZA PRIJAVU I REGISTRACIJU ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("PRIJAVA", createLoginPanel());
        tabbedPane.addTab("REGISTRACIJA REFERENTA", createRegisterPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Kreira panel sa formom za prijavu.
     * Koristi radio-dugmad za odabir uloge (Referent/Student).
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 45, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        // Radio buttons za ulogu
        JLabel lblRole = new JLabel("Prijavljujem se kao:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(secondaryColor);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(lblRole, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setOpaque(false);
        JRadioButton rbReferent = new JRadioButton("Referent", true);
        JRadioButton rbStudent = new JRadioButton("Student");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbReferent); bg.add(rbStudent);
        rolePanel.add(rbReferent);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(rbStudent);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(rolePanel, gbc);

        // Form polja
        JTextField txtId = createStyledField("Korisnički ID / Broj Indeksa", panel, gbc, 2);
        JPasswordField txtPass = createStyledPasswordField("Lozinka", panel, gbc, 4);

        JButton btnLogin = createStyledButton("PRISTUPI SISTEMU");
        gbc.gridy = 6; gbc.insets = new Insets(30, 0, 10, 0);
        panel.add(btnLogin, gbc);

        // Logika prijave
        btnLogin.addActionListener(e -> {
            String id = txtId.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (id.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Molimo unesite sve podatke.", "Upozorenje", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (rbReferent.isSelected()) {
                if (referentService.login(id, pass)) {
                    new MainDashboard(studentService, courseService, enrollmentService, referentService).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Pogrešan Referent ID ili lozinka!", "Greska", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Optional<Student> studentOpt = studentService.getStudentByIndex(id);
                if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(pass)) {
                    openStudentView(studentOpt.get());
                } else {
                    JOptionPane.showMessageDialog(this, "Pogrešan broj indeksa ili lozinka!", "Greska", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    /**
     * Otvara ograničeni Dashboard pogled namijenjen studentu.
     * Student vidi isključivo svoj akademski karton (Report).
     */
    private void openStudentView(Student s) {
        MainDashboard dashboard = new MainDashboard(studentService, courseService, enrollmentService, referentService);
        dashboard.setTitle("Dosije Studenta: " + s.getFirstName() + " " + s.getLastName());

        StudentReportPanel report = new StudentReportPanel(enrollmentService, s.getIndexNumber());
        dashboard.updateContent(report);
        dashboard.setVisible(true);
        this.dispose();
    }

    /**
     * Kreira formu za registraciju novog referenta.
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 45, 25, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; gbc.gridx = 0;

        JTextField txtNewId = createStyledField("Novi Referent ID", panel, gbc, 0);
        JTextField txtIme = createStyledField("Ime", panel, gbc, 2);
        JTextField txtPrezime = createStyledField("Prezime", panel, gbc, 4);
        JPasswordField txtNewPass = createStyledPasswordField("Lozinka", panel, gbc, 6);

        JButton btnReg = createStyledButton("KREIRAJ NALOG");
        gbc.gridy = 8; gbc.insets = new Insets(25, 0, 0, 0);
        panel.add(btnReg, gbc);

        btnReg.addActionListener(e -> {
            try {
                referentService.registerReferent(new StudentManagmentSystem.models.Referent(
                        txtNewId.getText().trim(),
                        new String(txtNewPass.getPassword()),
                        txtIme.getText().trim(),
                        txtPrezime.getText().trim()
                ));
                JOptionPane.showMessageDialog(this, "Referent uspješno registrovan!");
                ((JTabbedPane)panel.getParent()).setSelectedIndex(0); // Povratak na login
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage(), "Registracija neuspjela", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // --- POMOĆNE METODE ZA STILIZACIJU KOMPONENTI ---

    private JTextField createStyledField(String label, JPanel p, GridBagConstraints g, int y) {
        g.gridy = y; g.insets = new Insets(8, 0, 3, 0);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l, g);

        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        g.gridy = y + 1; g.insets = new Insets(0, 0, 10, 0);
        p.add(f, g);
        return f;
    }

    private JPasswordField createStyledPasswordField(String label, JPanel p, GridBagConstraints g, int y) {
        g.gridy = y; g.insets = new Insets(8, 0, 3, 0);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l, g);

        JPasswordField f = new JPasswordField();
        f.setPreferredSize(new Dimension(0, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        g.gridy = y + 1; g.insets = new Insets(0, 0, 10, 0);
        p.add(f, g);
        return f;
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(primaryColor);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(0, 48));
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efekat promjene boje pri prelasku mišem (Hover effect)
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(primaryColor.darker()); }
            public void mouseExited(MouseEvent e) { b.setBackground(primaryColor); }
        });
        return b;
    }
}