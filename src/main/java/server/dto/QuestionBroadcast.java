package server.dto;

import java.util.List;

/**
 * Frage, die an die Spieler-Browser geschickt wird.
 * Enthaelt bewusst KEINEN Hinweis auf die richtige Antwort,
 * damit Spieler nicht ueber die DevTools schummeln koennen.
 */
public class QuestionBroadcast {

    private int questionId;
    private String questionText;
    private List<String> answers;
    private int scoreValue;

    public QuestionBroadcast() {
    }

    public QuestionBroadcast(int questionId, String questionText, List<String> answers, int scoreValue) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.answers = answers;
        this.scoreValue = scoreValue;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }
}
