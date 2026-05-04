package config.ui;

import javax.swing.*;
import java.awt.*;

public class ConfiguratorPanel extends JPanel {

    private JButton buttonCreate;
    private JButton buttonEdit;
    private JButton buttonDuplicate;
    private JButton buttonDelete;

    private final JLabel labelConfigurations = new JLabel("Konfigurationen");
    private final JPanel panelConfigurationButtons = new JPanel();

    public JButton getButtonCreate() { return buttonCreate; }
    public JPanel getPanelConfigurationButtons() { return panelConfigurationButtons; }

    public ConfiguratorPanel() {
        setLayout(new BorderLayout());

        add(Header(),  BorderLayout.NORTH);
        add(Configurations(),  BorderLayout.CENTER);
    }

    private JPanel Header(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Konfigurator Übersicht"),  BorderLayout.NORTH);
        panel.add(UserSelectionButtons(), BorderLayout.CENTER);
        return  panel;
    }

    private JPanel UserSelectionButtons(){
        JPanel panel = new JPanel();

        this.buttonCreate = new JButton("Neue Konfiguration erstellen");
        this.buttonEdit = new JButton("Konfiguration bearbeiten");
        this.buttonDuplicate = new JButton("Konfiguration duplizieren");
        this.buttonDelete = new JButton("Konfiguration löschen");

        panel.add(this.buttonCreate);
        panel.add(this.buttonEdit);
        panel.add(this.buttonDuplicate);
        panel.add(this.buttonDelete);

        return panel;
    }

    private JPanel Configurations(){
        panelConfigurationButtons.setLayout(new BoxLayout(panelConfigurationButtons, BoxLayout.Y_AXIS));
        panelConfigurationButtons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        panelConfigurationButtons.add(labelConfigurations);
        return panelConfigurationButtons;
    }
}
