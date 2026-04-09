import javax.swing.*;

public class ConfiguratorController
{
    public ConfiguratorController(ConfiguratorPanel configuratorPanel, MainView mainView, ConfigurationPanel configurationPanel, ConfiguratorOverview configuratorOverview, ConfigurationController configurationController) {

        configuratorPanel.buttonCreate.addActionListener(e -> {
            configuratorOverview.createNewConfiguration();

            String title = configuratorOverview.configurationBuilder.getConfiguration().getTitle();
            if (title == null || title.isBlank()) title = "Konfiguration";
            JButton cfgBtn = new JButton(title);
            cfgBtn.addActionListener(ev -> mainView.showPage(configurationPanel));
            configuratorPanel.panelConfigurationButtons.add(cfgBtn);
            configuratorPanel.panelConfigurationButtons.revalidate();
            configuratorPanel.panelConfigurationButtons.repaint();

            configurationController.setCurrentConfigButton(cfgBtn);

            mainView.showPage(configurationPanel);
        });
    }
}
