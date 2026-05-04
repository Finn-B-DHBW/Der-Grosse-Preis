package Model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final List<Question> rightAnswers = new ArrayList<>();
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

    public void addRightAnswerToList(Question rightQuestion) {
        this.rightAnswers.add(rightQuestion);
        this.score += rightQuestion.getScore();
    }

}
