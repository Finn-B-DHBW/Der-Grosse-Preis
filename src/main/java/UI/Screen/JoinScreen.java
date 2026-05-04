package UI.Screen;

import javax.swing.*;
import java.awt.*;

import Manager.GameManager;
import Model.Player;

public class JoinScreen {

    public void showJoinScreen(GameManager gameManager) {
        //QR-Code that lets people join through the browser
        JPanel joinPanel = new JPanel(new BorderLayout(5, 5));
        joinPanel.setBackground(Color.CYAN);

        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        joinPanel.add(title, BorderLayout.NORTH);

        //joinPanel.add(list of people, BorderLayout.WEST);

        //joinPanel.add(QR Code, BorderLayout.EAST);

        JButton startButton = setUpStartButton(gameManager);
        joinPanel.add(startButton, BorderLayout.SOUTH);

        gameManager.add(joinPanel);
        gameManager.setVisible(true);
    }

    private JButton setUpStartButton(GameManager gameManager) {
        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(500, 100));
        startButton.addActionListener(e -> {
            if (gameManager.getPlayerList().isEmpty()){
                gameManager.getPlayerList().add(new Player("Test Player"));
                gameManager.getPlayerList().add(new Player("Test Player number 2"));
            }
            gameManager.setQuestions();
            gameManager.setCurrentPlayerIndex(0);
            gameManager.getMainScreen().showMainScreen(gameManager);
        });
        return startButton;
    }
}
