package config.ui;

import config.ConfiguratorOverview;
import config.model.Configuration;

import javax.swing.*;

public class ConfiguratorController
{
    private final ConfiguratorPanel configuratorPanel;
    private final MainView mainView;
    private final ConfigurationPanel configurationPanel;
    private final ConfiguratorOverview configuratorOverview;
    private final ConfigurationController configurationController;

    public ConfiguratorController(ConfiguratorPanel configuratorPanel, MainView mainView, ConfigurationPanel configurationPanel, ConfiguratorOverview configuratorOverview, ConfigurationController configurationController) {
        this.configuratorPanel = configuratorPanel;
        this.mainView = mainView;
        this.configurationPanel = configurationPanel;
        this.configuratorOverview = configuratorOverview;
        this.configurationController = configurationController;

        wireToolbar();
        refreshConfigurationList();
    }

    public void refreshConfigurationList() {
        configuratorOverview.refreshConfigurations();
        Configuration selected = configuratorOverview.getCurrentConfiguration();
        Integer selectedId = selected == null ? null : selected.getConfigId();
        configuratorPanel.refreshConfigurationButtons(
                configuratorOverview.getConfigurations(),
                selectedId,
                this::onConfigurationSelected);
    }

    private void onConfigurationSelected(Integer configId) {
        configuratorOverview.setCurrentConfigurationById(configId);
        Configuration selected = configuratorOverview.getCurrentConfiguration();
        Integer selectedId = selected == null ? null : selected.getConfigId();
        configuratorPanel.refreshConfigurationButtons(
                configuratorOverview.getConfigurations(),
                selectedId,
                this::onConfigurationSelected);
    }

    private void wireToolbar() {
        configuratorPanel.getButtonCreate().addActionListener(e -> {
            configuratorOverview.clearSelection();
            configuratorOverview.createNewConfiguration();
            configurationController.prepareForNew();
            mainView.showPage(configurationPanel);
        });

        configuratorPanel.getButtonEdit().addActionListener(e -> {
            Configuration selected = configuratorOverview.getCurrentConfiguration();
            if (selected == null) return;
            configuratorOverview.editConfiguration();
            configurationController.loadFromConfiguration(selected);
            mainView.showPage(configurationPanel);
        });

        configuratorPanel.getButtonDuplicate().addActionListener(e -> {
            Configuration selected = configuratorOverview.getCurrentConfiguration();
            if (selected == null) return;
            configuratorOverview.duplicateConfiguration();
            refreshConfigurationList();
        });

        configuratorPanel.getButtonDelete().addActionListener(e -> {
            Configuration selected = configuratorOverview.getCurrentConfiguration();
            if (selected == null) return;
            String name = (selected.getTitle() == null || selected.getTitle().isBlank())
                    ? "Konfiguration " + selected.getConfigId()
                    : selected.getTitle();
            int choice = JOptionPane.showConfirmDialog(configuratorPanel,
                    "Konfiguration \"" + name + "\" wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                configuratorOverview.deleteConfiguration();
                refreshConfigurationList();
            }
        });
    }
}
