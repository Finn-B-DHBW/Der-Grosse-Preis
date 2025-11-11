package dhbw.dgp.ui;

import dhbw.dgp.ConfiguratorOverview;

import javax.swing.*;
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

        // ### ADD NEW PAGE HERE

        configuratorPanel = new ConfiguratorPanel();
        configurationPanel = new ConfigurationPanel();
        categoryPanel = new CategoryPanel();

        mainView.addPage("configuratorPanel", configuratorPanel);
        mainView.addPage("configurationPanel", configurationPanel);
        mainView.addPage("categoryPanel", categoryPanel);

        // Create controllers with updated dependencies
        configurationController = new ConfigurationController(configurationPanel, mainView, configuratorOverview, configuratorPanel);
        configuratorController = new ConfiguratorController(configuratorPanel, mainView, configurationPanel, configuratorOverview, configurationController);
        categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);

        // ### END ADD NEW PAGE

        mainView.showPage(configuratorPanel);

        mainView.setVisible(true);
    }

    private static void setNimbusLookAndFeel() {
        try {
            // Initialize FlatLaf (light theme)
            FlatLightLaf.setup();
        } catch (Exception ex) {
            // keep default LookAndFeel if FlatLaf is not available
        }
    }
}
