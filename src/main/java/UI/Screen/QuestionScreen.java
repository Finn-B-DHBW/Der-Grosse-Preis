package UI.Screen;

import Manager.GameManager;
import Model.Question;
import server.dto.AnswerResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuestionScreen {

    private final JPanel resultsPanel = new JPanel();

    public void showQuestionScreen(GameManager gameManager, Question question) {
        //Screen that will display the selected question and the different answer possibilities
        gameManager.cleanScreen();

        // Frage an alle verbundenen Browser-Spieler schicken
        gameManager.broadcastQuestion(question);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.GREEN);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel questionPanel = new JPanel();
        questionPanel.setBackground(Color.GREEN);
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        JLabel questionLabel = new JLabel(question.getQuestionText());
        questionLabel.setPreferredSize(new Dimension(1000, 200));
        questionLabel.setBackground(Color.DARK_GRAY);
        questionLabel.setBorder(new LineBorder(Color.RED, 2));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setVerticalAlignment(SwingConstants.CENTER);
        questionPanel.add(questionLabel);

        JPanel questionButtonsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JButton[] answerButtonList = setUpAnswerButtons(questionButtonsPanel, question);
        questionPanel.add(questionButtonsPanel);

        questionPanel.add(setUpCloseQuestionButton(gameManager, question, answerButtonList));

        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(setUpResultsPanel(), BorderLayout.EAST);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    /**
     * Wird vom GameManager (auf dem EDT) aufgerufen, sobald ein Spieler
     * ueber den Browser geantwortet hat.
     */
    public void updateResultsPanel(AnswerResult result) {
        String symbol = result.isCorrect() ? "✓" : "✗";
        JLabel entry = new JLabel(result.getPlayerName() + ": " + result.getSelectedAnswer() + " " + symbol);
        entry.setFont(new Font("Arial", Font.PLAIN, 20));
        entry.setForeground(result.isCorrect() ? new Color(0, 128, 0) : Color.RED);
        resultsPanel.add(entry);
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JScrollPane setUpResultsPanel() {
        resultsPanel.removeAll();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        JLabel resultsTitle = new JLabel("Spieler-Antworten:");
        resultsTitle.setFont(new Font("Arial", Font.BOLD, 24));
        resultsPanel.add(resultsTitle);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        return scrollPane;
    }

    private JButton[] setUpAnswerButtons(JPanel questionButtonsPanel, Question question) {
        List<String> answerList = new ArrayList<>();
        if (question.getWrongAnswers() != null) {
            answerList.addAll(Arrays.asList(question.getWrongAnswers()));
        }
        answerList.add(question.getRightAnswer());
        Collections.shuffle(answerList);

        JButton[] answerButtonList = new JButton[answerList.size()];
        for (int i = 0; i < answerButtonList.length; i++) {
            answerButtonList[i] = new JButton(answerList.get(i));
            questionButtonsPanel.add(answerButtonList[i]);
        }
        return answerButtonList;
    }

    private JButton setUpCloseQuestionButton(GameManager gameManager, Question question, JButton[] answerButtonList) {
        JButton closeButton = new JButton("Frage schließen");
        closeButton.setPreferredSize(new Dimension(300, 60));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        AtomicBoolean isPressedOnce = new AtomicBoolean(false);
        closeButton.addActionListener(e -> {
            if (!isPressedOnce.get()) {
                // Erster Klick: richtige Antwort aufdecken und Frage fuer
                // die Browser-Spieler schliessen
                revealButtonColor(answerButtonList, question.getRightAnswer());
                gameManager.broadcastQuestionClosed(question);
                gameManager.getAnsweredQuestionList().add(question);
                closeButton.setText("Zurück zur Übersicht");
                isPressedOnce.set(true);
            } else {
                switchPlayer(gameManager);
                gameManager.getMainScreen().showMainScreen(gameManager);
            }
        });
        return closeButton;
    }

    private void revealButtonColor(JButton[] buttons, String rightAnswer) {
        for (JButton button : buttons) {
            if (button.getText().equals(rightAnswer)) {
                button.setBackground(Color.blue);
            } else {
                button.setBackground(Color.red);
            }
        }
    }

    private void switchPlayer(GameManager gameManager) {
        if (gameManager.getCurrentPlayerIndex() >= gameManager.getPlayerList().size() - 1) {
            gameManager.setCurrentPlayerIndex(0);
        } else {
            gameManager.setCurrentPlayerIndex(gameManager.getCurrentPlayerIndex() + 1);
        }
    }

}
