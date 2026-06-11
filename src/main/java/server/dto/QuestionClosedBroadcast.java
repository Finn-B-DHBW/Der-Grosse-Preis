package server.dto;

public class QuestionClosedBroadcast {

    private int questionId;
    private String correctAnswer;

    public QuestionClosedBroadcast() {
    }

    public QuestionClosedBroadcast(int questionId, String correctAnswer) {
        this.questionId = questionId;
        this.correctAnswer = correctAnswer;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
