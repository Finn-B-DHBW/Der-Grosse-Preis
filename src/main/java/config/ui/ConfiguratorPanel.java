package config.ui;

import config.model.Configuration;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfiguratorPanel extends JPanel {

    // ── Buttons ──────────────────────────────────────────────────────────────
    private JButton buttonCreate;
    private JButton buttonEdit;
    private JButton buttonDuplicate;
    private JButton buttonDelete;

    // ── Config-button list ────────────────────────────────────────────────────
    private final JPanel panelConfigurationButtons = new JPanel();
    private final List<JButton>         configButtons    = new ArrayList<>();
    private final Map<JButton, Integer> buttonToConfigId = new LinkedHashMap<>();

    // ── Selection borders ─────────────────────────────────────────────────────
    private static final Border SELECTED_BORDER =
            BorderFactory.createLineBorder(new Color(0, 120, 215), 2);
    private static final Border DEFAULT_BORDER =
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1);

    // ── Sizing constants ──────────────────────────────────────────────────────
    private static final int   CONFIG_BTN_HEIGHT = 52;
    private static final int   ACTION_BTN_WIDTH  = 220;
    private static final int   ACTION_BTN_HEIGHT = 52;
    private static final int   ACTION_GAP        = 10;
    private static final float FONT_NORMAL       = 16f;
    private static final float FONT_HEADER_BTN   = 17f;

    // ── Public API ────────────────────────────────────────────────────────────
    public JButton getButtonCreate()              { return buttonCreate; }
    public JButton getButtonEdit()                { return buttonEdit; }
    public JButton getButtonDuplicate()           { return buttonDuplicate; }
    public JButton getButtonDelete()              { return buttonDelete; }
    public JPanel  getPanelConfigurationButtons() { return panelConfigurationButtons; }

    // ── Constructor ───────────────────────────────────────────────────────────
    public ConfiguratorPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        updateToolbarEnablement(null);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    // Full-width "Neue Konfiguration erstellen" button, bold, slightly taller
    private JPanel buildHeader() {
        buttonCreate = new JButton("+ Neues Spiel erstellen");
        buttonCreate.setFont(buttonCreate.getFont().deriveFont(Font.BOLD, FONT_HEADER_BTN));
        buttonCreate.setFocusPainted(false);
        buttonCreate.setPreferredSize(new Dimension(0, 50));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panel.add(buttonCreate, BorderLayout.CENTER);
        return panel;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    // Two columns: scrollable config list (CENTER) + action buttons (EAST)
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.add(buildConfigList(),   BorderLayout.CENTER);
        panel.add(buildActionColumn(), BorderLayout.EAST);
        return panel;
    }

    // Left column ─ scrollable list of saved configurations
    private JPanel buildConfigList() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JLabel header = new JLabel("Spiele");
        header.setFont(header.getFont().deriveFont(Font.BOLD, FONT_NORMAL));
        panel.add(header, BorderLayout.NORTH);

        panelConfigurationButtons.setLayout(
                new BoxLayout(panelConfigurationButtons, BoxLayout.Y_AXIS));

        // Scroll-pane wrapper (NORTH trick keeps buttons packed at top)
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panelConfigurationButtons, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Right column ─ Bearbeiten / Duplizieren / Löschen (always same size)
    private JPanel buildActionColumn() {
        buttonEdit      = makeActionButton("Bearbeiten");
        buttonDuplicate = makeActionButton("Duplizieren");
        buttonDelete    = makeActionButton("Löschen");

        // GridLayout: all three cells always identical in size
        JPanel grid = new JPanel(new GridLayout(3, 1, 0, ACTION_GAP));
        grid.add(buttonEdit);
        grid.add(buttonDuplicate);
        grid.add(buttonDelete);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Invisible spacer matching the "Spiele" header label height + gap on the left
        JLabel spacer = new JLabel(" ");
        spacer.setFont(spacer.getFont().deriveFont(Font.BOLD, FONT_NORMAL));
        spacer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.add(spacer);
        inner.add(Box.createRigidArea(new Dimension(0, 8)));
        inner.add(grid);
        inner.add(Box.createVerticalGlue());

        // Fixed-width outer wrapper so the column doesn't shrink/grow
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(ACTION_BTN_WIDTH, 0));
        outer.add(inner, BorderLayout.NORTH);
        return outer;
    }

    private JButton makeActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, FONT_NORMAL));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(ACTION_BTN_WIDTH, ACTION_BTN_HEIGHT));
        return btn;
    }

    // ── Dynamic config list ───────────────────────────────────────────────────

    public void refreshConfigurationButtons(List<Configuration> configurations,
                                            Integer selectedConfigId,
                                            ConfigurationButtonClickListener listener) {
        panelConfigurationButtons.removeAll();
        configButtons.clear();
        buttonToConfigId.clear();

        if (configurations != null) {
            for (Configuration cfg : configurations) {
                String label = (cfg.getTitle() == null || cfg.getTitle().isBlank())
                        ? "Spiel " + cfg.getConfigId()
                        : cfg.getTitle();

                JButton btn = new JButton(label);
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN, FONT_NORMAL));
                btn.setBorder(DEFAULT_BORDER);
                btn.setFocusPainted(false);
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, CONFIG_BTN_HEIGHT));
                btn.setPreferredSize(new Dimension(100, CONFIG_BTN_HEIGHT));  // min width; grows with panel
                btn.addActionListener(e -> {
                    if (listener != null) listener.onConfigurationSelected(cfg.getConfigId());
                });

                configButtons.add(btn);
                buttonToConfigId.put(btn, cfg.getConfigId());
                panelConfigurationButtons.add(btn);
                panelConfigurationButtons.add(Box.createRigidArea(new Dimension(0, 4)));
            }
        }

        applySelectionHighlight(selectedConfigId);
        updateToolbarEnablement(selectedConfigId);
        panelConfigurationButtons.revalidate();
        panelConfigurationButtons.repaint();
    }

    private void applySelectionHighlight(Integer selectedConfigId) {
        for (JButton btn : configButtons) {
            Integer id = buttonToConfigId.get(btn);
            boolean sel = selectedConfigId != null && selectedConfigId.equals(id);
            btn.setBorder(sel ? SELECTED_BORDER : DEFAULT_BORDER);
        }
    }

    private void updateToolbarEnablement(Integer selectedConfigId) {
        boolean has = selectedConfigId != null;
        if (buttonEdit      != null) buttonEdit.setEnabled(has);
        if (buttonDuplicate != null) buttonDuplicate.setEnabled(has);
        if (buttonDelete    != null) buttonDelete.setEnabled(has);
    }

    // ── Listener interface ────────────────────────────────────────────────────

    public interface ConfigurationButtonClickListener {
        void onConfigurationSelected(Integer configId);
    }
}
