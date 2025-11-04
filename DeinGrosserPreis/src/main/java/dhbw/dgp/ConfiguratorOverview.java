package dhbw.dgp;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing game configurations
 */
public class ConfiguratorOverview {
    private List<Configuration> configurations;
    private Configuration currentConfiguration;

    /**
     * Constructor that initializes the configurations list and loads configurations from the database
     */
    public ConfiguratorOverview() {
        this.configurations = new ArrayList<>();
        loadConfigurationsFromDatabase();
    }

    /**
     * Loads configurations from the database
     */
    private void loadConfigurationsFromDatabase() {
        this.configurations = DatabaseConnector.loadConfigurations();
    }

    /**
     * Saves the current configuration to the database
     */
    public void saveConfigurationToDatabase() {
        if (currentConfiguration != null) {
            DatabaseConnector.saveConfiguration(currentConfiguration);
        }
    }

    /**
     * Creates a new configuration with the given title
     * @return The newly created configuration
     */
    public void createNewConfiguration() {
        var builder = ConfigurationBuilder.createConfiguration();
        this.currentConfiguration = builder.getConfiguration();
    }

    /**
     * Edits an existing configuration
     */
    public void editConfiguration() {

    }

    /**
     * Duplicates an existing configuration
     * @return The duplicated configuration
     */
    public void duplicateConfiguration() {

    }

    /**
     * Deletes a configuration
     * @return True if the configuration was deleted, false otherwise
     */
    public boolean deleteConfiguration() {
        return configurations.remove(currentConfiguration);
    }

    /**
     * Gets the list of all configurations
     * @return The list of configurations
     */
    public List<Configuration> getConfigurations() {
        return configurations;
    }
}
