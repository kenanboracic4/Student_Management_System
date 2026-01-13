package StudentManagmentSystem.ui.gui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Pomoćna klasa za centralizovano upravljanje dizajnom (UI/UX).
 * Sadrži konstante boja i metode za uniformno stilizovanje Swing komponenti.
 */
public class SwingUtil {

    // --- GLOBALNA PALETA BOJA (Tailwind-inspired) ---
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235); // Intenzivna plava
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_TEXT = new Color(15, 23, 42);     // Skoro crna (Slate 900)
    public static final Color COLOR_BORDER = new Color(226, 232, 240); // Svijetlo siva za linije

    /**
     * Primjenjuje moderan stil na JButton.
     * Uključuje prostran padding, specifičan font i promjenu kursora.
     */
    public static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // CompoundBorder: Vanjska linija + Unutrašnji razmak (padding)
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    /**
     * Postavlja modernu ivicu sa paddingom na bilo koju JComponent.
     * Idealno za tekstualna polja i panele.
     */
    public static void setModernBorder(JComponent comp) {
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
}