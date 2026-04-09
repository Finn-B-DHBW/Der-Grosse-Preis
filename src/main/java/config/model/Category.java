package config.model;

import java.util.Map;

public class Category {
    private Map<Integer, ConfigQuestion> pointQuestionMap;
    private String name;

    public Category(String name, Map<Integer, ConfigQuestion> questionsMap) {
        this.name = name;
        this.pointQuestionMap = questionsMap;
    }

    public Map<Integer, ConfigQuestion> getPointQuestionMap() {
        return pointQuestionMap;
    }

    public String getName() {
        return name;
    }
}
