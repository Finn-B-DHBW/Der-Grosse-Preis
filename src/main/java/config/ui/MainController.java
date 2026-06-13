package config.ui;

import config.ConfiguratorOverview;
import com.formdev.flatlaf.FlatLightLaf;

public class MainController {
    private MainView mainView;
    private ConfiguratorOverview configuratorOverview;

    private ConfiguratorController configuratorController;
    private ConfigurationController configurationController;
    private CategoryController categoryController;

    private ConfiguratorPanel configuratorPanel;
    private ConfigurationPanel configurationPanel;
    private CategoryPanel categoryPanel;

    public MainController() {
        setNimbusLookAndFeel();
        mainView = new MainView();
        configuratorOverview = new ConfiguratorOverview();

        configuratorPanel = new ConfiguratorPanel();
        configurationPanel = new ConfigurationPanel();
        categoryPanel = new CategoryPanel();

        mainView.addPage("configuratorPanel", configuratorPanel);
        mainView.addPage("configurationPanel", configurationPanel);
        mainView.addPage("categoryPanel", categoryPanel);

        configurationController = new ConfigurationController(configurationPanel, mainView, configuratorOverview, configuratorPanel);
        configuratorController = new ConfiguratorController(configuratorPanel, mainView, configurationPanel, configuratorOverview, configurationController);
        categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);

        configurationController.setOnDoneCallback(configuratorController::refreshConfigurationList);

        mainView.showPage(configuratorPanel);

        mainView.setVisible(true);
    }

    private static void setNimbusLookAndFeel() {
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            // keep default LookAndFeel if FlatLaf is not available
        }
    }
}
