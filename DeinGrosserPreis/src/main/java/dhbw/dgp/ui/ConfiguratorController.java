package dhbw.dgp.ui;

import dhbw.dgp.ConfiguratorOverview;

import javax.swing.*;

public class ConfiguratorController
{
    public ConfiguratorController(ConfiguratorPanel configuratorPanel, MainView mainView, ConfigurationPanel configurationPanel, ConfiguratorOverview configuratorOverview, ConfigurationController configurationController) {

        configuratorPanel.buttonCreate.addActionListener(e -> {
            configuratorOverview.createNewConfiguration();

            // Create a button for this new configuration using its current title
            String title = configuratorOverview.configurationBuilder.getConfiguration().getTitle();
            if (title == null || title.isBlank()) title = "Konfiguration";
            JButton cfgBtn = new JButton(title);
            // Clicking the button opens the configuration panel
            cfgBtn.addActionListener(ev -> mainView.showPage(configurationPanel));
            configuratorPanel.panelConfigurationButtons.add(cfgBtn);
            configuratorPanel.panelConfigurationButtons.revalidate();
            configuratorPanel.panelConfigurationButtons.repaint();

            // Let the configuration controller update the button text when title changes
            configurationController.setCurrentConfigButton(cfgBtn);

            // Go to configuration page now
            mainView.showPage(configurationPanel);
        });
    }
}
