package StudentManagmentSystem.ui.gui.dialogs;

import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.services.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dijalog za unos ili izmjenu ocjene studenta na određenom predmetu.
 * Implementira strogo ograničen unos (5-10) i bilježi razlog izmjene.
 */
public class GradingDialog extends JDialog {
    private final EnrollmentService enrollmentService;
    private final Enrollment enrollment;
    private JComboBox<Integer> cbGrades;
    private JTextField txtReason;
    private boolean success = false;

    /**
     * @param parent Glavni Dashboard okvir.
     * @param es Servis za upravljanje ocjenama.
     * @param enr Objekat upisa koji se trenutno ocjenjuje.
     */
    public GradingDialog(Frame parent, EnrollmentService es, Enrollment enr) {
        super(parent, "Ocjenjivanje", true);
        this.enrollmentService = es;
        this.enrollment = enr;

        setSize(400, 450);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // --- HEADER: Informacije o akciji ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("  UNOS OCJENE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);

        root.add(header, BorderLayout.NORTH);

        // --- BODY: Forma za unos ---
        JPanel body = new JPanel(new GridLayout(0, 1, 0, 15));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Podaci o studentu i predmetu su fiksni (read-only)
        body.add(createLabel("STUDENT: " + enrollment.getStudentIndexNumber()));
        body.add(createLabel("PREDMET: " + enrollment.getCourseCode()));

        body.add(new JLabel("Izaberite ocjenu:"));
        cbGrades = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9, 10});
        cbGrades.setPreferredSize(new Dimension(0, 40));
        body.add(cbGrades);

        body.add(new JLabel("Razlog izmjene (ako se ocjena mijenja):"));
        txtReason = new JTextField();
        txtReason.setPreferredSize(new Dimension(0, 40));
        body.add(txtReason);

        root.add(body, BorderLayout.CENTER);

        // --- FOOTER: Dugme za spremanje ---
        JButton btnSave = new JButton("POTVRDI I SPREMI");
        btnSave.setBackground(new Color(79, 70, 229));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(0, 55));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> save());

        root.add(btnSave, BorderLayout.SOUTH);
        add(root);
    }

    private JLabel createLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    /**
     * Poziva servis za upis ocjene u bazu.
     * Ukoliko servis baci grešku (npr. neispravni podaci), ista se prikazuje korisniku.
     */
    private void save() {
        try {
            // Dohvatanje trenutnog referenta iz sesije
            String ref = ReferentService.getCurrentUser().getReferentId();

            enrollmentService.enterOrUpdateGrade(
                    enrollment.getStudentIndexNumber(),
                    enrollment.getCourseCode(),
                    enrollment.getAcademicYear(),
                    (Integer)cbGrades.getSelectedItem(),
                    txtReason.getText(),
                    ref
            );

            success = true;
            dispose(); // Zatvaranje dijaloga
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage(),
                    "Sistemska Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() { return success; }
}