package config.ui;

import config.model.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseWheelEvent;

public class QuestionPanel extends JPanel {

    private static final int   ANSWER_COUNT  = 3;
    private static final int   TEXT_ROWS     = 2;
    private static final int   TEXT_COLS     = 24;
    private static final float FONT_HEADER   = 15f;   // "X Punkte" block header
    private static final float FONT_LABEL    = 18f;   // row labels (?, a), b), c), "richtig?")
    private static final float FONT_AREA     = 14f;   // text area content
    private static final Color HDR_BG        = new Color(237, 242, 250);
    private static final Color HDR_BORDER    = new Color(200, 215, 240);
    private static final Color HDR_FG        = new Color(30, 60, 120);

    // Labels for answer rows: a), b), c)
    private static final String[] ANSWER_LABELS = {"a)", "b)", "c)"};

    private final Integer       pointsValue;
    private final ConfigQuestion questionModel;
    private boolean suppressCheckboxEvents = false;

    private final JTextArea   textAreaQuestion             = new JTextArea(TEXT_ROWS, TEXT_COLS);
    private final JTextArea[] answerAreas                  = new JTextArea[ANSWER_COUNT];
    private final JCheckBox[] correctCheckBoxes            = new JCheckBox[ANSWER_COUNT];

    public QuestionPanel() {
        this(null, null);
    }

