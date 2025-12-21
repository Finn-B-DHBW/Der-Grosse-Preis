import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameManager extends JFrame {
    private List<Player> players;
    private List<Question> questions;
    private SocketServer serverSocket;
    private DataBase db;

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.db = new DataBase();

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

        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(500, 100));
        startButton.addActionListener(e -> showMainScreen());
        panelJoin.add(startButton, BorderLayout.SOUTH);

        this.add(panelJoin);
        this.setVisible(true);
    }

    public void showMainScreen(){
        //main.java.Main screen that will display every theme and the different question points
        cleanScreen();
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

        //Top Panel
        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMain.add(title);

        //Center Panel
        JPanel centerPanelMain = new JPanel(new GridLayout(6, 5));
        for (int i = 1; i < 6; i++) {
            centerPanelMain.add(new JButton("Theme "+i));
        }
        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < 6; j++) {
                JButton button = new JButton(i*10+"Points");
//                button.addActionListener(e -> showQuestionScreen()); just for testing
                centerPanelMain.add(button);
            }
        }
        panelMain.add(centerPanelMain);

        //Bottom Panel
        JPanel bottomPanelMain = new JPanel(new GridLayout(0,2));
        bottomPanelMain.add(new JLabel("main.java.Player x turn"));

        //Testing JTable
        System.out.println((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/4);
        bottomPanelMain.setPreferredSize(new Dimension(Integer.MAX_VALUE, x));
        bottomPanelMain.setMaximumSize(new Dimension(Integer.MAX_VALUE, x));
        String[] columnHeader = {"main.java.Player", "Points"};
        Object[][] data = {
                {"main.java.Player 1", 2},
                {"main.java.Player 2", 3},
                {"main.java.Player 3", 1}
        };
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

}
