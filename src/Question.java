public class Question {
    private final String questionText;
    private final String categorie;
    private final String rightAnswer;
    private String[] wrongAnswers = new String[3];
    private final int score;

    public Question(String questionText, String categorie, String rightAnswer, String[] wrongAnswers, int score) {
        this.questionText = questionText;
        this.categorie = categorie;
        this.rightAnswer = rightAnswer;
        this.wrongAnswers = wrongAnswers;
        this.score = score;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCategorie() {
        return categorie;
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
