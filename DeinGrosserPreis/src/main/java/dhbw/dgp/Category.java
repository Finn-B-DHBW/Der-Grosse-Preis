package dhbw.dgp;

import java.util.Map;

public class Category {
    private Map<Integer, Question> pointQuestionMap;
    private String name;

    public Category(String name, Map<Integer, Question> questionsMap) {
        this.name = name;
        this.pointQuestionMap = questionsMap;
    }

    /**
     * Gets the map of point values with their questions
     * @return Map of Integer points to Question objects
     */
    public Map<Integer, Question> getPointQuestionMap() {
        return pointQuestionMap;
    }

    /**
     * Gets the name of the category
     * @return The name of the category
     */
    public String getName() {
        return name;
    }
}