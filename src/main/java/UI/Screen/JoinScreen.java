package UI.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import Manager.GameManager;
import Model.Player;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import config.DatabaseConnector;
import config.model.Configuration;
import config.ui.MainController;
import server.NetworkUtils;

public class JoinScreen {

    private final DefaultListModel<String> playerListModel = new DefaultListModel<>();

    public void showJoinScreen(GameManager gameManager) {
        //QR-Code that lets people join through the browser
        JPanel joinPanel = new JPanel(new BorderLayout(5, 5));
        joinPanel.setBackground(Color.CYAN);

        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        joinPanel.add(title, BorderLayout.NORTH);

        joinPanel.add(setUpPlayerListPanel(), BorderLayout.WEST);

        joinPanel.add(setUpJoinInfoPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.CYAN);
        buttonPanel.add(setUpStartButton(gameManager));
        buttonPanel.add(setUpConfigureButton());
        buttonPanel.add(setUpLoadButton(gameManager));

        joinPanel.add(buttonPanel, BorderLayout.SOUTH);

        gameManager.add(joinPanel);
        gameManager.setVisible(true);
    }

    public void refreshPlayerList(List<Player> players) {
        playerListModel.clear();
        for (Player player : players) {
            playerListModel.addElement(player.getName());
        }
    }

    private JPanel setUpPlayerListPanel() {
        JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
        playerPanel.setBackground(Color.CYAN);

        JLabel playerListTitle = new JLabel("Verbundene Spieler:");
        playerListTitle.setFont(new Font("Arial", Font.BOLD, 24));
        playerPanel.add(playerListTitle, BorderLayout.NORTH);

        JList<String> playerListView = new JList<>(playerListModel);
        playerListView.setFont(new Font("Arial", Font.PLAIN, 20));
        JScrollPane scrollPane = new JScrollPane(playerListView);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        playerPanel.add(scrollPane, BorderLayout.CENTER);

        return playerPanel;
    }

    private JPanel setUpJoinInfoPanel() {
        String url = "http://" + NetworkUtils.getLocalIpAddress() + ":8080";

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.CYAN);

        JLabel joinLabel = new JLabel("Zum Beitreten im Browser oeffnen:");
        joinLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        joinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(joinLabel);

        JLabel urlLabel = new JLabel(url);
        urlLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(urlLabel);
        infoPanel.add(Box.createVerticalStrut(20));

        BufferedImage qrImage = generateQrCode(url, 300, 300);
        if (qrImage != null) {
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
            qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(qrLabel);
        }
        infoPanel.add(Box.createVerticalGlue());

        return infoPanel;
    }

    private BufferedImage generateQrCode(String text, int width, int height) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            return null;
        }
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
