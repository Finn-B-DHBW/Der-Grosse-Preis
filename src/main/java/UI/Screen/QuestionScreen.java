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

    public void showQuestionScreen(GameManager gm, Question question) {
        //Screen that will display the selected question and the different answer possibilities
        gm.cleanScreen();
        //Test Segment to see if the right question is being displayed
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
        panelMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelMain.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelMain.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLabel = new JLabel(question.getQuestionText());
        questionLabel.setPreferredSize(new Dimension(1000, 200));
        questionLabel.setBackground(Color.DARK_GRAY);
        questionLabel.setBorder(new LineBorder(Color.RED, 2));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setVerticalAlignment(SwingConstants.CENTER);
        panelMain.add(questionLabel);

        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        setUpAnswerButtons(p, question, gm);

        panelMain.add(p);

        gm.add(panelMain);
        gm.setVisible(true);
    }

    private void setUpAnswerButtons(JPanel panel, Question question, GameManager gm) {
        String rightAnswer = question.getRightAnswer();
        List<String> answers = new ArrayList<>(Arrays.asList(question.getWrongAnswers()));
        answers.add(rightAnswer);
        Collections.shuffle(answers);

        AtomicBoolean isPressedOnce = new AtomicBoolean(false);
        JButton[] answerButtons = new JButton[4];

        for(int i = 0; i< answerButtons.length; i++){
            answerButtons[i] = new JButton(answers.get(i));
        }

        for (JButton button : answerButtons) {
            button.addActionListener(e -> {

                if (button.getText().equals(rightAnswer) && !isPressedOnce.get()) {
                    gm.getPlayers().get(gm.getCurrentPlayerIndex()).addRightAnswerToList(question);
                }
                revealButtonColor(answerButtons, rightAnswer);

                if (isPressedOnce.get()) {
                    switchPlayer(gm);
                    gm.getAnsweredQuestionList().add(question);
                    gm.getMainScreen().showMainScreen(gm);
                }

                isPressedOnce.set(true);
            });
            panel.add(button);
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

    private void switchPlayer(GameManager gm) {
        if(gm.getCurrentPlayerIndex() == gm.getPlayers().size()-1) {
            gm.setCurrentPlayerIndex(0);
        } else {
            gm.setCurrentPlayerIndex(gm.getCurrentPlayerIndex()+1);
        }
    }

}
