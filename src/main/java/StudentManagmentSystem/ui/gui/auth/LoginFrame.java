package StudentManagmentSystem.ui.gui.auth;

import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.dashboard.MainDashboard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // Boje
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color secondaryColor = new Color(52, 73, 94);
    private final Color backgroundColor = new Color(236, 240, 241);

    public LoginFrame(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;
        initUI();
    }

    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(secondaryColor);
        headerPanel.setPreferredSize(new Dimension(450, 80));
        JLabel lblTitle = new JLabel("STUDENTSKA SLUŽBA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABBED PANE (Login / Registracija) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("PRIJAVA", createLoginPanel());
        tabbedPane.addTab("REGISTRACIJA REFERENTA", createRegisterPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        // Izbor Uloge
        gbc.gridy = 0;
        JLabel lblRole = new JLabel("Prijavi se kao:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblRole, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setBackground(backgroundColor);
        JRadioButton rbReferent = new JRadioButton("Referent", true);
        JRadioButton rbStudent = new JRadioButton("Student");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbReferent); bg.add(rbStudent);
        rolePanel.add(rbReferent); rolePanel.add(rbStudent);
        gbc.gridy = 1;
        panel.add(rolePanel, gbc);

        // ID i Lozinka
        JTextField txtId = createStyledField("Korisnički ID / Indeks", panel, gbc, 2);
        JPasswordField txtPass = createStyledPasswordField("Lozinka", panel, gbc, 4);

        // Dugme
        JButton btnLogin = createStyledButton("PRISTUPI SISTEMU");
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String id = txtId.getText();
            String pass = new String(txtPass.getPassword());

            if (rbReferent.isSelected()) {
                if (referentService.login(id, pass)) {
                    new MainDashboard(studentService, courseService, enrollmentService, referentService).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Pogrešan ID ili lozinka!");
                }
            } else {
                // Logika za studenta (pregled ocjena)
                JOptionPane.showMessageDialog(this, "Prijava studenta u izradi...");
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        JTextField txtNewId = createStyledField("Novi Referent ID (min. 3 znaka)", panel, gbc, 0);
        JTextField txtIme = createStyledField("Ime", panel, gbc, 2);
        JTextField txtPrezime = createStyledField("Prezime", panel, gbc, 4);
        JPasswordField txtNewPass = createStyledPasswordField("Lozinka", panel, gbc, 6);

        JButton btnReg = createStyledButton("KREIRAJ NALOG");
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(btnReg, gbc);

        btnReg.addActionListener(e -> {
            try {
                String id = txtNewId.getText();
                String pass = new String(txtNewPass.getPassword());
                String ime = txtIme.getText();
                String prezime = txtPrezime.getText();

                // Kreiramo objekt Referent jer tvoj servis to zahtijeva
                StudentManagmentSystem.models.Referent noviReferent =
                        new StudentManagmentSystem.models.Referent(id, pass, ime, prezime);

                // Pozivamo ispravnu metodu iz tvog servisa
                referentService.registerReferent(noviReferent);

                JOptionPane.showMessageDialog(this, "Registracija uspješna! Molimo prijavite se.");

                // Opcionalno: očisti polja nakon registracije
                txtNewId.setText("");
                txtNewPass.setText("");
                txtIme.setText("");
                txtPrezime.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // --- POMOĆNE METODE ZA DIZAJN ---

    private JTextField createStyledField(String label, JPanel p, GridBagConstraints g, int y) {
        g.gridy = y; g.insets = new Insets(10, 0, 2, 0);
        p.add(new JLabel(label), g);
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 35));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        g.gridy = y + 1; g.insets = new Insets(0, 0, 10, 0);
        p.add(f, g);
        return f;
    }

    private JPasswordField createStyledPasswordField(String label, JPanel p, GridBagConstraints g, int y) {
        g.gridy = y; g.insets = new Insets(10, 0, 2, 0);
        p.add(new JLabel(label), g);
        JPasswordField f = new JPasswordField();
        f.setPreferredSize(new Dimension(0, 35));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        g.gridy = y + 1; g.insets = new Insets(0, 0, 10, 0);
        p.add(f, g);
        return f;
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(primaryColor);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(0, 45));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(primaryColor.brighter()); }
            public void mouseExited(MouseEvent e) { b.setBackground(primaryColor); }
        });
        return b;
    }
}