package config.ui;

import config.model.Category;
import config.model.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CategoryExpandablePanel extends JPanel {
    private final Category categoryModel;
    private final JButton toggleButton = new JButton("▶");
    private final JLabel nameLabel = new JLabel("");
    private final JButton editButton = new JButton("bearbeiten");

    public JButton getEditButton() {
        return editButton;
    }

    private final JPanel contentPanel = new JPanel();
    private final JPanel header;
    private boolean expanded = false;

    public CategoryExpandablePanel(Category category) {
        this.categoryModel = category;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 0, 128), 1),
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220,220,220))
        ));

        header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 165, 0), 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        ));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        left.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        left.add(toggleButton);
        nameLabel.setText(category != null && category.getName() != null && !category.getName().isBlank()
                ? category.getName() : "Kategorie");
        left.add(nameLabel);
        header.add(left, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        right.add(editButton);
        header.add(right, BorderLayout.EAST);
        // CENTER spacer: ensures EAST always gets its preferred width on narrow windows
        JPanel centerSpacer = new JPanel();
        centerSpacer.setOpaque(false);
        header.add(centerSpacer, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 1));
        if (expanded) {
            add(contentPanel, BorderLayout.CENTER);
            contentPanel.setVisible(true);
        } else {
            contentPanel.setVisible(false);
        }
        rebuildContent();

        setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        enforceCollapsedHeight();

        toggleButton.addActionListener(e -> toggle());
        updateToggleGlyph();
    }

    private void toggle() {
        expanded = !expanded;
        if (expanded) {
            if (categoryModel != null) {
                String nm = categoryModel.getName();
                setCategoryName((nm != null && !nm.isBlank()) ? nm : "Kategorie");
            }
            rebuildContent();
            if (contentPanel.getParent() != this) {
                add(contentPanel, BorderLayout.CENTER);
            }
            contentPanel.setVisible(true);
        } else {
            if (contentPanel.getParent() == this) {
                remove(contentPanel);
            }
            contentPanel.setVisible(false);
        }
        enforceCollapsedHeight();
        updateToggleGlyph();
        revalidate();
        repaint();
        Container p = getParent();
        while (p != null) {
            p.revalidate();
            p.repaint();
            if (p instanceof JScrollPane) break;
            p = p.getParent();
        }
    }

    private void updateToggleGlyph() {
        toggleButton.setText(expanded ? "▼" : "▶");
    }

    private void enforceCollapsedHeight() {
        if (!expanded) {
            int headerH = header.getPreferredSize().height;
            int btnH = toggleButton.getPreferredSize().height;
            int padding = 3;
            int h = Math.max(headerH, btnH) + padding;
            // Do NOT set preferred width to Integer.MAX_VALUE — that causes the scroll
            // pane viewport to render the panel billions of pixels wide, pushing EAST
            // components (bearbeiten button) off-screen. Leave preferredSize null so the
            // natural header width is used; BoxLayout Y_AXIS still stretches each child
            // to the container width via maxWidth = Integer.MAX_VALUE.
            setPreferredSize(null);
            setMinimumSize(new Dimension(0, h));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        } else {
            setPreferredSize(null);
            setMinimumSize(null);
            int headerH = header.getPreferredSize().height;
            int contentH = contentPanel.isVisible() ? contentPanel.getPreferredSize().height : 0;
            int borderTop = 1;
            int extraBottomPadding = 12;
            int h = headerH + contentH + borderTop + extraBottomPadding;
            if (h <= 0) {
                h = Math.max(getPreferredSize() != null ? getPreferredSize().height : 0, headerH + 10 + extraBottomPadding);
            }
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        }
    }

    public void setCategoryName(String name) {
        nameLabel.setText((name != null && !name.isBlank()) ? name : "Kategorie");
    }

    public final void rebuildContent() {
        contentPanel.removeAll();
        if (categoryModel == null || categoryModel.getPointQuestionMap() == null) {
            contentPanel.add(new JLabel("Keine Fragen"));
        } else {
            for (Map.Entry<Integer, ConfigQuestion> entry : categoryModel.getPointQuestionMap().entrySet()) {
                Integer points = entry.getKey();
                ConfigQuestion q = entry.getValue();

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.CYAN, 1),
                        BorderFactory.createEmptyBorder(4, 24, 4, 4)
                ));
                String qText = (q != null && q.getQuestion() != null) ? q.getQuestion() : "";
                JLabel qLabel = new JLabel(String.format("%d Punkte: %s", points, qText));
                qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD));
                row.add(qLabel);

                if (q != null && q.getAnswers() != null) {
                    int i = 1;
                    for (String ans : q.getAnswers()) {
                        String prefix = (q.getCorrectAnswer() == i) ? "✔ Antwort " : "Antwort ";
                        JLabel aLabel = new JLabel(prefix + i + ": " + (ans == null ? "" : ans));
                        row.add(aLabel);
                        i++;
                    }
                }
                contentPanel.add(row);
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
