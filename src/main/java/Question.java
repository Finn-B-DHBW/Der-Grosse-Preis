import java.util.Arrays;

public class Question {

    private final String questionText;
    private final String category;
    private final String rightAnswer;
    private String[] wrongAnswers;
    private final int score;
    private final int questionId;

    public Question(String questionText, String category, String rightAnswer, int score, int questionId) {
        this(questionText, category, rightAnswer, null, score, questionId);
    }

    public Question(String questionText, String category, String rightAnswer, String[] wrongAnswers, int score, int questionId) {
        this.questionText = questionText;
        this.category = category;
        this.rightAnswer = rightAnswer;
        this.wrongAnswers = wrongAnswers;
        this.score = score;
        this.questionId = questionId;
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

    public int getQuestionId() {
        return questionId;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionText='" + questionText + '\'' +
                ", category='" + category + '\'' +
                ", rightAnswer='" + rightAnswer + '\'' +
                ", wrongAnswers=" + Arrays.toString(wrongAnswers) +
                ", score=" + score +
                ", questionId=" + questionId +
                '}';
    }
}
