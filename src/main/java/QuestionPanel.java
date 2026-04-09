import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;

public class QuestionPanel extends JPanel {

    private Integer pointsValue;
    private ConfigQuestion questionModel;
    private boolean suppressCheckboxEvents = false;

    public JTextArea textAreaQuestion = new JTextArea(2, 20);

    public JTextArea textAreaAnswer1 = new JTextArea(2, 20);
    public JCheckBox checkBoxCorrectAnswer1 = new JCheckBox("");

    public JTextArea textAreaAnswer2 = new JTextArea(2, 20);
    public JCheckBox checkBoxCorrectAnswer2 = new JCheckBox("");

    public JTextArea textAreaAnswer3 = new JTextArea(2, 20);
    public JCheckBox checkBoxCorrectAnswer3 = new JCheckBox("");

    public QuestionPanel() {
        this(null, null);
    }

    public QuestionPanel(Integer points, ConfigQuestion question) {
        this.pointsValue = points;
        this.questionModel = question;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        String headerText = (pointsValue != null ? pointsValue + " Punkte" : "Punkte");
        JLabel header = new JLabel(headerText);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0;
        add(header, gbc);
        gbc.gridwidth = 1;

        for (JTextArea ta : new JTextArea[]{textAreaQuestion, textAreaAnswer1, textAreaAnswer2, textAreaAnswer3}) {
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
        }

        JLabel frageLabel = new JLabel("Frage");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(frageLabel, gbc);

        textAreaQuestion.setRows(2);
        JScrollPane qScroll = new JScrollPane(textAreaQuestion,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enforceTwoRowViewport(textAreaQuestion, qScroll);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.gridwidth = 1;
        add(qScroll, gbc);
        JLabel correctLabelHeader = new JLabel("richtig?");
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(correctLabelHeader, gbc);
        gbc.gridwidth = 1;

        JLabel a1Label = new JLabel("Antwort 1");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(a1Label, gbc);

        textAreaAnswer1.setRows(2);
        JScrollPane a1Scroll = new JScrollPane(textAreaAnswer1,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enforceTwoRowViewport(textAreaAnswer1, a1Scroll);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(a1Scroll, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(checkBoxCorrectAnswer1, gbc);

        JLabel a2Label = new JLabel("Antwort 2");
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(a2Label, gbc);

        textAreaAnswer2.setRows(2);
        JScrollPane a2Scroll = new JScrollPane(textAreaAnswer2,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enforceTwoRowViewport(textAreaAnswer2, a2Scroll);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(a2Scroll, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(checkBoxCorrectAnswer2, gbc);

        JLabel a3Label = new JLabel("Antwort 3");
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(a3Label, gbc);

        textAreaAnswer3.setRows(2);
        JScrollPane a3Scroll = new JScrollPane(textAreaAnswer3,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enforceTwoRowViewport(textAreaAnswer3, a3Scroll);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(a3Scroll, gbc);

        gbc.gridx = 2; gbc.gridy = 4; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        add(checkBoxCorrectAnswer3, gbc);

        setBorder(BorderFactory.createLineBorder(Color.RED, 1));

        populateFromModel();
        attachAutosaveListeners();
        attachCorrectAnswerBinding();
    }

    private void attachAutosaveListeners() {
        textAreaQuestion.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) {
                    questionModel.setQuestion(textAreaQuestion.getText());
                }
            }
        });
        textAreaAnswer1.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) {
                    questionModel.setAnswer(0, textAreaAnswer1.getText());
                }
            }
        });
        textAreaAnswer2.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) {
                    questionModel.setAnswer(1, textAreaAnswer2.getText());
                }
            }
        });
        textAreaAnswer3.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (questionModel != null) {
                    questionModel.setAnswer(2, textAreaAnswer3.getText());
                }
            }
        });
    }

    private void attachCorrectAnswerBinding() {
        int idx = (questionModel != null) ? questionModel.getCorrectAnswer() : 0;
        suppressCheckboxEvents = true;
        checkBoxCorrectAnswer1.setSelected(idx == 1);
        checkBoxCorrectAnswer2.setSelected(idx == 2);
        checkBoxCorrectAnswer3.setSelected(idx == 3);
        suppressCheckboxEvents = false;

        ActionListener l1 = e -> handleCheckboxChange(1);
        ActionListener l2 = e -> handleCheckboxChange(2);
        ActionListener l3 = e -> handleCheckboxChange(3);
        checkBoxCorrectAnswer1.addActionListener(l1);
        checkBoxCorrectAnswer2.addActionListener(l2);
        checkBoxCorrectAnswer3.addActionListener(l3);
    }

    private void populateFromModel() {
        if (questionModel == null) return;
        String qText = questionModel.getQuestion();
        if (qText != null) textAreaQuestion.setText(qText);
        java.util.List<String> answers = questionModel.getAnswers();
        if (answers != null) {
            if (answers.size() > 0 && answers.get(0) != null) textAreaAnswer1.setText(answers.get(0));
            if (answers.size() > 1 && answers.get(1) != null) textAreaAnswer2.setText(answers.get(1));
            if (answers.size() > 2 && answers.get(2) != null) textAreaAnswer3.setText(answers.get(2));
        }
    }

    private void handleCheckboxChange(int which) {
        if (suppressCheckboxEvents) return;
        suppressCheckboxEvents = true;
        JCheckBox cb1 = checkBoxCorrectAnswer1;
        JCheckBox cb2 = checkBoxCorrectAnswer2;
        JCheckBox cb3 = checkBoxCorrectAnswer3;
        if (which == 1) {
            if (cb1.isSelected()) {
                cb2.setSelected(false);
                cb3.setSelected(false);
                if (questionModel != null) questionModel.setCorrectAnswer(1);
            } else {
                if (questionModel != null) questionModel.setCorrectAnswer(0);
            }
        } else if (which == 2) {
            if (cb2.isSelected()) {
                cb1.setSelected(false);
                cb3.setSelected(false);
                if (questionModel != null) questionModel.setCorrectAnswer(2);
            } else {
                if (questionModel != null) questionModel.setCorrectAnswer(0);
            }
        } else if (which == 3) {
            if (cb3.isSelected()) {
                cb1.setSelected(false);
                cb2.setSelected(false);
                if (questionModel != null) questionModel.setCorrectAnswer(3);
            } else {
                if (questionModel != null) questionModel.setCorrectAnswer(0);
            }
        }
        suppressCheckboxEvents = false;
    }

    private static void enforceTwoRowViewport(JTextArea area, JScrollPane scroll) {
        if (area == null || scroll == null) return;
        int rows = Math.max(2, area.getRows());
        area.setRows(rows);
        FontMetrics fm = area.getFontMetrics(area.getFont());
        int lineH = (fm != null) ? fm.getHeight() : Math.max(12, area.getPreferredSize().height);
        Insets in = area.getInsets();
        int contentH = (lineH * rows) + (in != null ? in.top + in.bottom : 0);
        Dimension taMin = new Dimension(100, contentH);
        area.setMinimumSize(taMin);
        Dimension spPref = new Dimension(200, contentH + 4);
        scroll.setMinimumSize(spPref);
        scroll.setPreferredSize(spPref);
    }
}
