package config.ui;

import config.model.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class QuestionPanel extends JPanel {

    private static final int ANSWER_COUNT = 3;
    private static final int TEXT_ROWS = 2;
    private static final int TEXT_COLS = 20;

    private final Integer pointsValue;
    private final ConfigQuestion questionModel;
    private boolean suppressCheckboxEvents = false;

    private final JTextArea textAreaQuestion = new JTextArea(TEXT_ROWS, TEXT_COLS);
    private final JTextArea[] answerAreas = new JTextArea[ANSWER_COUNT];
    private final JCheckBox[] correctCheckBoxes = new JCheckBox[ANSWER_COUNT];

    public QuestionPanel() {
        this(null, null);
    }

    public QuestionPanel(Integer points, ConfigQuestion question) {
        this.pointsValue = points;
        this.questionModel = question;
        for (int i = 0; i < ANSWER_COUNT; i++) {
            answerAreas[i] = new JTextArea(TEXT_ROWS, TEXT_COLS);
            correctCheckBoxes[i] = new JCheckBox("");
        }

        buildLayout();
        populateFromModel();
        attachAutosaveListeners();
        attachCorrectAnswerBinding();
    }

    private void buildLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        addHeader(gbc);
        enableLineWrap(textAreaQuestion);
        for (JTextArea area : answerAreas) enableLineWrap(area);

        addLabeledTextRow("Frage", textAreaQuestion, 1, gbc, "richtig?");
        for (int i = 0; i < ANSWER_COUNT; i++) {
            addAnswerRow(i, gbc);
        }

        setBorder(BorderFactory.createLineBorder(Color.RED, 1));
    }

    private void addHeader(GridBagConstraints gbc) {
        String headerText = (pointsValue != null ? pointsValue + " Punkte" : "Punkte");
        JLabel header = new JLabel(headerText);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0;
        add(header, gbc);
        gbc.gridwidth = 1;
    }

    private void addLabeledTextRow(String labelText, JTextArea area, int row,
                                    GridBagConstraints gbc, String trailingHeader) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbc);

        JScrollPane scroll = wrapInScrollPane(area);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(scroll, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(trailingHeader), gbc);
    }

    private void addAnswerRow(int index, GridBagConstraints gbc) {
        int row = 2 + index;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Antwort " + (index + 1)), gbc);

        JScrollPane scroll = wrapInScrollPane(answerAreas[index]);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(scroll, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(correctCheckBoxes[index], gbc);
    }

    private JScrollPane wrapInScrollPane(JTextArea area) {
        area.setRows(TEXT_ROWS);
        JScrollPane scroll = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enforceTwoRowViewport(area, scroll);
        return scroll;
    }

    private static void enableLineWrap(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private void attachAutosaveListeners() {
        textAreaQuestion.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) {
                    questionModel.setQuestion(textAreaQuestion.getText());
                }
            }
        });
        for (int i = 0; i < ANSWER_COUNT; i++) {
            final int index = i;
            answerAreas[i].addFocusListener(new FocusAdapter() {
                @Override public void focusLost(FocusEvent e) {
                    if (questionModel != null) {
                        questionModel.setAnswer(index, answerAreas[index].getText());
                    }
                }
            });
        }
    }

    private void attachCorrectAnswerBinding() {
        int selected = (questionModel != null) ? questionModel.getCorrectAnswer() : 0;
        suppressCheckboxEvents = true;
        for (int i = 0; i < ANSWER_COUNT; i++) {
            correctCheckBoxes[i].setSelected(selected == i + 1);
        }
        suppressCheckboxEvents = false;

        for (int i = 0; i < ANSWER_COUNT; i++) {
            final int index = i;
            correctCheckBoxes[i].addActionListener(e -> handleCheckboxChange(index));
        }
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

    private static void enforceTwoRowViewport(JTextArea area, JScrollPane scroll) {
        if (area == null || scroll == null) return;
        int rows = Math.max(TEXT_ROWS, area.getRows());
        area.setRows(rows);
        FontMetrics fm = area.getFontMetrics(area.getFont());
        int lineH = (fm != null) ? fm.getHeight() : Math.max(12, area.getPreferredSize().height);
        Insets in = area.getInsets();
        int contentH = (lineH * rows) + (in != null ? in.top + in.bottom : 0);
        area.setMinimumSize(new Dimension(100, contentH));
        Dimension spPref = new Dimension(200, contentH + 4);
        scroll.setMinimumSize(spPref);
        scroll.setPreferredSize(spPref);
    }
}
