package config.ui;

import config.model.Category;
import config.model.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class CategoryExpandablePanel extends JPanel {

    private static final Color HDR_BG     = Color.WHITE;
    private static final Color BORDER_CLR = new Color(226, 232, 240);  // slate-200
    private static final Color ROW_SEP    = new Color(241, 245, 249);  // slate-100
    private static final Color ACCENT     = new Color(59,  130, 246);  // blue-500
    private static final Color DANGER_CLR = new Color(239, 68,  68);   // red-500
    private static final float    FONT_NAME      = 14f;
    private static final float    FONT_Q         = 17f;
    private static final float    FONT_A         = 16f;
    private static final int      CHECK_COL_W    = 22;   // fixed px for ✔ / space column
    private static final int      INDENT_W       = 12;   // left indent before checkmark
    private static final String[] ANSWER_LETTERS = {"a)", "b)", "c)"};

    private final Category categoryModel;
    private final JButton  toggleButton  = new JButton("▶");
    private final JLabel   nameLabel     = new JLabel("");
    private final JButton  editButton    = new JButton("Bearbeiten");
    private final JButton  deleteButton  = new JButton("Löschen");

    private final JPanel header;
    private final JPanel contentPanel = new JPanel();
    private boolean expanded = false;

    public JButton getEditButton()   { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }

    public CategoryExpandablePanel(Category category) {
        this.categoryModel = category;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // ── Header ────────────────────────────────────────────────────────
        header = new JPanel(new BorderLayout());
        header.setBackground(HDR_BG);
        header.setOpaque(true);
        // Left blue accent stripe
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)));

        // Toggle glyph — looks like a label, no button chrome
        toggleButton.setFont(toggleButton.getFont().deriveFont(Font.PLAIN, FONT_NAME));
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setMargin(new Insets(0, 2, 0, 2));

        // Category name
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, FONT_NAME));
        nameLabel.setForeground(new Color(15, 23, 42));
        nameLabel.setText(category != null && category.getName() != null
                && !category.getName().isBlank() ? category.getName() : "Kategorie");

        // nameLabel in CENTER of left fills all available width → no truncation
        // Clicking anywhere on the left panel (glyph or name) expands/collapses
        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
        left.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        left.add(toggleButton, BorderLayout.WEST);
        left.add(nameLabel,    BorderLayout.CENTER);
        MouseAdapter expandClick = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { toggle(); }
        };
        left.addMouseListener(expandClick);
        nameLabel.addMouseListener(expandClick);
        header.add(left, BorderLayout.CENTER);

        // Bearbeiten + Löschen buttons
        editButton.setFont(editButton.getFont().deriveFont(Font.PLAIN, FONT_NAME));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setBackground(new Color(219, 234, 254));  // blue-100
        editButton.setForeground(ACCENT);
        editButton.setPreferredSize(new Dimension(102, 30));  // fixed — prevents truncation on DPI change
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.PLAIN, FONT_NAME));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setBackground(new Color(226, 232, 240));  // slate-200
        deleteButton.setForeground(DANGER_CLR);
        deleteButton.setPreferredSize(new Dimension(90, 30));  // fixed — prevents truncation on DPI change
        JPanel right = new JPanel(new GridLayout(1, 2, 6, 0));
        right.setOpaque(false);
        right.add(editButton);
        right.add(deleteButton);
        header.add(right, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── Content panel ─────────────────────────────────────────────────
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 12, 6, 12));
        contentPanel.setVisible(false);

        rebuildContent();

        setAlignmentX(Component.LEFT_ALIGNMENT);
        enforceCollapsedHeight();

        toggleButton.addActionListener(e -> toggle());
        updateToggleGlyph();
    }

    // ── Toggle ────────────────────────────────────────────────────────────────
    private void toggle() {
        expanded = !expanded;
        if (expanded) {
            if (categoryModel != null) {
                String nm = categoryModel.getName();
                setCategoryName((nm != null && !nm.isBlank()) ? nm : "Kategorie");
            }
            rebuildContent();
            if (contentPanel.getParent() != this) add(contentPanel, BorderLayout.CENTER);
            contentPanel.setVisible(true);
        } else {
            if (contentPanel.getParent() == this) remove(contentPanel);
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
            int h = Math.max(header.getPreferredSize().height,
                             toggleButton.getPreferredSize().height) + 4;
            setPreferredSize(null);
            setMinimumSize(new Dimension(0, h));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        } else {
            setPreferredSize(null);
            setMinimumSize(null);
            int h = header.getPreferredSize().height
                  + (contentPanel.isVisible() ? contentPanel.getPreferredSize().height : 0)
                  + 14;
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(h, 40)));
        }
    }

    // ── Name ─────────────────────────────────────────────────────────────────
    public String getCategoryName() { return nameLabel.getText(); }

    public void setCategoryName(String name) {
        nameLabel.setText((name != null && !name.isBlank()) ? name : "Kategorie");
    }

    // ── Read-only content ─────────────────────────────────────────────────────
    public final void rebuildContent() {
        contentPanel.removeAll();
        if (categoryModel == null || categoryModel.getPointQuestionMap() == null) {
            contentPanel.add(new JLabel("Keine Fragen"));
        } else {
            for (Map.Entry<Integer, ConfigQuestion> entry
                    : categoryModel.getPointQuestionMap().entrySet()) {
                Integer points = entry.getKey();
                ConfigQuestion q = entry.getValue();

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setOpaque(false);
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, ROW_SEP),
                        BorderFactory.createEmptyBorder(6, 4, 6, 4)));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                String qText = (q != null && q.getQuestion() != null) ? q.getQuestion() : "";
                JLabel qLabel = new JLabel(String.format("[%d]  %s", points, qText));
                qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD, FONT_Q));
                row.add(qLabel);

                if (q != null && q.getAnswers() != null) {
                    int i = 1;
                    for (String ans : q.getAnswers()) {
                        String letter = (i <= ANSWER_LETTERS.length) ? ANSWER_LETTERS[i - 1] : i + ")";
                        boolean correct = (q.getCorrectAnswer() == i);

                        JPanel ansRow = new JPanel();
                        ansRow.setLayout(new BoxLayout(ansRow, BoxLayout.X_AXIS));
                        ansRow.setOpaque(false);
                        ansRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                        // indent
                        ansRow.add(Box.createRigidArea(new Dimension(INDENT_W, 0)));

                        // fixed-width checkmark column (left of the letter)
                        JLabel chk = new JLabel(correct ? "✔" : "");
                        chk.setFont(chk.getFont().deriveFont(Font.PLAIN, FONT_A));
                        int h = chk.getPreferredSize().height;
                        chk.setMinimumSize(new Dimension(CHECK_COL_W, h));
                        chk.setPreferredSize(new Dimension(CHECK_COL_W, h));
                        chk.setMaximumSize(new Dimension(CHECK_COL_W, Integer.MAX_VALUE));
                        ansRow.add(chk);

                        // letter + answer text
                        JLabel txt = new JLabel(letter + "  " + (ans == null ? "" : ans));
                        txt.setFont(txt.getFont().deriveFont(Font.PLAIN, FONT_A));
                        ansRow.add(txt);

                        row.add(ansRow);
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
