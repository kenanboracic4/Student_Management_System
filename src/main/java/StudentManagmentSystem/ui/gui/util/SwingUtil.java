package StudentManagmentSystem.ui.gui.util;



import javax.swing.*;
import java.awt.*;

public class SwingUtil {

    // Ultra-kontrastna paleta koju koristimo kroz cijeli sistem
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_TEXT = new Color(15, 23, 42);
    public static final Color COLOR_BORDER = new Color(226, 232, 240);

    public static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    public static void setModernBorder(JComponent comp) {
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
}