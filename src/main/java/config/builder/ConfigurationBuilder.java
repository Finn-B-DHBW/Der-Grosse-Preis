package config.builder;

import config.model.Configuration;

public class ConfigurationBuilder {
    private Configuration configuration;
    private CategoryBuilder categoryBuilder;

    public static ConfigurationBuilder createConfiguration() {
        return new ConfigurationBuilder();
    }

    public static ConfigurationBuilder fromExisting(Configuration existing) {
        return new ConfigurationBuilder(existing);
    }

    private ConfigurationBuilder() {
        this.configuration = new Configuration("");
        this.categoryBuilder = new CategoryBuilder(configuration, 5);
    }

    private ConfigurationBuilder(Configuration existing) {
        this.configuration = existing;
        int questionsPerCategory = 5;
        if (existing.getCategories() != null && !existing.getCategories().isEmpty()) {
            int max = 0;
            for (config.model.Category cat : existing.getCategories()) {
                if (cat.getPointQuestionMap() != null) {
                    max = Math.max(max, cat.getPointQuestionMap().size());
                }
            }
            if (max > 0) questionsPerCategory = max;
        }
        this.categoryBuilder = new CategoryBuilder(configuration, questionsPerCategory);
    }

    public void setTitle(String title) {
        this.configuration.setTitle(title);
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.categoryBuilder.setNumberOfQuestions(numberOfQuestions);
    }

    public int getNumberOfQuestions() {
        return this.categoryBuilder.numberOfQuestions;
    }

    public void createNewCategory(String name) {
        categoryBuilder.createNewCategory(name);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
