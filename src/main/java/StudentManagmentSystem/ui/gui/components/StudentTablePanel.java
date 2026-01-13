package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.services.EnrollmentService;
import StudentManagmentSystem.services.StudentService;
import StudentManagmentSystem.ui.gui.dashboard.MainDashboard;
import StudentManagmentSystem.ui.gui.dialogs.AddStudentDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Glavni panel za upravljanje studentima.
 * Omogućava pregled, dodavanje, brisanje i pretragu studenata,
 * kao i brzi pristup akademskom kartonu studenta putem dvoklika.
 */
public class StudentTablePanel extends JPanel {
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private Timer searchTimer;

    // Paleta boja usklađena sa modernim UI standardima
    private final Color COLOR_PRIMARY = new Color(37, 99, 235);
    private final Color COLOR_DANGER = new Color(220, 38, 38);
    private final Color COLOR_SUCCESS = new Color(22, 163, 74);
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    /**
     * @param studentService Servis za bazične operacije nad studentima.
     * @param enrollmentService Servis potreban za generisanje izvještaja pri navigaciji.
     */
    public StudentTablePanel(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;

        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initTopBar();
        initTable();
        refreshData();

        // Tajmer od 1000ms sprječava preopterećenje baze tokom kucanja
        searchTimer = new Timer(1000, e -> handleSearch());
        searchTimer.setRepeats(false);
    }

    /**
     * Kreira gornji kontrolni panel sa naslovom, pretragom i akcionim dugmadi.
     */
    private void initTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Naslov i Pretraga
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        JLabel lblSection = new JLabel("Studenti");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblSection.setForeground(COLOR_TEXT_MAIN);
        leftPanel.add(lblSection);

        txtSearch = new JTextField("Pretraži...");
        txtSearch.setPreferredSize(new Dimension(220, 40));
        txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));

        // Dinamički efekti fokusa na polju za pretragu
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Pretraži...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(COLOR_TEXT_MAIN);
                }
                txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARY));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Pretraži...");
                    txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
                }
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { searchTimer.restart(); }
        });
        leftPanel.add(txtSearch);

        // Akciona dugmad (CRUD)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = createModernButton("+ NOVI", COLOR_PRIMARY);
        JButton btnDelete = createModernButton("OBRIŠI", COLOR_DANGER);
        JButton btnRefresh = createModernButton("OSVJEŽI", COLOR_SUCCESS);

        btnAdd.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            AddStudentDialog dialog = new AddStudentDialog((Frame) parentWindow, studentService);
            dialog.setVisible(true);
            if (dialog.isStudentAdded()) refreshData();
        });

        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> refreshData());

        actionPanel.add(btnAdd);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(actionPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    /**
     * Inicijalizuje tabelu i postavlja MouseListener za navigaciju na izvještaj.
     */
    private void initTable() {
        String[] columns = {"INDEKS", "IME", "PREZIME", "PROGRAM", "GODINA"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Stilizacija zaglavlja
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BG);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Dodavanje unutrašnjeg paddinga ćelijama (bolja čitljivost)
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        // Logika za dvoklik -> Otvaranje dosijea studenta
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String studentIndex = table.getValueAt(row, 0).toString();
                        StudentReportPanel reportPanel = new StudentReportPanel(enrollmentService, studentIndex);
                        MainDashboard.getInstance().updateContent(reportPanel);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Osvježava tabelu podacima iz student servisa.
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        studentService.getAllStudents().forEach(s -> {
            tableModel.addRow(new Object[]{
                    s.getIndexNumber(), s.getFirstName().toUpperCase(), s.getLastName().toUpperCase(),
                    s.getStudyProgram(), s.getEnrollmentYear()
            });
        });
    }

    /**
     * Logika za brisanje studenta uz potvrdu korisnika.
     */
    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Molimo odaberite studenta za brisanje.");
            return;
        }
        String index = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite obrisati studenta " + index + "?",
                "Potvrda brisanja", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            studentService.getStudentByIndex(index).ifPresent(s -> {
                studentService.deleteStudent(s);
                refreshData();
            });
        }
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setBorderPainted(false);
        return btn;
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty() || query.equals("pretraži...")) { refreshData(); return; }

        List<Student> filtered = studentService.getAllStudents().stream()
                .filter(s -> s.getIndexNumber().toLowerCase().contains(query) ||
                        s.getFirstName().toLowerCase().contains(query) ||
                        s.getLastName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        filtered.forEach(s -> tableModel.addRow(new Object[]{
                s.getIndexNumber(), s.getFirstName().toUpperCase(), s.getLastName().toUpperCase(),
                s.getStudyProgram(), s.getEnrollmentYear()
        }));
    }
}