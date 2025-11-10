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
        this.db = new DataBase(this);

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
        //Main screen that will display every theme and the different question points
        cleanScreen();
        JPanel panelMain = new JPanel();
        panelMain.setBackground(Color.GREEN);
        this.add(panelMain);
        this.setVisible(true);
    }

    public void showQuestionScreen(){
        //Screen that will display the selected question and the different answer possibilities
    }

    public void showEndScreen(){
        //show the result who is in first, second, etc... place
    }

    public void addPlayer(String name) {
        this.players.add(new Player(name));
    }
}
