public class Question {

    private final String questionText;
    private final String category;
    private final String rightAnswer;
    private String[] wrongAnswers = new String[3];
    private final int score;

    public Question(String questionText, String category, String rightAnswer, int score) {
        this.questionText = questionText;
        this.category = category;
        this.rightAnswer = rightAnswer;
        this.score = score;
    }

    public Question(String questionText, String category, String rightAnswer, String[] wrongAnswers, int score) {
        this.questionText = questionText;
        this.category = category;
        this.rightAnswer = rightAnswer;
        this.wrongAnswers = wrongAnswers;
        this.score = score;
    }

    public void setWrongAnswers(String[] wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCategory() {
        return category;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String[] getWrongAnswers() {
        return wrongAnswers;
    }

    public int getScore() {
        return score;
    }
}
