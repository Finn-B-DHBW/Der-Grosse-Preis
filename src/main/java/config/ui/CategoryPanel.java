package config.ui;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends JPanel {

    private final JLabel  titleLabel         = new JLabel("Kategorie");
    private final JButton buttonSave         = new JButton("Speichern");
    private final JButton buttonCancel       = new JButton("Abbrechen");
    private final JPanel  questionsContainer = new JPanel();

    private static final float FONT_TITLE  = 15f;
    private static final float FONT_NORMAL = 13f;
    private static final Color HDR_BG      = new Color(239, 246, 255);  // blue-50
    private static final Color HDR_BORDER  = new Color(191, 219, 254);  // blue-200
    private static final Color HDR_FG      = new Color(29,  78,  216);  // blue-700
    private static final Color BORDER_CLR  = new Color(226, 232, 240);  // slate-200

    public JButton getButtonSave()         { return buttonSave; }
    public JButton getButtonCancel()       { return buttonCancel; }
    public JPanel  getQuestionsContainer() { return questionsContainer; }

    public CategoryPanel() {
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildContent(),   BorderLayout.CENTER);
        add(buildFooter(),    BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, FONT_TITLE));
        titleLabel.setForeground(HDR_FG);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HDR_BG);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, HDR_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JScrollPane buildContent() {
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(questionsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel buildFooter() {
        for (JButton btn : new JButton[]{buttonSave, buttonCancel}) {
            btn.setFont(btn.getFont().deriveFont(Font.PLAIN, FONT_NORMAL));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 36));
        }
        buttonSave.setBackground(new Color(59, 130, 246));
        buttonSave.setForeground(Color.WHITE);
        buttonCancel.setForeground(new Color(100, 116, 139));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
        panel.add(buttonSave);
        panel.add(buttonCancel);
        return panel;
    }

    public void setCategoryName(String name) {
        titleLabel.setText((name != null && !name.isBlank()) ? "Kategorie: " + name : "Kategorie");
        revalidate();
        repaint();
    }
}
