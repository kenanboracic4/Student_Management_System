package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.services.CourseService;
import StudentManagmentSystem.ui.gui.dialogs.AddCourseDialog;

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
 * Panel koji prikazuje tabelarni pregled svih predmeta u sistemu.
 * Sadrži funkcionalnosti za pretragu u realnom vremenu, osvježavanje podataka
 * i pozivanje dijaloga za dodavanje novih predmeta.
 */
public class CourseTablePanel extends JPanel {
    private final CourseService courseService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private Timer searchTimer;

    // Definicija konzistentne palete boja za tabelarni prikaz
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // Brend plava
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    /**
     * Konstruktor panela. Inicijalizuje UI komponente i učitava početne podatke.
     * @param courseService Servis za dobavljanje podataka o predmetima.
     */
    public CourseTablePanel(CourseService courseService) {
        this.courseService = courseService;
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initTopBar();
        initTable();
        refreshData();

        // Inicijalizacija tajmera za pretragu (sprječava prečesto osvježavanje)
        searchTimer = new Timer(1000, e -> handleSearch());
        searchTimer.setRepeats(false);
    }

    /**
     * Kreira gornju traku (Top Bar) koja sadrži naslov sekcije,
     * polje za pretragu i dugme za dodavanje novog predmeta.
     */
    private void initTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        JLabel lblSection = new JLabel("Predmeti");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblSection.setForeground(COLOR_TEXT_MAIN);
        leftPanel.add(lblSection);

        // Polje za pretragu sa "Placeholder" efektom
        txtSearch = new JTextField("Pretraži...");
        txtSearch.setPreferredSize(new Dimension(220, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setForeground(new Color(148, 163, 184));
        txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));

        // Logika za vizuelni fokus polja za pretragu
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Pretraži...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(COLOR_TEXT_MAIN);
                }
                txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARY));
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Pretraži...");
                    txtSearch.setForeground(new Color(148, 163, 184));
                    txtSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
                }
            }
        });

        // Event listener koji pokreće tajmer pretrage nakon svakog otkucanog znaka
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchTimer.restart(); }
        });
        leftPanel.add(txtSearch);

        JButton btnAdd = createModernButton("+ NOVI", COLOR_PRIMARY);
        btnAdd.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            AddCourseDialog dialog = new AddCourseDialog((Frame) parent, courseService);
            dialog.setVisible(true);
            if (dialog.isCourseAdded()) refreshData();
        });

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(btnAdd, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    /**
     * Konfiguriše JTable komponentu, model tabele i vizuelni izgled zaglavlja.
     */
    private void initTable() {
        String[] columns = {"ŠIFRA", "NAZIV PREDMETA", "ECTS", "SEMESTAR"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; } // Zabrana direktnog editovanja u tabeli
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setBackground(COLOR_BG);
        table.setSelectionBackground(new Color(47, 75, 101));

        // Stilizacija zaglavlja tabele
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Renderer za padding teksta unutar ćelija
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        sp.getViewport().setBackground(COLOR_BG);
        add(sp, BorderLayout.CENTER);
    }

    /**
     * Pomoćna metoda za kreiranje uniformnih modernih dugmadi.
     */
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

    /**
     * Dobavlja sve predmete iz servisa i ponovo iscrtava redove u tabeli.
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        for (Course c : courseService.getAllCourses()) {
            tableModel.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getName().toUpperCase(),
                    c.getEcts(),
                    c.getSemester()
            });
        }
    }

    /**
     * Filtrira podatke u tabeli na osnovu teksta unesenog u polje za pretragu.
     * Pretraga se vrši i po nazivu i po šifri predmeta.
     */
    private void handleSearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty() || q.equals("pretraži...")) { refreshData(); return; }

        List<Course> filtered = courseService.getAllCourses().stream()
                .filter(c -> c.getName().toLowerCase().contains(q) || c.getCourseCode().toLowerCase().contains(q))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Course c : filtered) {
            tableModel.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getName().toUpperCase(),
                    c.getEcts(),
                    c.getSemester()
            });
        }
    }
}