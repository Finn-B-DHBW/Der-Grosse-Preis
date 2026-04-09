package Model;

import java.util.List;

public class Player {
    private final String name;
    private List<Question> rightAnswers;
    private int score;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Question> getRightAnswers() {
        return rightAnswers;
    }

    public int getScore() {
        return score;
    }

    public void addRightAnswerToList(Question rightAnswers, String name) {
        this.rightAnswers.add(rightAnswers);
        this.score += rightAnswers.getScore();
    }

}
