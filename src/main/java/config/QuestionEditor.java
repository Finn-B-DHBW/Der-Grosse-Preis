package config;

import config.model.Category;
import config.model.ConfigQuestion;

public class QuestionEditor {
    private ConfigQuestion currentQuestionInEditing;

    public ConfigQuestion setQuestionInEditing(Category category, int points) {
        if (category == null || !category.getPointQuestionMap().containsKey(points)) {
            this.currentQuestionInEditing = null;
            return null;
        }

        this.currentQuestionInEditing = category.getPointQuestionMap().get(points);
        return this.currentQuestionInEditing;
    }

    public void clearQuestionInEditing() {
        this.currentQuestionInEditing = null;
    }
}
