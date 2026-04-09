package config;

import config.builder.ConfigurationBuilder;
import config.model.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfiguratorOverview {
    private List<Configuration> configurations;
    private Configuration currentConfiguration;
    public ConfigurationBuilder configurationBuilder;

    public ConfiguratorOverview() {
        this.configurations = new ArrayList<>();
        loadConfigurationsFromDatabase();
    }

    private void loadConfigurationsFromDatabase() {
        this.configurations = DatabaseConnector.loadConfigurations();
    }

    public void saveConfigurationToDatabase() {
        if (currentConfiguration != null) {
            DatabaseConnector.saveConfiguration(currentConfiguration);
        }
    }

    public void createNewConfiguration() {
        configurationBuilder = ConfigurationBuilder.createConfiguration();
        this.currentConfiguration = configurationBuilder.getConfiguration();
    }

    public void editConfiguration() {
    }

    public void duplicateConfiguration() {
    }

    public boolean deleteConfiguration() {
        return configurations.remove(currentConfiguration);
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }
}
