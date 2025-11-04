package dhbw.dgp;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String question;
    private List<String> answers;
    private int numberOfAnswers;

    /**
     * Constructor for Question class
     * @param count Number of answers, defaults to 3 if null
     */
    public Question(Integer count) {
        this.numberOfAnswers = (count != null) ? count : 3;
        this.answers = new ArrayList<>(numberOfAnswers);

        // Initialize the list with empty strings
        for (int i = 0; i < numberOfAnswers; i++) {
            answers.add("");
        }
    }

    /**
     * Sets the question text
     * @param question The question text
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Sets an answer at the specified position
     * @param position The position of the answer (0-based index)
     * @param answer The answer text
     */
    public void setAnswer(int position, String answer) {
        if (position >= 0 && position < numberOfAnswers) {
            answers.set(position, answer);
        }
    }
}