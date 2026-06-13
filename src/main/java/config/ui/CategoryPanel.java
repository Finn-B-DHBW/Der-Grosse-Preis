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
    private static final Color HDR_BG      = new Color(244, 244, 244);
    private static final Color BORDER_CLR  = new Color(210, 210, 210);

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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HDR_BG);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
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
