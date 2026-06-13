package config.model;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private Integer configId;
    private String title;
    private List<Category> categories;

    public Configuration(String title) {
        this.title = title;
        this.categories = new ArrayList<>();
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }
}
