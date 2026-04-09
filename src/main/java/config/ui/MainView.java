package config.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<JPanel, String> panelNames;

    public MainView() {
        super("Dein großer Preis - Konfigurator");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        panelNames = new HashMap<>();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(cardPanel, BorderLayout.CENTER);
    }

    public void addPage(String name, JPanel panel) {
        panelNames.put(panel, name);
        cardPanel.add(panel, name);
    }

    public void showPage(JPanel panel) {
        String name = panelNames.get(panel);
        if (name == null) {
            throw new IllegalArgumentException("Panel not added to MainView: " + panel);
        }
        cardLayout.show(cardPanel, name);
    }
}
