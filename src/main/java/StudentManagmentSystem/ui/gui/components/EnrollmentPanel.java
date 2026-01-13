package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.services.*;
import StudentManagmentSystem.ui.gui.dialogs.GradingDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel za upravljanje upisima studenata na predmete i procesom ocjenjivanja.
 * Objedinjuje rad StudentService, CourseService i EnrollmentService kako bi
 * referentima omogućio brz upis i uvid u rezultate ispita.
 */
public class EnrollmentPanel extends JPanel {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    private JComboBox<String> comboStudents, comboCourses;
    private JTable table;
    private DefaultTableModel tableModel;

    // --- MODERNA PALETA BOJA ---
    private final Color COLOR_BG = new Color(248, 250, 252);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_ACCENT = new Color(79, 70, 229); // Indigo
    private final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);
    private final Color COLOR_TEXT_SUB = new Color(100, 116, 139);
    private final Color COLOR_SELECTION = new Color(51, 65, 85);

    /**
     * Konstruktor panela koji inicijalizuje sve slojeve i komponente.
     */
    public EnrollmentPanel(StudentService ss, CourseService cs, EnrollmentService es) {
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;

        setLayout(new BorderLayout(0, 30));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initHeader();

        JPanel contentBody = new JPanel(new BorderLayout(0, 30));
        contentBody.setOpaque(false);
        contentBody.add(createEnrollmentCard(), BorderLayout.NORTH); // Dio za upis
        contentBody.add(createTableCard(), BorderLayout.CENTER);   // Dio za pregled/ocjenu

        add(contentBody, BorderLayout.CENTER);
        refreshTable();
    }

    /**
     * Inicijalizuje naslov i opis sekcije u gornjem dijelu panela.
     */
    private void initHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        JLabel title = new JLabel("Upis i Ocjenjivanje");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(COLOR_TEXT_MAIN);
        JLabel sub = new JLabel("Administracija akademskih upisa i unos rezultata ispita.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(COLOR_TEXT_SUB);
        header.add(title);
        header.add(sub);
        add(header, BorderLayout.NORTH);
    }

    /**
     * Kreira "karticu" sa formom za brz upis studenta na predmet.
     * @return JPanel sa stilizovanim ComboBox-ovima i dugmetom.
     */
    private JPanel createEnrollmentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel inputs = new JPanel(new GridLayout(1, 3, 20, 0));
        inputs.setOpaque(false);

        comboStudents = createStyledCombo();
        loadStudents(); // Punjenje podacima iz baze
        inputs.add(createFieldWrapper("STUDENT", comboStudents));

        comboCourses = createStyledCombo();
        loadCourses(); // Punjenje podacima iz baze
        inputs.add(createFieldWrapper("PREDMET", comboCourses));

        JButton btnEnroll = new JButton("UPISI STUDENTA");
        btnEnroll.setBackground(COLOR_ACCENT);
        btnEnroll.setForeground(Color.WHITE);
        btnEnroll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEnroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnroll.addActionListener(e -> handleEnrollment());

        JPanel btnCont = new JPanel(new BorderLayout());
        btnCont.setOpaque(false);
        btnCont.setBorder(new EmptyBorder(18, 0, 0, 0));
        btnCont.add(btnEnroll);
        inputs.add(btnCont);

        card.add(inputs, BorderLayout.CENTER);
        return card;
    }

    /**
     * Kreira tabelarni prikaz svih aktivnih upisa.
     * Omogućava otvaranje dijaloga za ocjenjivanje putem dvoklika na red.
     */
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(COLOR_CARD);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"INDEKS", "PREDMET", "GODINA", "OCJENA", "DATUM"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setSelectionBackground(COLOR_SELECTION);
        table.setShowVerticalLines(false);

        // Header tabele
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 45));

        // Listener za dvoklik
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openGrading();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        card.add(new JLabel("Aktivni upisi (Dvoklikni red za unos ocjene):"), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    /**
     * Otvara GradingDialog za selektovani red u tabeli.
     */
    private void openGrading() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String idx = table.getValueAt(row, 0).toString();
            String code = table.getValueAt(row, 1).toString();
            String year = table.getValueAt(row, 2).toString();

            enrollmentService.getEnrollment(idx, code, year).ifPresent(enr -> {
                GradingDialog gd = new GradingDialog((Frame) SwingUtilities.getWindowAncestor(this), enrollmentService, enr);
                gd.setVisible(true);
                if (gd.isSuccess()) refreshTable();
            });
        }
    }

    /**
     * Osvježava podatke u tabeli povlačenjem najnovijih upisa iz baze.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        enrollmentService.getAllEnrollments().forEach(e -> {
            tableModel.addRow(new Object[]{
                    e.getStudentIndexNumber(), e.getCourseCode(), e.getAcademicYear(),
                    (e.getGrade() == null || e.getGrade() == 0) ? "Nije ocjenjen" : e.getGrade(),
                    e.getGradeDate() != null ? e.getGradeDate() : "-"
            });
        });
    }

    /**
     * Vrši upis studenta na predmet pozivanjem EnrollmentService-a.
     * Automatski prepoznaje trenutno ulogovanog referenta.
     */
    private void handleEnrollment() {
        try {
            String sid = ((String)comboStudents.getSelectedItem()).split(" - ")[0];
            String cid = ((String)comboCourses.getSelectedItem()).split(" - ")[0];
            String ref = (ReferentService.getCurrentUser() != null) ? ReferentService.getCurrentUser().getReferentId() : "SISTEM";

            enrollmentService.registerNewEnrollment(new Enrollment(sid, cid, "2024/2025", null, null, null, null, ref, null));
            JOptionPane.showMessageDialog(this, "Student uspješno upisan!");
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pomoćne metode za učitavanje podataka u ComboBox-ove
    private void loadStudents() { studentService.getAllStudents().forEach(s -> comboStudents.addItem(s.getIndexNumber() + " - " + s.getFirstName() + " " + s.getLastName())); }
    private void loadCourses() { courseService.getAllCourses().forEach(c -> comboCourses.addItem(c.getCourseCode() + " - " + c.getName())); }

    private JComboBox<String> createStyledCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return cb;
    }

    private JPanel createFieldWrapper(String lbl, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(COLOR_TEXT_SUB);
        p.add(l, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}