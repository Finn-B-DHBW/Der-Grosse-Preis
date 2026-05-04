package UI.Screen;

import Manager.GameManager;
import UI.JButtonWithCustomAttribute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class MainScreen {

    public void showMainScreen(GameManager gameManager) {
        //Main screen that will display every theme and the different question points
        gameManager.cleanScreen();
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.GREEN);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //Top Panel
        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        //Center Panel
        JPanel centerPanel = setUpCenterJPanel(gameManager);
        mainPanel.add(centerPanel);

        JPanel bottomPanel = setUpBottomPanel(gameManager);
        mainPanel.add(bottomPanel);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    private JPanel setUpBottomPanel(GameManager gameManager) {
        //Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(0, 2));

        bottomPanel.add(new JLabel("Player '" + gameManager.getPlayerList().get(gameManager.getCurrentPlayerIndex()).getName() + "' turn"));

        //Testing JTable
        System.out.println((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2));
        int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 4);
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, y));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, y));
        String[] columnHeader = {"Player", "Points"};

        Object[][] data = new Object[gameManager.getPlayerList().size()][2];
        for (int i = 0; i < gameManager.getPlayerList().size(); i++) {
            data[i][0] = gameManager.getPlayerList().get(i).getName();
            data[i][1] = gameManager.getPlayerList().get(i).getScore();
        }
        JTable currentRanking = new JTable(data, columnHeader);
        JScrollPane scrollPane = new JScrollPane(currentRanking);

        bottomPanel.add(scrollPane);
        return bottomPanel;
    }

    private JPanel setUpCenterJPanel(GameManager gameManager) {
        JPanel centerPanelMain = new JPanel(new GridLayout(6, 5, 10, 10));
        centerPanelMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanelMain.setBackground(Color.GREEN);

        for (String s : gameManager.getCategoryList()) {
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
                button.setText(i * 10 + " Points");
                //If a question is selected, show the QuestionScreen with the question
                button.addActionListener(e -> gameManager.getQuestionScreen().showQuestionScreen(gameManager, Objects.requireNonNull(gameManager.getQuestionList().stream()
                        .filter(q -> q.getQuestionId() == button.getQuestionId())
                        .findFirst()
                        .orElse(null)))
                );
                //disable the Button for the question that has been already used
                if (!gameManager.getAnsweredQuestionList().isEmpty() &&
                        gameManager.getAnsweredQuestionList().stream()
                                .filter(q -> q.getQuestionId() == button.getQuestionId())
                                .findFirst()
                                .orElse(null) != null) {
                    button.setEnabled(false);
                }
                centerPanelMain.add(button);
                questionIdCount += 5;
            }
        }
        return centerPanelMain;
    }
}
