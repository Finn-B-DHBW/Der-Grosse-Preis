package config.ui;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Kategorie");
    public JButton buttonDone = new JButton("fertig");
    public JPanel questionsContainer = new JPanel();

    public CategoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        questionsContainer.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        JScrollPane questionsScroll = new JScrollPane(questionsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        questionsScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(questionsScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)
        ));
        footer.add(buttonDone);
        add(footer, BorderLayout.SOUTH);
    }

    public void setCategoryName(String name) {
        String title = (name != null && !name.isBlank()) ? "Kategorie: " + name : "Kategorie";
        titleLabel.setText(title);
        revalidate();
        repaint();
    }
}
