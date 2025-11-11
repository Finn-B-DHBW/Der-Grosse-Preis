package dhbw.dgp.ui;

import dhbw.dgp.Category;
import dhbw.dgp.Question;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * An expandable/collapsible panel to display a Category summary in the Configuration view.
 * Collapsed header shows: [toggle] [category name] [Bearbeiten]
 * Expanded content shows: readonly question/answers stacked vertically.
 */
public class CategoryExpandablePanel extends JPanel {
    private final Category categoryModel;
    private final JButton toggleButton = new JButton("▶");
    private final JLabel nameLabel = new JLabel("");
    public final JButton editButton = new JButton("bearbeiten");
    private final JPanel contentPanel = new JPanel();
    private final JPanel header;
    private boolean expanded = false;

    public CategoryExpandablePanel(Category category) {
        this.categoryModel = category;
        setLayout(new BorderLayout());
        // Visual debug: purple outline around the whole expandable panel + existing top matte border
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 0, 128), 1),
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220,220,220))
        ));

        // Header
        header = new JPanel(new BorderLayout());
        // Add vertical padding above and below the header buttons
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 165, 0), 1), // orange header outline
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        ));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        // Visual debug: blue outline for left header group
        left.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        left.add(toggleButton);
        nameLabel.setText(category != null && category.getName() != null && !category.getName().isBlank()
                ? category.getName() : "Kategorie");
        left.add(nameLabel);
        header.add(left, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        // Visual debug: green outline for right header group
        right.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        right.add(editButton);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        // Visual debug: magenta outline for the content panel
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 1));
        if (expanded) {
            add(contentPanel, BorderLayout.CENTER);
            contentPanel.setVisible(true);
        } else {
            contentPanel.setVisible(false);
        }
        rebuildContent();

        // Ensure compact layout behavior in Y BoxLayout parent
        setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        enforceCollapsedHeight();

        toggleButton.addActionListener(e -> toggle());
        updateToggleGlyph();
    }

    private void toggle() {
        expanded = !expanded;
        // Always refresh content on expand to reflect latest edits
        if (expanded) {
            // Sync name from model (in case it changed)
            if (categoryModel != null) {
                String nm = categoryModel.getName();
                setCategoryName((nm != null && !nm.isBlank()) ? nm : "Kategorie");
            }
            rebuildContent();
            // Ensure content panel is part of layout so it consumes space
            if (contentPanel.getParent() != this) {
                add(contentPanel, BorderLayout.CENTER);
            }
            contentPanel.setVisible(true);
        } else {
            // Remove content from layout to free all vertical space
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
        if (p != null) { p.revalidate(); p.repaint(); }
    }

    private void updateToggleGlyph() {
        toggleButton.setText(expanded ? "▼" : "▶");
    }

    private void enforceCollapsedHeight() {
        // When collapsed, constrain our height to header/toggle height plus a tiny padding to avoid clipping
        if (!expanded) {
            int headerH = header.getPreferredSize().height;
            int btnH = toggleButton.getPreferredSize().height;
            int padding = 3; // small safety padding so the button is never cut off
            int h = Math.max(headerH, btnH) + padding;
            Dimension pref = new Dimension(Integer.MAX_VALUE, h);
            Dimension min = new Dimension(0, h);
            Dimension max = new Dimension(Integer.MAX_VALUE, h);
            setPreferredSize(pref);
            setMinimumSize(min);
            setMaximumSize(max);
        } else {
            // On expand, allow natural preferred/min sizes, but cap max height to avoid BoxLayout stretching
            // and add a small bottom padding so the content has breathing space
            setPreferredSize(null);
            setMinimumSize(null);
            int headerH = header.getPreferredSize().height;
            int contentH = contentPanel.isVisible() ? contentPanel.getPreferredSize().height : 0;
            int borderTop = 1; // matte top border
            int extraBottomPadding = 12; // small visual padding below the last item
            int h = headerH + contentH + borderTop + extraBottomPadding;
            if (h <= 0) {
                // Fallback to preferred size if not yet laid out
                h = Math.max(getPreferredSize() != null ? getPreferredSize().height : 0, headerH + 10 + extraBottomPadding);
            }
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        }
    }

    public void setCategoryName(String name) {
        nameLabel.setText((name != null && !name.isBlank()) ? name : "Kategorie");
    }

    /**
     * Recreates the read-only question/answer listing from the current model state.
     */
    public final void rebuildContent() {
        contentPanel.removeAll();
        if (categoryModel == null || categoryModel.getPointQuestionMap() == null) {
            contentPanel.add(new JLabel("Keine Fragen"));
        } else {
            for (Map.Entry<Integer, Question> entry : categoryModel.getPointQuestionMap().entrySet()) {
                Integer points = entry.getKey();
                Question q = entry.getValue();

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                // Visual debug: cyan outline for each row; keep inner padding
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.CYAN, 1),
                        BorderFactory.createEmptyBorder(4, 24, 4, 4)
                ));
                // Points + question
                String qText = (q != null && q.getQuestion() != null) ? q.getQuestion() : "";
                JLabel qLabel = new JLabel(String.format("%d Punkte: %s", points, qText));
                qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD));
                row.add(qLabel);

                // Answers
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
