package Manager;

import Connection.DataBase;
import Connection.SocketServer;
import Model.Player;
import Model.Question;
import UI.Screen.EndScreen;
import UI.Screen.JoinScreen;
import UI.Screen.MainScreen;
import UI.Screen.QuestionScreen;
import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager extends JFrame {
    private final List<Player> playerList = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private List<Question> questionList;
    private final SocketServer serverSocket;
    private final DataBase dataBase;
    private String[] categoryList = {"SPORT", "LAND", "ESSEN", "SCHAUSPIELER", "VIDEO-SPIEL"};
    private final List<Question> answeredQuestionList;

    private final EndScreen endScreen;
    private final JoinScreen joinScreen;
    private final MainScreen mainScreen;
    private final QuestionScreen questionScreen;

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.dataBase = new DataBase();
        this.endScreen = new EndScreen();
        this.joinScreen = new JoinScreen();
        this.mainScreen = new MainScreen();
        this.questionScreen = new QuestionScreen();
        this.answeredQuestionList = new ArrayList<>();

        this.setResizable(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true); maybe later add an option to use esc button to close
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.joinScreen.showJoinScreen(this);
    }

    public void setQuestions() {
        this.questionList = new ArrayList<>();
        int questionId = 1;
        for (String category : categoryList) {
            for (int j = 1; j <= 5; j++) {
                this.questionList.add(new Question("TEST QUESTIONmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm", category, "RIGHT ANSWER",
                        new String[]{"WRONG ANSWER 1", "WRONG ANSWER 2", "WRONG ANSWER 3"}, j * 10, questionId));
                questionId++;
            }
        }
        this.answeredQuestionList.clear();
    }

    public void loadConfiguration(Configuration configuration) {
        if (configuration == null || configuration.getCategories().isEmpty()) {
            return;
        }

        this.categoryList = configuration.getCategories().stream()
                .map(Category::getName)
                .toArray(String[]::new);
        this.questionList = new ArrayList<>();
        this.answeredQuestionList.clear();

        int questionId = 1;
        for (Category category : configuration.getCategories()) {
            for (Map.Entry<Integer, ConfigQuestion> entry : category.getPointQuestionMap().entrySet()) {
                ConfigQuestion configQuestion = entry.getValue();
                String rightAnswer = getRightAnswer(configQuestion);
                this.questionList.add(new Question(
                        getQuestionText(configQuestion),
                        category.getName(),
                        rightAnswer,
                        getWrongAnswers(configQuestion, rightAnswer),
                        entry.getKey(),
                        questionId
                ));
                questionId++;
            }
        }
    }

    private String getQuestionText(ConfigQuestion configQuestion) {
        if (configQuestion == null || isBlank(configQuestion.getQuestion())) {
            return "Keine Frage eingetragen";
        }
        return configQuestion.getQuestion();
    }

    private String getRightAnswer(ConfigQuestion configQuestion) {
        if (configQuestion == null || configQuestion.getAnswers().isEmpty()) {
            return "Keine richtige Antwort eingetragen";
        }

        int correctAnswerIndex = configQuestion.getCorrectAnswer() - 1;
        if (correctAnswerIndex >= 0 && correctAnswerIndex < configQuestion.getAnswers().size()
                && !isBlank(configQuestion.getAnswers().get(correctAnswerIndex))) {
            return configQuestion.getAnswers().get(correctAnswerIndex);
        }

        for (String answer : configQuestion.getAnswers()) {
            if (!isBlank(answer)) {
                return answer;
            }
        }
        return "Keine richtige Antwort eingetragen";
    }

    private String[] getWrongAnswers(ConfigQuestion configQuestion, String rightAnswer) {
        if (configQuestion == null) {
            return new String[0];
        }

        List<String> wrongAnswers = new ArrayList<>();
        for (String answer : configQuestion.getAnswers()) {
            if (!isBlank(answer) && !answer.equals(rightAnswer)) {
                wrongAnswers.add(answer);
            }
        }
        return wrongAnswers.toArray(new String[0]);
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
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

    public DataBase getDataBase() {
        return dataBase;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Question> getQuestionList() {
        return questionList;
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
