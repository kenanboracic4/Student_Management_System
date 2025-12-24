package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.services.StudentService;
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

public class StudentTablePanel extends JPanel {
    private final StudentService studentService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private Timer searchTimer;

    private final Color COLOR_PRIMARY = new Color(37, 99, 235);
    private final Color COLOR_DANGER = new Color(220, 38, 38);
    private final Color COLOR_SUCCESS = new Color(22, 163, 74);
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);
    private final Color COLOR_TEXT_SEC = new Color(148, 163, 184);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    public StudentTablePanel(StudentService studentService) {
        this.studentService = studentService;
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initTopBar();
        initTable();
        refreshData();

        // REFRESH NA 1 SEKUNDU (1000ms)
        searchTimer = new Timer(1000, e -> handleSearch());
        searchTimer.setRepeats(false);
    }

    private void initTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        JLabel lblSection = new JLabel("Studenti");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblSection.setForeground(COLOR_TEXT_MAIN);
        leftPanel.add(lblSection);

        // SMANJEN SEARCH DA NE GUŠI DUGMIĆE
        txtSearch = new JTextField("Pretraži...");
        txtSearch.setPreferredSize(new Dimension(220, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setForeground(COLOR_TEXT_SEC);
        txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));

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
                    txtSearch.setForeground(COLOR_TEXT_SEC);
                    txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
                }
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchTimer.restart();
            }
        });

        leftPanel.add(txtSearch);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = createModernButton("+ NOVI", COLOR_PRIMARY);
        JButton btnDelete = createModernButton("OBRIŠI", COLOR_DANGER);
        JButton btnRefresh = createModernButton("OSVJEŽI", COLOR_SUCCESS);

        btnAdd.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            // Koristimo direktan Frame cast da budemo sigurni
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

    private void initTable() {
        String[] columns = {"INDEKS", "IME", "PREZIME", "PROGRAM", "GODINA"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(241, 245, 249));
        table.setSelectionForeground(COLOR_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setBackground(COLOR_BG);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BG);
        header.setForeground(new Color(100, 116, 139));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(COLOR_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 40));
        return btn;
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getIndexNumber(), s.getFirstName().toUpperCase(), s.getLastName().toUpperCase(),
                    s.getStudyProgram(), s.getEnrollmentYear()
            });
        }
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty() || query.equals("pretraži...")) {
            refreshData();
            return;
        }
        List<Student> filtered = studentService.getAllStudents().stream()
                .filter(s -> s.getIndexNumber().toLowerCase().contains(query) ||
                        s.getFirstName().toLowerCase().contains(query) ||
                        s.getLastName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        tableModel.setRowCount(0);
        for (Student s : filtered) {
            tableModel.addRow(new Object[]{
                    s.getIndexNumber(), s.getFirstName().toUpperCase(), s.getLastName().toUpperCase(),
                    s.getStudyProgram(), s.getEnrollmentYear()
            });
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String index = (String) tableModel.getValueAt(row, 0);
        Student s = studentService.getAllStudents().stream().filter(st -> st.getIndexNumber().equals(index)).findFirst().orElse(null);
        if (s != null && JOptionPane.showConfirmDialog(this, "Obriši: " + index + "?", "Potvrda", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            studentService.deleteStudent(s);
            refreshData();
        }
    }
}