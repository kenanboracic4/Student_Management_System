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

public class LoginFrame extends JFrame {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReferentService referentService;

    // Moderne Boje
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color secondaryColor = new Color(52, 73, 94);
    private final Color backgroundColor = new Color(245, 247, 250);

    public LoginFrame(StudentService ss, CourseService cs, EnrollmentService es, ReferentService rs) {
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
        this.referentService = rs;
        initUI();
    }

    private void initUI() {
        setTitle("Sistem Studentska Služba v2.0 - Prijava");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(secondaryColor);
        headerPanel.setPreferredSize(new Dimension(450, 100));

        JLabel lblTitle = new JLabel("STUDENTSKA SLUŽBA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABBED PANE ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("PRIJAVA", createLoginPanel());
        tabbedPane.addTab("REGISTRACIJA REFERENTA", createRegisterPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 45, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        // Izbor Uloge (Referent / Student)
        JLabel lblRole = new JLabel("Prijavljujem se kao:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(secondaryColor);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(lblRole, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setOpaque(false);
        JRadioButton rbReferent = new JRadioButton("Referent", true);
        JRadioButton rbStudent = new JRadioButton("Student");
        rbReferent.setFocusPainted(false); rbStudent.setFocusPainted(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbReferent); bg.add(rbStudent);
        rolePanel.add(rbReferent);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(rbStudent);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(rolePanel, gbc);

        // Polja za unos
        JTextField txtId = createStyledField("Korisnički ID / Broj Indeksa", panel, gbc, 2);
        JPasswordField txtPass = createStyledPasswordField("Lozinka", panel, gbc, 4);

        // Dugme za prijavu
        JButton btnLogin = createStyledButton("PRISTUPI SISTEMU");
        gbc.gridy = 6; gbc.insets = new Insets(30, 0, 10, 0);
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String id = txtId.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (id.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Molimo unesite sve podatke.", "Upozorenje", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (rbReferent.isSelected()) {
                // LOGIN REFERENTA
                if (referentService.login(id, pass)) {
                    MainDashboard dashboard = new MainDashboard(studentService, courseService, enrollmentService, referentService);
                    dashboard.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Pogrešan Referent ID ili lozinka!", "Neuspješna prijava", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // LOGIN STUDENTA (Kao u tvojoj konzoli)
                Optional<Student> studentOpt = studentService.getStudentByIndex(id);
                if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(pass)) {
                    // Otvaramo Dashboard ali direktno na karton studenta bez menija
                    openStudentView(studentOpt.get());
                } else {
                    JOptionPane.showMessageDialog(this, "Pogrešan broj indeksa ili lozinka!", "Neuspješna prijava", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void openStudentView(Student s) {
        // Kreiramo Dashboard
        MainDashboard dashboard = new MainDashboard(studentService, courseService, enrollmentService, referentService);

        // Postavljamo naslov i sakrivamo sidebar/navigaciju jer student smije vidjeti samo svoj karton
        dashboard.setTitle("Dosije Studenta: " + s.getFirstName() + " " + s.getLastName());

        // Kreiramo karton
        StudentReportPanel report = new StudentReportPanel(enrollmentService, s.getIndexNumber());

        // Prikazujemo karton i gasimo pristup ostalim funkcijama (ako tvoj Dashboard to dozvoljava)
        dashboard.updateContent(report);
        dashboard.setVisible(true);
        this.dispose();
    }

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

        JButton btnReg = createStyledButton("KREIRAJ NALOG REFERENTA");
        gbc.gridy = 8; gbc.insets = new Insets(25, 0, 0, 0);
        panel.add(btnReg, gbc);

        btnReg.addActionListener(e -> {
            try {
                String id = txtNewId.getText().trim();
                String pass = new String(txtNewPass.getPassword());
                String ime = txtIme.getText().trim();
                String prezime = txtPrezime.getText().trim();

                if(id.length() < 3) throw new Exception("ID mora imati bar 3 karaktera.");

                referentService.registerReferent(new StudentManagmentSystem.models.Referent(id, pass, ime, prezime));
                JOptionPane.showMessageDialog(this, "Referent uspješno registrovan!");

                // Vrati se na login tab
                ((JTabbedPane)panel.getParent()).setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage(), "Registracija neuspjela", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // --- STYLING METODE ---

    private JTextField createStyledField(String label, JPanel p, GridBagConstraints g, int y) {
        g.gridy = y; g.insets = new Insets(8, 0, 3, 0);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, g);

        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 38));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        l.setForeground(new Color(100, 116, 139));
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
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(primaryColor.darker()); }
            public void mouseExited(MouseEvent e) { b.setBackground(primaryColor); }
        });
        return b;
    }
}