package UI.Screen;

import Manager.GameManager;
import Model.Question;

import javax.swing.*;
import java.awt.*;

public class QuestionScreen {

    public void showQuestionScreen(GameManager gm, Question question) {
        //Screen that will display the selected question and the different answer possibilities
        gm.cleanScreen();
        //Test Segment to see if the right question is being displayed
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea(question.toString());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 40));

        panelMain.add(textArea);

        gm.add(panelMain);
        gm.setVisible(true);
    }
}
