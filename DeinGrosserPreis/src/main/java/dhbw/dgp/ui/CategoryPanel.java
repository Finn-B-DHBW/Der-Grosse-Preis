package dhbw.dgp.ui;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Kategorie");
    public JButton buttonDone = new JButton("fertig");
    public JPanel questionsContainer = new JPanel();

    public CategoryPanel() {
        // Use BorderLayout so the questions container can span full width
        setLayout(new BorderLayout());
        // Visual debug: blue outline around the whole CategoryPanel
        setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

        // Compact header instead of TitledBorder to avoid extra left insets
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // container for dynamic question panels (fills available width)
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        // Visual debug: green outline around questions container
        questionsContainer.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        // Wrap the questions container in a scroll pane to allow scrolling when many questions are present
        JScrollPane questionsScroll = new JScrollPane(questionsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        questionsScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(questionsScroll, BorderLayout.CENTER);

        // Footer aligned to the right with a small top padding (replaces former vertical strut)
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