    public QuestionPanel(Integer points, ConfigQuestion question) {
        this.pointsValue   = points;
        this.questionModel = question;
        for (int i = 0; i < ANSWER_COUNT; i++) {
            answerAreas[i]       = new JTextArea(TEXT_ROWS, TEXT_COLS);
            correctCheckBoxes[i] = new JCheckBox("");
            // FlatLaf: scale checkbox icon per component
            correctCheckBoxes[i].putClientProperty("FlatLaf.style",
                    "icon.width: 22; icon.height: 22");
        }
        buildLayout();
        populateFromModel();
        attachAutosaveListeners();
        attachCorrectAnswerBinding();
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void buildLayout() {
        setLayout(new GridBagLayout());
        // No separator line — extra top padding creates visual breathing room between blocks
        setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        addHeader(gbc);

        enableLineWrap(textAreaQuestion);
        for (JTextArea a : answerAreas) enableLineWrap(a);

        // Row 1: Frage — "?" label, text area, "richtig?" trailing header (centered in col)
        addQuestionRow(gbc);
        // Rows 2-4: Answers — a) b) c) labels, text areas, checkboxes
        for (int i = 0; i < ANSWER_COUNT; i++) addAnswerRow(i, gbc);
    }

    private void addHeader(GridBagConstraints gbc) {
        String text = (pointsValue != null ? pointsValue + " Punkte" : "Punkte");
        JLabel lbl  = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, FONT_HEADER));
        lbl.setForeground(HDR_FG);

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(HDR_BG);
        hdr.setOpaque(true);
        hdr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HDR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        hdr.add(lbl, BorderLayout.WEST);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 8, 0);
        add(hdr, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
    }

    private void addQuestionRow(GridBagConstraints gbc) {
        // "?" label
        JLabel qLbl = makeLabel("?");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 2, 4, 16);
        add(qLbl, gbc);

        // Text area
        JScrollPane scroll = wrapInScrollPane(textAreaQuestion);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 0, 4, 6);
        add(scroll, gbc);

        // "✔" header — centered in its column
        JLabel trail = makeLabel("✔");
        gbc.gridx = 2; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 4, 4, 2);
        add(trail, gbc);
    }

    private void addAnswerRow(int index, GridBagConstraints gbc) {
        int row = 2 + index;

        // a) / b) / c) label
        JLabel lbl = makeLabel(ANSWER_LABELS[index]);
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 2, 4, 16);
        add(lbl, gbc);

        // Text area
        JScrollPane scroll = wrapInScrollPane(answerAreas[index]);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 0, 4, 6);
        add(scroll, gbc);

        // Checkbox — centered under "richtig?"
        gbc.gridx = 2; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 4, 4, 2);
        add(correctCheckBoxes[index], gbc);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, FONT_LABEL));
        return lbl;
    }

    // ── Scroll-pane wrapper ───────────────────────────────────────────────────
    private JScrollPane wrapInScrollPane(JTextArea area) {
        area.setRows(TEXT_ROWS);
        area.setFont(area.getFont().deriveFont(FONT_AREA));
        JScrollPane scroll = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setWheelScrollingEnabled(false);
        area.addMouseWheelListener(e -> {
            JScrollPane inner = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, area);
            JScrollPane outer = (inner == null) ? null
                    : (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, inner);
            if (outer == null) return;
            outer.dispatchEvent(new MouseWheelEvent(
                    outer, e.getID(), e.getWhen(), e.getModifiersEx(),
                    0, 0, 0, 0, e.getClickCount(), e.isPopupTrigger(),
                    e.getScrollType(), e.getScrollAmount(), e.getWheelRotation(),
                    e.getPreciseWheelRotation()));
        });
        enforceTwoRowViewport(area, scroll);
        return scroll;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static void enableLineWrap(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private static void enforceTwoRowViewport(JTextArea area, JScrollPane scroll) {
        if (area == null || scroll == null) return;
        int rows     = Math.max(TEXT_ROWS, area.getRows());
        area.setRows(rows);
        FontMetrics fm   = area.getFontMetrics(area.getFont());
        int lineH        = (fm != null) ? fm.getHeight() : Math.max(14, area.getPreferredSize().height);
        Insets in        = area.getInsets();
        int contentH     = (lineH * rows) + (in != null ? in.top + in.bottom : 0);
        area.setMinimumSize(new Dimension(100, contentH));
        Dimension spPref = new Dimension(200, contentH + 4);
        scroll.setMinimumSize(spPref);
        scroll.setPreferredSize(spPref);
    }

    // ── Autosave listeners ────────────────────────────────────────────────────
    private void attachAutosaveListeners() {
        textAreaQuestion.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) questionModel.setQuestion(textAreaQuestion.getText());
            }
        });
        for (int i = 0; i < ANSWER_COUNT; i++) {
            final int idx = i;
            answerAreas[i].addFocusListener(new FocusAdapter() {
                @Override public void focusLost(FocusEvent e) {
                    if (questionModel != null) questionModel.setAnswer(idx, answerAreas[idx].getText());
                }
            });
        }
    }

    private void attachCorrectAnswerBinding() {
        int selected = (questionModel != null) ? questionModel.getCorrectAnswer() : 0;
        suppressCheckboxEvents = true;
        for (int i = 0; i < ANSWER_COUNT; i++) correctCheckBoxes[i].setSelected(selected == i + 1);
        suppressCheckboxEvents = false;
        for (int i = 0; i < ANSWER_COUNT; i++) {
            final int idx = i;
            correctCheckBoxes[i].addActionListener(e -> handleCheckboxChange(idx));
        }
    }

    private void handleCheckboxChange(int index) {
        if (suppressCheckboxEvents) return;
        suppressCheckboxEvents = true;
        if (correctCheckBoxes[index].isSelected()) {
            for (int i = 0; i < ANSWER_COUNT; i++) {
                if (i != index) correctCheckBoxes[i].setSelected(false);
            }
            if (questionModel != null) questionModel.setCorrectAnswer(index + 1);
        } else {
            if (questionModel != null) questionModel.setCorrectAnswer(0);
        }
        suppressCheckboxEvents = false;
    }

    private void populateFromModel() {
        if (questionModel == null) return;
        String qText = questionModel.getQuestion();
        if (qText != null) textAreaQuestion.setText(qText);
        java.util.List<String> answers = questionModel.getAnswers();
        if (answers == null) return;
        for (int i = 0; i < ANSWER_COUNT && i < answers.size(); i++) {
            String a = answers.get(i);
            if (a != null) answerAreas[i].setText(a);
        }
    }

    // ── Public helpers ────────────────────────────────────────────────────────
    public boolean hasAnyContent() {
        if (textAreaQuestion.getText() != null && !textAreaQuestion.getText().isBlank()) return true;
        for (JTextArea area : answerAreas) {
            if (area.getText() != null && !area.getText().isBlank()) return true;
        }
        return false;
    }

    public boolean hasCorrectAnswerSelected() {
        for (JCheckBox cb : correctCheckBoxes) {
            if (cb.isSelected()) return true;
        }
        return false;
    }

    public void saveAll() {
        if (questionModel == null) return;
        questionModel.setQuestion(textAreaQuestion.getText());
        for (int i = 0; i < ANSWER_COUNT; i++) {
            questionModel.setAnswer(i, answerAreas[i].getText());
        }
    }
}
