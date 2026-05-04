package UI.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import Manager.GameManager;
import Model.Player;
import config.DatabaseConnector;
import config.model.Configuration;
import config.ui.MainController;

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.CYAN);
        buttonPanel.add(setUpStartButton(gameManager));
        buttonPanel.add(setUpConfigureButton());
        buttonPanel.add(setUpLoadButton(gameManager));

        joinPanel.add(buttonPanel, BorderLayout.SOUTH);

        gameManager.add(joinPanel);
        gameManager.setVisible(true);
    }

    private JButton setUpStartButton(GameManager gameManager) {
        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(300, 80));
        startButton.addActionListener(e -> {
            preparePlayers(gameManager);
            gameManager.setQuestions();
            gameManager.setCurrentPlayerIndex(0);
            gameManager.getMainScreen().showMainScreen(gameManager);
        });
        return startButton;
    }

    private JButton setUpConfigureButton() {
        JButton configureButton = new JButton("Configure Game");
        configureButton.setPreferredSize(new Dimension(300, 80));
        configureButton.addActionListener(e -> new MainController());
        return configureButton;
    }

    private JButton setUpLoadButton(GameManager gameManager) {
        JButton loadButton = new JButton("Load Game");
        loadButton.setPreferredSize(new Dimension(300, 80));
        loadButton.addActionListener(e -> {
            List<Configuration> configurations = DatabaseConnector.loadConfigurations();
            if (configurations.isEmpty()) {
                JOptionPane.showMessageDialog(gameManager,
                        "Keine gespeicherten Konfigurationen gefunden.",
                        "Spiel laden",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Configuration selectedConfiguration = selectConfiguration(gameManager, configurations);
            if (selectedConfiguration == null) {
                return;
            }

            preparePlayers(gameManager);
            gameManager.loadConfiguration(selectedConfiguration);
            gameManager.setCurrentPlayerIndex(0);
            gameManager.getMainScreen().showMainScreen(gameManager);
        });
        return loadButton;
    }

    private Configuration selectConfiguration(GameManager gameManager, List<Configuration> configurations) {
        if (configurations.size() == 1) {
            return configurations.get(0);
        }

        String[] titles = getConfigurationTitles(configurations);
        Object selectedTitle = JOptionPane.showInputDialog(gameManager,
                "Konfiguration wählen:",
                "Spiel laden",
                JOptionPane.QUESTION_MESSAGE,
                null,
                titles,
                titles[0]);
        if (selectedTitle == null) {
            return null;
        }

        for (int i = 0; i < titles.length; i++) {
            if (titles[i].equals(selectedTitle)) {
                return configurations.get(i);
            }
        }
        return null;
    }

    private String[] getConfigurationTitles(List<Configuration> configurations) {
        String[] titles = new String[configurations.size()];
        for (int i = 0; i < configurations.size(); i++) {
            String title = configurations.get(i).getTitle();
            titles[i] = title == null || title.isBlank() ? "Konfiguration " + (i + 1) : title;
        }
        return titles;
    }

    private void preparePlayers(GameManager gameManager) {
        if (gameManager.getPlayerList().isEmpty()) {
            gameManager.getPlayerList().add(new Player("Test Player"));
            gameManager.getPlayerList().add(new Player("Test Player number 2"));
        }
    }
}
