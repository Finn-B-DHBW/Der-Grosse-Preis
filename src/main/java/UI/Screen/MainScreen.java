package UI.Screen;

import Manager.GameManager;
import UI.JButtonWithCustomAttribute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class MainScreen {

    public void showMainScreen(GameManager gm){
        //Main screen that will display every theme and the different question points
        gm.cleanScreen();
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

        //Top Panel
        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMain.add(title);

        //Center Panel
        JPanel centerPanelMain = setUpCenterJPanel(gm);
        panelMain.add(centerPanelMain);

        //Bottom Panel
        JPanel bottomPanelMain = new JPanel(new GridLayout(0,2));

        bottomPanelMain.add(new JLabel("Player '"+ gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName() +"' turn"));

        //Testing JTable
        System.out.println((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/4);
        bottomPanelMain.setPreferredSize(new Dimension(Integer.MAX_VALUE, y));
        bottomPanelMain.setMaximumSize(new Dimension(Integer.MAX_VALUE, y));
        String[] columnHeader = {"Player", "Points"};

        Object[][] data = new Object[gm.getPlayers().size()][2];
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            data[i][0] = gm.getPlayers().get(i).getName();
            data[i][1] = gm.getPlayers().get(i).getScore();
        }
        JTable currentRanking = new JTable(data, columnHeader);
        JScrollPane scrollPane = new JScrollPane(currentRanking);
        bottomPanelMain.add(scrollPane);
        panelMain.add(bottomPanelMain);

        gm.add(panelMain);
        gm.setVisible(true);
    }

    private JPanel setUpCenterJPanel(GameManager gm) {
        JPanel centerPanelMain = new JPanel(new GridLayout(6, 5, 10, 10));
        centerPanelMain.setBorder(new EmptyBorder(10, 10, 10,10));
        centerPanelMain.setBackground(Color.GREEN);

        for (String s : gm.getCategoryList()) {
            JLabel category = new JLabel(s, SwingConstants.CENTER);
            category.setBackground(Color.DARK_GRAY);
            category.getFont();
            centerPanelMain.add(category);
        }

        int questionIdCount;

        for (int i = 1; i < 6; i++) {
            questionIdCount = i;
            for (int j = 1; j < 6; j++) {
                JButtonWithCustomAttribute button = new JButtonWithCustomAttribute(questionIdCount);
                button.setText(i*10+" Points");
                //If a question is selected, show the QuestionScreen with the question
                button.addActionListener(e -> gm.getQuestionScreen().showQuestionScreen(gm, Objects.requireNonNull(gm.getQuestions().stream()
                        .filter(q -> q.getQuestionId() == button.getQuestionId())
                        .findFirst()
                        .orElse(null)))
                );
                //disable the Button for the question that has been already used
                if(!gm.getAnsweredQuestionList().isEmpty() &&
                        gm.getAnsweredQuestionList().stream()
                        .filter(q-> q.getQuestionId() == button.getQuestionId())
                        .findFirst()
                        .orElse(null) != null) {
                    button.setEnabled(false);
                }
                centerPanelMain.add(button);
                questionIdCount+= 5;
            }
        }
        return centerPanelMain;
    }
}
