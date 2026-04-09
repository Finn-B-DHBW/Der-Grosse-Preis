package game;

import config.model.Category;
import config.model.ConfigQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameOverview {
    private List<Category> categories;
    private Map<Category, List<Integer>> availableQuestions;
    private Map<Category, List<Integer>> answeredQuestions;

    public GameOverview() {
        this.categories = new ArrayList<>();
        this.availableQuestions = new HashMap<>();
        this.answeredQuestions = new HashMap<>();
    }

    public void addCategory(Category category) {
        categories.add(category);
        List<Integer> pointsList = new ArrayList<>(category.getPointQuestionMap().keySet());
        availableQuestions.put(category, pointsList);
        answeredQuestions.put(category, new ArrayList<>());
    }

    public boolean markQuestionAsAnswered(Category category, int points) {
        if (availableQuestions.containsKey(category) && availableQuestions.get(category).contains(points)) {
            availableQuestions.get(category).remove(Integer.valueOf(points));
            answeredQuestions.get(category).add(points);
            return true;
        }
        return false;
    }

    public ConfigQuestion getQuestion(Category category, int points) {
        if (category != null && category.getPointQuestionMap().containsKey(points)) {
            return category.getPointQuestionMap().get(points);
        }
        return null;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Map<Category, List<Integer>> getAvailableQuestions() {
        return availableQuestions;
    }

    public Map<Category, List<Integer>> getAnsweredQuestions() {
        return answeredQuestions;
    }
}
