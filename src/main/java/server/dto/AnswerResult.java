package server.dto;

public class AnswerResult {

    private String playerName;
    private String selectedAnswer;
    private boolean correct;
    private int newScore;

    public AnswerResult() {
    }

    public AnswerResult(String playerName, String selectedAnswer, boolean correct, int newScore) {
        this.playerName = playerName;
        this.selectedAnswer = selectedAnswer;
        this.correct = correct;
        this.newScore = newScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getNewScore() {
        return newScore;
    }

    public void setNewScore(int newScore) {
        this.newScore = newScore;
    }
}
