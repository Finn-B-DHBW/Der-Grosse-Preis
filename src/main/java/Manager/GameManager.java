package Manager;

import Connection.DataBase;
import Connection.SocketServer;
import Model.Player;
import Model.Question;
import UI.Screen.EndScreen;
import UI.Screen.JoinScreen;
import UI.Screen.MainScreen;
import UI.Screen.QuestionScreen;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends JFrame {
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private List<Question> questions;
    private final SocketServer serverSocket;
    private final DataBase db;
    private final String[] categoryList = {"SPORT", "LAND", "ESSEN", "SCHAUSPIELER", "VIDEO-SPIEL"};
    private final List<Question> answeredQuestionList;

    private final EndScreen endScreen;
    private final JoinScreen joinScreen;
    private final MainScreen mainScreen;
    private final QuestionScreen questionScreen;

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.db = new DataBase();
        this.endScreen = new EndScreen();
        this.joinScreen = new JoinScreen();
        this.mainScreen = new MainScreen();
        this.questionScreen = new QuestionScreen();
        this.answeredQuestionList = new ArrayList<>();

        this.setResizable(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true); maybe later add a option to use esc button to close
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.joinScreen.showJoinScreen(this);
    }

    public void setQuestions() {
        questions = new ArrayList<>();
        int questionId = 1;
        for (String category : categoryList) {
            for (int j = 1; j <= 5; j++) {
                questions.add(new Question("TEST QUESTIONmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm", category, "RIGHT ANSWER",
                        new String[]{"WRONG ANSWER 1", "WRONG ANSWER 2", "WRONG ANSWER 3"}, j * 10, questionId));
                questionId++;
            }
        }
    }

    public void cleanScreen() {
        this.getContentPane().removeAll();
        this.getContentPane().repaint();
    }

    public void answerQuestion(Question question, String name){
        //todo check if question is right or wrong
    }

    public String[] getCategoryList() {
        return categoryList;
    }

    public DataBase getDb() {
        return db;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public SocketServer getServerSocket() {
        return serverSocket;
    }

    public EndScreen getEndScreen() {
        return endScreen;
    }

    public JoinScreen getJoinScreen() {
        return joinScreen;
    }

    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public QuestionScreen getQuestionScreen() {
        return questionScreen;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public List<Question> getAnsweredQuestionList() {
        return answeredQuestionList;
    }
}
