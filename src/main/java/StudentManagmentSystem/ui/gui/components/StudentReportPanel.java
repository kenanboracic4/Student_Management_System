package StudentManagmentSystem.ui.gui.components;

import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.models.StudentReport;
import StudentManagmentSystem.services.EnrollmentService;
import StudentManagmentSystem.ui.gui.dashboard.MainDashboard;
import StudentManagmentSystem.ui.gui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel za prikaz akademskog kartona (izvještaja) studenta.
 * Vizuelno prikazuje lične podatke, ključne statistike (prosjek i bodovi)
 * te tabelarni pregled svih položenih ispita.
 */
public class StudentReportPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    private final String studentIndex;

    /**
     * Konstruktor panela za izvještaj.
     * @param enrollmentService Servis koji generiše podatke za izvještaj.
     * @param studentIndex Broj indeksa studenta čiji se podaci prikazuju.
     */
    public StudentReportPanel(EnrollmentService enrollmentService, String studentIndex) {
        this.enrollmentService = enrollmentService;
        this.studentIndex = studentIndex;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(40, 50, 40, 50));

        renderReport();
    }

    /**
     * Glavna metoda za generisanje i iscrtavanje UI elemenata na osnovu podataka iz baze.
     * Uključuje logiku za uslovno sakrivanje navigacije zavisno od uloge korisnika.
     */
    private void renderReport() {
        try {
            // Generisanje modela podataka izvještaja preko servisa
            StudentReport report = enrollmentService.generateStudentReport(studentIndex);

            // --- NAVIGACIJA ---
            JPanel pnlTopNavigation = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlTopNavigation.setOpaque(false);

            JButton btnBack = new JButton("← NAZAD NA LISTU");
            SwingUtil.styleButton(btnBack, Color.WHITE, SwingUtil.COLOR_TEXT);
            btnBack.addActionListener(e -> MainDashboard.getInstance().showStudentPanel());

            /* * SIGURNOSNA PROVJERA:
             * Ako u sesiji nema referenta, znači da je ulogovan student.
             * Studentu se onemogućava povratak na listu svih studenata radi zaštite privatnosti.
             */
            if (StudentManagmentSystem.services.ReferentService.getCurrentUser() == null) {
                btnBack.setVisible(false);
            }

            pnlTopNavigation.add(btnBack);

            // --- HEADER: Ime studenta i statističke kartice ---
            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setOpaque(false);
            pnlHeader.setBorder(new EmptyBorder(20, 0, 30, 0));

            // Informacije o studentu
            JPanel pnlInfoText = new JPanel(new GridLayout(2, 1, 0, 5));
            pnlInfoText.setOpaque(false);

            JLabel lblName = new JLabel(report.getStudent().getFirstName() + " " + report.getStudent().getLastName());
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 32));
            lblName.setForeground(SwingUtil.COLOR_TEXT);

            JLabel lblSubInfo = new JLabel("Indeks: " + report.getStudent().getIndexNumber() +
                    "  •  Program: " + report.getStudent().getStudyProgram());
            lblSubInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblSubInfo.setForeground(new Color(100, 116, 139));

            pnlInfoText.add(lblName);
            pnlInfoText.add(lblSubInfo);

            // Vizuelne kartice za statistiku
            JPanel pnlStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            pnlStats.setOpaque(false);
            pnlStats.add(createMiniStatCard("UKUPNO ECTS", String.valueOf(report.getTotalEcts()), new Color(37, 99, 235)));
            pnlStats.add(createMiniStatCard("PROSJEK", String.format("%.2f", report.getAverageGrade()), new Color(22, 163, 74)));

            pnlHeader.add(pnlInfoText, BorderLayout.WEST);
            pnlHeader.add(pnlStats, BorderLayout.EAST);

            // --- TABELA POLOŽENIH ISPITA ---
            String[] columns = {"ŠIFRA PREDMETA", "OCJENA", "DATUM POLAGANJA", "AKAD. GODINA", "REFERENT"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            // Prikazujemo samo predmete sa prolaznom ocjenom (>= 6)
            for (Enrollment e : report.getEnrollments()) {
                if (e.getGrade() != null && e.getGrade() >= 6) {
                    model.addRow(new Object[]{
                            e.getCourseCode(),
                            e.getGrade(),
                            e.getGradeDate(),
                            e.getAcademicYear(),
                            e.getAddedByReferentId()
                    });
                }
            }

            JTable table = new JTable(model);
            styleReportTable(table);

            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            sp.getViewport().setBackground(Color.WHITE);

            // Sastavljanje layouta
            JPanel pnlNorthContainer = new JPanel(new BorderLayout());
            pnlNorthContainer.setOpaque(false);
            pnlNorthContainer.add(pnlTopNavigation, BorderLayout.NORTH);
            pnlNorthContainer.add(pnlHeader, BorderLayout.CENTER);

            add(pnlNorthContainer, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);

        } catch (Exception ex) {
            showError("Greška pri generisanju izvještaja: " + ex.getMessage());
        }
    }

    /**
     * Kreira malu stilizovanu karticu za prikaz numeričkih podataka.
     * @param label Naslov kartice (npr. PROSJEK).
     * @param value Vrijednost podatka.
     * @param accent Boja teksta vrijednosti.
     */
    private JPanel createMiniStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(new Color(148, 163, 184));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(accent);

        card.add(lblTitle);
        card.add(lblValue);
        return card;
    }

    /**
     * Primjenjuje vizuelni stil na tabelu sa ispitima.
     */
    private void styleReportTable(JTable table) {
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    private void showError(String msg) {
        removeAll();
        add(new JLabel(msg, SwingConstants.CENTER));
        revalidate();
        repaint();
    }
}