package config.builder;

import config.model.Configuration;

public class ConfigurationBuilder {
    private Configuration configuration;
    private CategoryBuilder categoryBuilder;

    public static ConfigurationBuilder createConfiguration() {
        return new ConfigurationBuilder();
    }

    private ConfigurationBuilder() {
        this.configuration = new Configuration("");
        this.categoryBuilder = new CategoryBuilder(configuration, 5);
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
