import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private String title;
    private List<Category> categories;

    public Configuration(String title) {
        this.title = title;
        this.categories = new ArrayList<>();
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
