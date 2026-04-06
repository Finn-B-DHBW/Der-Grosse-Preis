import javax.swing.*;

public class JButtonWithCustomAttribute extends JButton {
    private final int questionId;

    public JButtonWithCustomAttribute(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }
}
