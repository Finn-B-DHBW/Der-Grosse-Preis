package config;

import config.builder.ConfigurationBuilder;
import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConfiguratorOverview {
    private List<Configuration> configurations;
    private Configuration currentConfiguration;
    public ConfigurationBuilder configurationBuilder;

    public ConfiguratorOverview() {
        this.configurations = new ArrayList<>();
        refreshConfigurations();
    }

    public void refreshConfigurations() {
        this.configurations = DatabaseConnector.loadConfigurations();
        if (currentConfiguration != null && currentConfiguration.getConfigId() != null) {
            Integer id = currentConfiguration.getConfigId();
            Configuration found = null;
            for (Configuration c : configurations) {
                if (id.equals(c.getConfigId())) { found = c; break; }
            }
            currentConfiguration = found;
        }
    }

    public void saveConfigurationToDatabase() {
        if (currentConfiguration == null) return;
        if (currentConfiguration.getConfigId() != null) {
            DatabaseConnector.deleteConfiguration(currentConfiguration.getConfigId());
            currentConfiguration.setConfigId(null);
        }
        DatabaseConnector.saveConfiguration(currentConfiguration);
        refreshConfigurations();
    }

    public void createNewConfiguration() {
        configurationBuilder = ConfigurationBuilder.createConfiguration();
        this.currentConfiguration = configurationBuilder.getConfiguration();
    }

    public void editConfiguration() {
        if (currentConfiguration == null) return;
        configurationBuilder = ConfigurationBuilder.fromExisting(currentConfiguration);
    }

    public void duplicateConfiguration() {
        if (currentConfiguration == null) return;
        Configuration copy = deepCopy(currentConfiguration);
        String suffix = " (Kopie)";
        copy.setTitle((copy.getTitle() == null ? "" : copy.getTitle()) + suffix);
        DatabaseConnector.saveConfiguration(copy);
        refreshConfigurations();
        setCurrentConfigurationById(copy.getConfigId());
    }

    public boolean deleteConfiguration() {
        if (currentConfiguration == null || currentConfiguration.getConfigId() == null) {
            return false;
        }
        DatabaseConnector.deleteConfiguration(currentConfiguration.getConfigId());
        currentConfiguration = null;
        refreshConfigurations();
        return true;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(Configuration configuration) {
        this.currentConfiguration = configuration;
    }

    public void setCurrentConfigurationById(Integer configId) {
        if (configId == null) {
            currentConfiguration = null;
            return;
        }
        for (Configuration c : configurations) {
            if (configId.equals(c.getConfigId())) {
                currentConfiguration = c;
                return;
            }
        }
        currentConfiguration = null;
    }

    public void clearSelection() {
        currentConfiguration = null;
    }

    private Configuration deepCopy(Configuration source) {
        Configuration target = new Configuration(source.getTitle());
        for (Category cat : source.getCategories()) {
            Map<Integer, ConfigQuestion> newMap = new TreeMap<>();
            for (Map.Entry<Integer, ConfigQuestion> entry : cat.getPointQuestionMap().entrySet()) {
                ConfigQuestion src = entry.getValue();
                ConfigQuestion copy = new ConfigQuestion(src.getAnswers().size());
                copy.setQuestion(src.getQuestion());
                for (int i = 0; i < src.getAnswers().size(); i++) {
                    copy.setAnswer(i, src.getAnswers().get(i));
                }
                copy.setCorrectAnswer(src.getCorrectAnswer());
                newMap.put(entry.getKey(), copy);
            }
            target.addCategory(new Category(cat.getName(), newMap));
        }
        return target;
    }
}
