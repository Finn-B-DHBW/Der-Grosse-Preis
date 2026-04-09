import config.DatabaseConnector;
import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;
import config.model.Team;
import config.ui.MainController;
import game.GameLogic;
import game.PointsManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameManager extends JFrame {
    private List<Player> players;
    private List<Question> questions;
    private SocketServer serverSocket;
    private DataBase db;
    private GameLogic gameLogic;

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.db = new DataBase(this);
        this.gameLogic = new GameLogic();

        this.setResizable(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true); maybe later add a option to use esc button to close
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        showJoinScreen();
    }

    private void cleanScreen() {
        this.getContentPane().removeAll();
        this.getContentPane().repaint();
    }

    public void showJoinScreen(){
        //QR-Code that lets people join through the browser
        JPanel panelJoin = new JPanel(new BorderLayout(5, 5));
        panelJoin.setBackground(Color.CYAN);

        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panelJoin.add(title, BorderLayout.NORTH);

        //panelJoin.add(list of people, BorderLayout.WEST);

        //panelJoin.add(QR Code, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.CYAN);

        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(300, 80));
        startButton.addActionListener(e -> showMainScreen());
        buttonPanel.add(startButton);

        JButton configButton = new JButton("Neue Konfiguration erstellen");
        configButton.setPreferredSize(new Dimension(300, 80));
        configButton.addActionListener(e -> {
            new MainController();
        });
        buttonPanel.add(configButton);

        JButton loadButton = new JButton("Spiel laden");
        loadButton.setPreferredSize(new Dimension(300, 80));
        loadButton.addActionListener(e -> {
            List<Configuration> configs = DatabaseConnector.loadConfigurations();
            if (!configs.isEmpty()) {
                Configuration config = configs.get(0);
                gameLogic.loadGame(config);
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Keine gespeicherten Konfigurationen gefunden.",
                    "Spiel laden", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(loadButton);

        panelJoin.add(buttonPanel, BorderLayout.SOUTH);

        this.add(panelJoin);
        this.setVisible(true);
    }

    public void showMainScreen(){
        cleanScreen();
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

        //Top Panel
        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMain.add(title);

        //Center Panel — build grid from loaded configuration or fallback to defaults
        Configuration config = gameLogic.getConfiguration();
        if (config != null && !config.getCategories().isEmpty()) {
            List<Category> categories = config.getCategories();
            int maxPoints = 0;
            for (Category cat : categories) {
                maxPoints = Math.max(maxPoints, cat.getPointQuestionMap().size());
            }

            JPanel centerPanelMain = new JPanel(new GridLayout(maxPoints + 1, categories.size()));

            for (Category cat : categories) {
                JButton catButton = new JButton(cat.getName());
                catButton.setEnabled(false);
                centerPanelMain.add(catButton);
            }

            for (int row = 0; row < maxPoints; row++) {
                int pointValue = (row + 1) * 10;
                for (Category cat : categories) {
                    ConfigQuestion q = cat.getPointQuestionMap().get(pointValue);
                    JButton button = new JButton(pointValue + " Punkte");
                    if (q == null) {
                        button.setEnabled(false);
                    } else {
                        button.addActionListener(e -> {
                            button.setEnabled(false);
                            showQuestionScreen();
                        });
                    }
                    centerPanelMain.add(button);
                }
            }
            panelMain.add(centerPanelMain);
        } else {
            // Fallback: hardcoded 5x5 grid (original behavior)
            JPanel centerPanelMain = new JPanel(new GridLayout(6, 5));
            for (int i = 1; i < 6; i++) {
                centerPanelMain.add(new JButton("Theme " + i));
            }
            for (int i = 1; i < 6; i++) {
                for (int j = 1; j < 6; j++) {
                    JButton button = new JButton(i * 10 + " Points");
                    centerPanelMain.add(button);
                }
            }
            panelMain.add(centerPanelMain);
        }

        //Bottom Panel — scoreboard from GameLogic or fallback
        JPanel bottomPanelMain = new JPanel(new GridLayout(0, 2));

        String turnText = "Player x turn";
        if (!gameLogic.getTeams().isEmpty()) {
            turnText = gameLogic.getCurrentTeam().getName() + " ist dran";
        }
        bottomPanelMain.add(new JLabel(turnText));

        int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 4);
        bottomPanelMain.setPreferredSize(new Dimension(Integer.MAX_VALUE, x));
        bottomPanelMain.setMaximumSize(new Dimension(Integer.MAX_VALUE, x));

        String[] columnHeader = {"Team", "Punkte"};
        Object[][] data;
        PointsManager pm = gameLogic.getPointsManager();
        Map<Team, Integer> teamPoints = pm.getAllTeamPoints();
        if (!teamPoints.isEmpty()) {
            data = new Object[teamPoints.size()][2];
            int idx = 0;
            for (Map.Entry<Team, Integer> entry : teamPoints.entrySet()) {
                data[idx][0] = entry.getKey().getName();
                data[idx][1] = entry.getValue();
                idx++;
            }
        } else {
            data = new Object[][]{
                {"Player 1", 2},
                {"Player 2", 3},
                {"Player 3", 1}
            };
        }
        JTable currentRanking = new JTable(data, columnHeader);
        JScrollPane scrollPane = new JScrollPane(currentRanking);
        bottomPanelMain.add(scrollPane);
        panelMain.add(bottomPanelMain);

        this.add(panelMain);
        this.setVisible(true);
    }

    public void showQuestionScreen(){
        //Screen that will display the selected question and the different answer possibilities
        cleanScreen();
    }

    public void showEndScreen(){
        //show the result who is in first, second, etc... place
    }

    public void addPlayer(String name) {
        this.players.add(new Player(name));
    }

    public void answerQuestion(Question question, String name){
        //todo check if question is right or wrong
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}
