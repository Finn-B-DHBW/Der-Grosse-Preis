package UI.Screen;

import Manager.GameManager;
import Model.Question;

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

    public void showQuestionScreen(GameManager gameManager, Question question) {
        //Screen that will display the selected question and the different answer possibilities
        gameManager.cleanScreen();
        //Test Segment to see if the right question is being displayed
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.GREEN);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLabel = new JLabel(question.getQuestionText());
        questionLabel.setPreferredSize(new Dimension(1000, 200));
        questionLabel.setBackground(Color.DARK_GRAY);
        questionLabel.setBorder(new LineBorder(Color.RED, 2));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setVerticalAlignment(SwingConstants.CENTER);
        mainPanel.add(questionLabel);

        JPanel questionButtonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        setUpAnswerButtons(questionButtonsPanel, question, gameManager);

        mainPanel.add(questionButtonsPanel);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    private void setUpAnswerButtons(JPanel questionButtonsPanel, Question question, GameManager gameManager) {
        String rightAnswer = question.getRightAnswer();
        List<String> answerList = new ArrayList<>(Arrays.asList(question.getWrongAnswers()));
        answerList.add(rightAnswer);
        Collections.shuffle(answerList);

        AtomicBoolean isPressedOnce = new AtomicBoolean(false);
        JButton[] answerButtonList = new JButton[4];

        for(int i = 0; i< answerButtonList.length; i++){
            answerButtonList[i] = new JButton(answerList.get(i));
        }

        for (JButton answerButton : answerButtonList) {
            answerButton.addActionListener(e -> {

                if (answerButton.getText().equals(rightAnswer) && !isPressedOnce.get()) {
                    gameManager.getPlayerList().get(gameManager.getCurrentPlayerIndex()).addRightAnswerToList(question);
                }
                revealButtonColor(answerButtonList, rightAnswer);

                if (isPressedOnce.get()) {
                    switchPlayer(gameManager);
                    gameManager.getAnsweredQuestionList().add(question);
                    gameManager.getMainScreen().showMainScreen(gameManager);
                }

                isPressedOnce.set(true);
            });
            questionButtonsPanel.add(answerButton);
        }
    }

    private void revealButtonColor(JButton[] buttons, String rightAnswer) {
        for (JButton button : buttons) {
            if (button.getText().equals(rightAnswer)) {
                button.setBackground(Color.blue);
            }else {
                button.setBackground(Color.red);
            }
        }
    }

    private void switchPlayer(GameManager gameManager) {
        if(gameManager.getCurrentPlayerIndex() == gameManager.getPlayerList().size()-1) {
            gameManager.setCurrentPlayerIndex(0);
        } else {
            gameManager.setCurrentPlayerIndex(gameManager.getCurrentPlayerIndex()+1);
        }
    }

}
