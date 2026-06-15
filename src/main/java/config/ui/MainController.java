package config.ui;

import config.ConfiguratorOverview;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Insets;

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
            // Rounded corners for all interactive components
            UIManager.put("Button.arc",          12);
            UIManager.put("Component.arc",        8);
            UIManager.put("TextComponent.arc",    8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            // Slightly larger checkbox icons
            UIManager.put("CheckBox.icon.width",  18);
            UIManager.put("CheckBox.icon.height", 18);
            // Remove the default FlatLaf gray background — use white everywhere
            UIManager.put("Panel.background",      Color.WHITE);
            UIManager.put("ScrollPane.background", Color.WHITE);
            UIManager.put("Viewport.background",   Color.WHITE);
        } catch (Exception ex) {
            // keep default LookAndFeel if FlatLaf is not available
        }
    }
}
