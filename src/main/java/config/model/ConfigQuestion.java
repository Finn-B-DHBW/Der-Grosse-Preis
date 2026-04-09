package config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigQuestion {
    private String question;
    private List<String> answers;
    private int numberOfAnswers;
    /**
     * Index of the correct answer in 1..3; 0 means none selected.
     */
    private int correctAnswer;

    public ConfigQuestion(Integer count) {
        this.numberOfAnswers = (count != null) ? count : 3;
        this.answers = new ArrayList<>(numberOfAnswers);

        for (int i = 0; i < numberOfAnswers; i++) {
            answers.add("");
        }
        this.correctAnswer = 0;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setAnswer(int position, String answer) {
        if (position >= 0 && position < numberOfAnswers) {
            answers.set(position, answer);
        }
    }

    public List<String> getAnswers() {
        return Collections.unmodifiableList(this.answers);
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        if (correctAnswer >= 0 && correctAnswer <= 3) {
            this.correctAnswer = correctAnswer;
        }
    }
}
