package UI.Screen;

import Manager.GameManager;
import Model.Question;
import server.dto.AnswerResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuestionScreen {

    private final JPanel resultsPanel = new JPanel();
    private final List<AnswerResult> pendingResults = new ArrayList<>();
    private final Map<String, JLabel> symbolLabels = new LinkedHashMap<>();

    private static final Color BG          = new Color(15, 20, 40);
    private static final Color CARD        = new Color(25, 35, 60);
    private static final Color ACCENT      = new Color(52, 152, 219);
    private static final Color ANSWER_BG   = new Color(30, 45, 80);
    private static final Color SIDEBAR_BG  = new Color(20, 28, 50);
    private static final Color CORRECT     = new Color(46, 213, 115);
    private static final Color WRONG       = new Color(220, 60, 60);
    private static final Color PENDING     = new Color(180, 180, 220);

    public void showQuestionScreen(GameManager gameManager, Question question) {
        gameManager.cleanScreen();
        gameManager.broadcastQuestion(question);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // Center column: question card + answer grid + close button
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG);
        centerPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        centerPanel.add(buildQuestionCard(question.getQuestionText()), BorderLayout.NORTH);

        JPanel answersGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        answersGrid.setBackground(BG);
        JButton[] answerButtons = setUpAnswerButtons(answersGrid, question);
        centerPanel.add(answersGrid, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomPanel.setBackground(BG);
        bottomPanel.add(setUpCloseQuestionButton(gameManager, question, answerButtons));
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buildSidebarScrollPane(), BorderLayout.EAST);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    public void updateResultsPanel(AnswerResult result) {
        pendingResults.add(result);

        JPanel entryPanel = new JPanel(new BorderLayout(8, 0));
        entryPanel.setBackground(new Color(30, 40, 65));
        entryPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        entryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(result.getPlayerName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        nameLabel.setForeground(Color.WHITE);

        JLabel symbolLabel = new JLabel("●");
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 16));
        symbolLabel.setForeground(PENDING);
        symbolLabel.setBorder(new EmptyBorder(0, 6, 0, 0));

        symbolLabels.put(result.getPlayerName(), symbolLabel);

        entryPanel.add(nameLabel, BorderLayout.CENTER);
        entryPanel.add(symbolLabel, BorderLayout.EAST);

        resultsPanel.add(entryPanel);
        resultsPanel.add(Box.createVerticalStrut(4));
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JScrollPane buildSidebarScrollPane() {
        resultsPanel.removeAll();
        pendingResults.clear();
        symbolLabels.clear();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(SIDEBAR_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new EmptyBorder(14, 16, 14, 16));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Spieler-Antworten");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        resultsPanel.add(header);
        resultsPanel.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setPreferredSize(new Dimension(280, 0));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(SIDEBAR_BG);
        return scroll;
    }

    private JPanel buildQuestionCard(String text) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 160));
        card.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel label = new JLabel("<html><div style='text-align:center'>" + text + "</div></html>");
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        card.add(label, BorderLayout.CENTER);

        return card;
    }

    private JButton[] setUpAnswerButtons(JPanel grid, Question question) {
        List<String> answers = new ArrayList<>();
        if (question.getWrongAnswers() != null) {
            answers.addAll(Arrays.asList(question.getWrongAnswers()));
        }
        answers.add(question.getRightAnswer());
        Collections.shuffle(answers);

        JButton[] buttons = new JButton[answers.size()];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = buildAnswerButton(answers.get(i));
            grid.add(buttons[i]);
        }
        return buttons;
    }

    private JButton buildAnswerButton(String text) {
        JButton btn = new JButton("<html><center>" + text + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                Color bg = getBackground();
                g2.setColor(bg.equals(ANSWER_BG) ? ACCENT : bg.darker());
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("answer", text);
        btn.setBackground(ANSWER_BG);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton setUpCloseQuestionButton(GameManager gameManager, Question question, JButton[] answerButtons) {
        JButton btn = new JButton("Antwort anzeigen") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(260, 50));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 17));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        AtomicBoolean pressedOnce = new AtomicBoolean(false);
        btn.addActionListener(e -> {
            if (!pressedOnce.get()) {
                revealAnswers(answerButtons, question.getRightAnswer());
                gameManager.broadcastQuestionClosed(question);
                gameManager.getAnsweredQuestionList().add(question);
                btn.setText("Zurück zur Übersicht");
                pressedOnce.set(true);
            } else {
                switchPlayer(gameManager);
                gameManager.getMainScreen().showMainScreen(gameManager);
            }
        });
        return btn;
    }

    private void revealAnswers(JButton[] buttons, String rightAnswer) {
        for (JButton btn : buttons) {
            String answer = (String) btn.getClientProperty("answer");
            btn.setBackground(rightAnswer.equals(answer) ? CORRECT : WRONG);
            btn.repaint();
        }
        for (AnswerResult result : pendingResults) {
            JLabel lbl = symbolLabels.get(result.getPlayerName());
            if (lbl != null) {
                lbl.setText(result.isCorrect() ? "✓" : "✗");
                lbl.setForeground(result.isCorrect() ? CORRECT : WRONG);
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void switchPlayer(GameManager gameManager) {
        if (gameManager.getCurrentPlayerIndex() >= gameManager.getPlayerList().size() - 1) {
            gameManager.setCurrentPlayerIndex(0);
        } else {
            gameManager.setCurrentPlayerIndex(gameManager.getCurrentPlayerIndex() + 1);
        }
    }
}
