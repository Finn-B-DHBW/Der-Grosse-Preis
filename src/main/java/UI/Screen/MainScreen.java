package UI.Screen;

import Manager.GameManager;
import Model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        String[] categories = gameManager.getCategoryList();
        List<Integer> pointValues = getPointValues(gameManager);

        JPanel centerPanelMain = new JPanel(new GridLayout(pointValues.size() + 1, categories.length, 10, 10));
        centerPanelMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanelMain.setBackground(Color.GREEN);

        for (String s : categories) {
            JLabel category = new JLabel(s, SwingConstants.CENTER);
            category.setBackground(Color.DARK_GRAY);
            category.getFont();
            centerPanelMain.add(category);
        }

        for (Integer points : pointValues) {
            for (String category : categories) {
                Question question = findQuestion(gameManager, category, points);
                JButton button = new JButton(points + " Points");
                if (question == null) {
                    button.setEnabled(false);
                } else {
                    button.addActionListener(e -> gameManager.getQuestionScreen().showQuestionScreen(gameManager, question));
                }
                if (question != null && isAnswered(gameManager, question)) {
                    button.setEnabled(false);
                }
                centerPanelMain.add(button);
            }
        }
        return centerPanelMain;
    }

    private List<Integer> getPointValues(GameManager gameManager) {
        TreeSet<Integer> pointValues = new TreeSet<>();
        if (gameManager.getQuestionList() != null) {
            for (Question question : gameManager.getQuestionList()) {
                pointValues.add(question.getScore());
            }
        }
        return new ArrayList<>(pointValues);
    }

    private Question findQuestion(GameManager gameManager, String category, int points) {
        if (gameManager.getQuestionList() == null) {
            return null;
        }

        for (Question question : gameManager.getQuestionList()) {
            if (question.getCategory().equals(category) && question.getScore() == points) {
                return question;
            }
        }
        return null;
    }

    private boolean isAnswered(GameManager gameManager, Question question) {
        return gameManager.getAnsweredQuestionList().stream()
                .anyMatch(answeredQuestion -> answeredQuestion.getQuestionId() == question.getQuestionId());
    }
}
