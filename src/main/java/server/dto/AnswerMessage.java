package server.dto;

public class AnswerMessage {

    private int questionId;
    private String selectedAnswer;
    private String playerName;

    public AnswerMessage() {
    }

    public AnswerMessage(int questionId, String selectedAnswer, String playerName) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.playerName = playerName;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
