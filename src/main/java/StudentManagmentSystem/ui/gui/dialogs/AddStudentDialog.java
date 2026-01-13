package StudentManagmentSystem.ui.gui.dialogs;

import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.services.StudentService;
import StudentManagmentSystem.services.ReferentService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dijaloški prozor za registraciju novog studenta u sistem.
 * Omogućava unos ličnih podataka, akademskih informacija i pristupnih parametara (lozinka).
 */
public class AddStudentDialog extends JDialog {
    private final StudentService studentService;
    private boolean studentAdded = false;

    private JTextField txtIndex, txtPassword, txtFirstName, txtLastName, txtProgram, txtYear;

    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT = new Color(15, 23, 42);
    private final Color COLOR_ACCENT = new Color(37, 99, 235);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    public AddStudentDialog(Frame parent, StudentService studentService) {
        super(parent, "Novi Student", true); // Modalni prozor
        this.studentService = studentService;
        initUI();
    }

    private void initUI() {
        setSize(480, 680);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(COLOR_BG);
        pnlHeader.setBorder(new EmptyBorder(30, 45, 15, 45));

        JLabel lblTitle = new JLabel("Novi Student");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT);

        JLabel lblSub = new JLabel("Unesite podatke za novu registraciju.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);

        // --- FORMA ---
        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setBackground(COLOR_BG);
        pnlForm.setBorder(new EmptyBorder(10, 45, 20, 45));

        txtIndex = createModernInput("INDEKS", pnlForm);
        txtPassword = createModernInput("LOZINKA", pnlForm);
        txtFirstName = createModernInput("IME", pnlForm);
        txtLastName = createModernInput("PREZIME", pnlForm);
        txtProgram = createModernInput("STUDIJSKI PROGRAM", pnlForm);
        txtYear = createModernInput("GODINA UPISA", pnlForm);

        // --- AKCIJE ---
        JPanel pnlActions = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlActions.setBackground(COLOR_BG);
        pnlActions.setBorder(new EmptyBorder(20, 45, 40, 45));

        JButton btnCancel = new JButton("PONIŠTI");
        styleButton(btnCancel, Color.WHITE, COLOR_TEXT, true);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("DODAJ STUDENTA");
        styleButton(btnSave, COLOR_ACCENT, Color.WHITE, false);
        btnSave.addActionListener(e -> handleSave());

        pnlActions.add(btnCancel);
        pnlActions.add(btnSave);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlForm, BorderLayout.CENTER);
        add(pnlActions, BorderLayout.SOUTH);
    }

    private JTextField createModernInput(String labelText, JPanel parent) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));

        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        parent.add(tf);
        parent.add(Box.createVerticalStrut(15));
        return tf;
    }

    private void styleButton(JButton btn, Color bg, Color fg, boolean border) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        if (border) btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 2));
        else btn.setBorderPainted(false);
    }

    /**
     * Prikuplja podatke iz polja, vrši validaciju i prosljeđuje ih StudentService-u.
     */
    private void handleSave() {
        try {
            String index = txtIndex.getText().trim();
            String pass = txtPassword.getText().trim();
            String name = txtFirstName.getText().trim();
            String surname = txtLastName.getText().trim();
            String prog = txtProgram.getText().trim();
            String yearStr = txtYear.getText().trim();

            // Osnovna validacija obaveznih polja
            if (index.isEmpty() || pass.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sva polja su obavezna!", "Greška", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int year = Integer.parseInt(yearStr);
            String refId = (ReferentService.getCurrentUser() != null)
                    ? ReferentService.getCurrentUser().getReferentId()
                    : "SISTEM";

            Student s = new Student(index, pass, name, surname, prog, year, refId);

            studentService.addStudent(s);
            this.studentAdded = true;

            JOptionPane.showMessageDialog(this, "Student uspješno registrovan!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Godina upisa mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Greška pri spremanju: " + ex.getMessage());
        }
    }

    public boolean isStudentAdded() { return studentAdded; }
}