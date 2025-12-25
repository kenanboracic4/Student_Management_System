package StudentManagmentSystem.ui.gui.dialogs;

import StudentManagmentSystem.models.Enrollment;
import StudentManagmentSystem.services.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GradingDialog extends JDialog {
    private final EnrollmentService enrollmentService;
    private final Enrollment enrollment;
    private JComboBox<Integer> cbGrades;
    private JTextField txtReason;
    private boolean success = false;

    public GradingDialog(Frame parent, EnrollmentService es, Enrollment enr) {
        super(parent, "Ocjenjivanje", true);
        this.enrollmentService = es;
        this.enrollment = enr;

        setUndecorated(false);
        setSize(400, 450);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42)); // Tamna Navy
        header.setPreferredSize(new Dimension(0, 70));
        JLabel title = new JLabel("  UNOS OCJENE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridLayout(0, 1, 0, 15));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(25, 30, 25, 30));

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

        // Footer btn
        JButton btnSave = new JButton("POTVRDI I SPREMI");
        btnSave.setBackground(new Color(79, 70, 229));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(0, 55));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
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

    private void save() {
        try {
            String ref = ReferentService.getCurrentUser().getReferentId();
            enrollmentService.enterOrUpdateGrade(
                    enrollment.getStudentIndexNumber(), enrollment.getCourseCode(),
                    enrollment.getAcademicYear(), (Integer)cbGrades.getSelectedItem(),
                    txtReason.getText(), ref
            );
            success = true;
            dispose();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    public boolean isSuccess() { return success; }
}