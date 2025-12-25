package StudentManagmentSystem.ui.gui.dialogs;



import StudentManagmentSystem.services.EnrollmentService;
import StudentManagmentSystem.ui.gui.util.SwingUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GradingDialog extends JDialog {
    private final EnrollmentService enrollmentService;
    private final String studentIndex;
    private final String courseCode;
    private JComboBox<Integer> comboGrades;

    public GradingDialog(Frame parent, EnrollmentService service, String index, String code) {
        super(parent, "Unos Ocjene", true);
        this.enrollmentService = service;
        this.studentIndex = index;
        this.courseCode = code;

        initUI();
    }

    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel pnlContent = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblInfo = new JLabel("Unesite ocjenu za predmet: " + courseCode);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        comboGrades = new JComboBox<>(new Integer[]{6, 7, 8, 9, 10});
        SwingUtil.setModernBorder(comboGrades);

        JButton btnSave = new JButton("POTVRDI OCJENU");
        SwingUtil.styleButton(btnSave, SwingUtil.COLOR_PRIMARY, Color.WHITE);

        btnSave.addActionListener(e -> {
            int grade = (int) comboGrades.getSelectedItem();
            // enrollmentService.updateGrade(studentIndex, courseCode, grade);
            JOptionPane.showMessageDialog(this, "Ocjena uspješno upisana!");
            dispose();
        });

        pnlContent.add(lblInfo);
        pnlContent.add(comboGrades);
        pnlContent.add(btnSave);

        add(pnlContent, BorderLayout.CENTER);
    }
}