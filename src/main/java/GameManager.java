import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends JFrame {
    private final List<Player> players = new ArrayList<>();
    private List<Question> questions;
    private SocketServer serverSocket;
    private DataBase db;
    private final String[] categoryList = {"SPORT", "LAND", "ESSEN", "SCHAUSPIELER", "VIDEO-SPIEL"};

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.db = new DataBase();

        this.setResizable(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true); maybe later add a option to use esc button to close
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        showJoinScreen();
    }

    private void setQuestions() {
        questions = new ArrayList<>();
        int questionId = 1;
        for (String category : categoryList) {
            for (int j = 1; j <= 5; j++) {
                questions.add(new Question("TEST QUESTION", category, "RIGHT ANSWER",
                        new String[]{"WRONG ANSWER 1", "WRONG ANSWER 2", "WRONG ANSWER 3"}, j * 10, questionId));
                questionId++;
            }
        }
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
        startButton.addActionListener(e -> {
            if (players.isEmpty()){
                players.add(new Player("Test Player"));
            }
            setQuestions();
            showMainScreen();
        });
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
        JPanel centerPanelMain = getJPanel();
        panelMain.add(centerPanelMain);

        //Bottom Panel
        JPanel bottomPanelMain = new JPanel(new GridLayout(0,2));
        int playerTurn = 0;
        bottomPanelMain.add(new JLabel("Player '"+ players.get(playerTurn).getName() +"' turn"));

        //Testing JTable
        System.out.println((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/4);
        bottomPanelMain.setPreferredSize(new Dimension(Integer.MAX_VALUE, y));
        bottomPanelMain.setMaximumSize(new Dimension(Integer.MAX_VALUE, y));
        String[] columnHeader = {"Player", "Points"};

        Object[][] data = new Object[players.size()][2];
        for (int i = 0; i < players.size(); i++) {
            data[i][0] = players.get(i).getName();
            data[i][1] = players.get(i).getScore();
        }
        JTable currentRanking = new JTable(data, columnHeader);
        JScrollPane scrollPane = new JScrollPane(currentRanking);
        bottomPanelMain.add(scrollPane);
        panelMain.add(bottomPanelMain);

        this.add(panelMain);
        this.setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel centerPanelMain = new JPanel(new GridLayout(6, 5, 10, 10));
        centerPanelMain.setBorder(new EmptyBorder(10, 10, 10,10));
        centerPanelMain.setBackground(Color.GREEN);
        for (String s : this.categoryList) {
            JLabel category = new JLabel(s, SwingConstants.CENTER);
            category.setBackground(Color.DARK_GRAY);
            category.getFont();
            centerPanelMain.add(category);
        }

        int questionIdCount;

        for (int i = 1; i < 6; i++) {
            questionIdCount = i;
            for (int j = 1; j < 6; j++) {
//                JButton button = new JButton(i*10+"Points");
                JButtonWithCustomAttribute button = new JButtonWithCustomAttribute(questionIdCount);
                button.setText(i*10+" Points");
                button.addActionListener(e -> showQuestionScreen(questions.stream()
                        .filter(q -> q.getQuestionId() == button.getQuestionId())
                        .findFirst()
                        .orElse(null))
                );
                centerPanelMain.add(button);
                questionIdCount+= 5;
            }
        }
        return centerPanelMain;
    }

    public void showQuestionScreen(Question question){
        //Screen that will display the selected question and the different answer possibilities
        cleanScreen();
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

        this.add(panelMain);
        this.setVisible(true);
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
