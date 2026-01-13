package StudentManagmentSystem.ui.gui.dialogs;

import StudentManagmentSystem.models.Course;
import StudentManagmentSystem.services.CourseService;
import StudentManagmentSystem.services.ReferentService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modalni dijalog za unos novog akademskog predmeta.
 * Pruža validaciju numeričkih polja i automatsko povezivanje sa trenutnim referentom.
 */
public class AddCourseDialog extends JDialog {
    private final CourseService courseService;
    private boolean courseAdded = false; // Flag za javljanje uspjeha roditeljskom panelu

    private JTextField txtCode, txtName, txtEcts, txtSemester;

    // Konzistentna paleta boja za dijaloge
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT = new Color(15, 23, 42);
    private final Color COLOR_ACCENT = new Color(37, 99, 235);
    private final Color COLOR_BORDER = new Color(226, 232, 240);

    /**
     * @param parent Roditeljski Frame (Dashboard) za pravilno centriranje.
     * @param courseService Servis za perzistenciju podataka.
     */
    public AddCourseDialog(Frame parent, CourseService courseService) {
        super(parent, "Novi Predmet", true); // true postavlja dijalog kao modalni
        this.courseService = courseService;
        initUI();
    }

    private void initUI() {
        setSize(450, 580);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // --- HEADER SEKCIJA ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(COLOR_BG);
        pnlHeader.setBorder(new EmptyBorder(30, 45, 15, 45));

        JLabel lblTitle = new JLabel("Novi Predmet");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT);

        JLabel lblSub = new JLabel("Unesite šifru i detalje novog predmeta.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);

        // --- FORMA SEKCIJA ---
        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setBackground(COLOR_BG);
        pnlForm.setBorder(new EmptyBorder(10, 45, 20, 45));

        // Kreiranje polja pomoću fabričke metode
        txtCode = createModernInput("ŠIFRA PREDMETA", pnlForm);
        txtName = createModernInput("NAZIV PREDMETA", pnlForm);
        txtEcts = createModernInput("ECTS BODOVI", pnlForm);
        txtSemester = createModernInput("SEMESTAR", pnlForm);

        // --- AKCIJE SEKCIJA (Dugmad) ---
        JPanel pnlActions = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlActions.setBackground(COLOR_BG);
        pnlActions.setBorder(new EmptyBorder(20, 45, 40, 45));

        JButton btnCancel = new JButton("PONIŠTI");
        styleButton(btnCancel, Color.WHITE, COLOR_TEXT, true);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("SAČUVAJ");
        styleButton(btnSave, COLOR_ACCENT, Color.WHITE, false);
        btnSave.addActionListener(e -> handleSave());

        pnlActions.add(btnCancel);
        pnlActions.add(btnSave);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlForm, BorderLayout.CENTER);
        add(pnlActions, BorderLayout.SOUTH);
    }

    /**
     * Fabrička metoda za uniforman izgled labela i input polja.
     */
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

    /**
     * Centralizovano stilizovanje dugmadi za dijalog.
     */
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
     * Validira unos i poziva servis za spremanje predmeta.
     */
    private void handleSave() {
        try {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();

            // Validacija brojeva - baciće Exception ako unos nije validan
            int ects = Integer.parseInt(txtEcts.getText().trim());
            int semester = Integer.parseInt(txtSemester.getText().trim());

            // Osiguranje da polja nisu prazna
            if (code.isEmpty() || name.isEmpty()) {
                throw new Exception("Šifra i naziv su obavezni!");
            }

            String refId = (ReferentService.getCurrentUser() != null)
                    ? ReferentService.getCurrentUser().getReferentId()
                    : "SISTEM";

            Course c = new Course(code, name, ects, semester, refId);
            courseService.addCourse(c);
            this.courseAdded = true;

            JOptionPane.showMessageDialog(this, "Predmet uspješno dodan!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ECTS i Semestar moraju biti brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Greška: " + ex.getMessage());
        }
    }

    public boolean isCourseAdded() { return courseAdded; }
}