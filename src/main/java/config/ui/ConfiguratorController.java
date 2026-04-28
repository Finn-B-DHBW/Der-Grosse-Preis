package config.ui;

import config.ConfiguratorOverview;

import javax.swing.*;

public class ConfiguratorController
{
    public ConfiguratorController(ConfiguratorPanel configuratorPanel, MainView mainView, ConfigurationPanel configurationPanel, ConfiguratorOverview configuratorOverview, ConfigurationController configurationController) {

        configuratorPanel.getButtonCreate().addActionListener(e -> {
            configuratorOverview.createNewConfiguration();

            String title = configuratorOverview.configurationBuilder.getConfiguration().getTitle();
            if (title == null || title.isBlank()) title = "Konfiguration";
            JButton cfgBtn = new JButton(title);
            cfgBtn.addActionListener(ev -> mainView.showPage(configurationPanel));
            configuratorPanel.getPanelConfigurationButtons().add(cfgBtn);
            configuratorPanel.getPanelConfigurationButtons().revalidate();
            configuratorPanel.getPanelConfigurationButtons().repaint();

            configurationController.setCurrentConfigButton(cfgBtn);

            mainView.showPage(configurationPanel);
        });
    }
}
