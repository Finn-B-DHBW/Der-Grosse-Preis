package config.ui;

import config.model.Configuration;

import javax.swing.*;
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

    // ── Colors ───────────────────────────────────────────────────────────────
    static final Color PRIMARY      = new Color(59, 130, 246);   // blue-500
    static final Color DANGER       = new Color(239, 68,  68);   // red-500
    static final Color SURFACE      = new Color(248, 250, 252);  // slate-50
    static final Color BORDER_CLR   = new Color(226, 232, 240);  // slate-200
    static final Color TEXT_DARK    = new Color(15,  23,  42);   // slate-900

    // ── Selection colors (border-free) ───────────────────────────────────────
    private static final Color SEL_BG     = new Color(59, 130, 246);    // blue-500 — selected (stark)
    private static final Color DEFAULT_BG = new Color(191, 219, 254);   // blue-200 — default

    // ── Sizing constants ──────────────────────────────────────────────────────
    private static final int   CONFIG_BTN_HEIGHT = 52;
    private static final int   ACTION_BTN_WIDTH  = 220;
    private static final int   ACTION_BTN_HEIGHT = 52;
    private static final int   ACTION_GAP        = 10;
    private static final float FONT_NORMAL       = 16f;
    private static final float FONT_HEADER_BTN   = 17f;
    private static final float FONT_SECTION_HDR  = 20f;
    private static final Color HEADER_BG         = new Color(219, 234, 254);  // blue-100

    // ── Public API ────────────────────────────────────────────────────────────
    public JButton getButtonCreate()              { return buttonCreate; }
    public JButton getButtonEdit()                { return buttonEdit; }
    public JButton getButtonDuplicate()           { return buttonDuplicate; }
    public JButton getButtonDelete()              { return buttonDelete; }
    public JPanel  getPanelConfigurationButtons() { return panelConfigurationButtons; }

    // ── Constructor ───────────────────────────────────────────────────────────
    public ConfiguratorPanel() {
        setLayout(new BorderLayout());
        // No outer margin — the three colored sections fill edge-to-edge

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        updateToolbarEnablement(null);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    // Colored header band (blue-800) with the create-button floating inside
    private JPanel buildHeader() {
        buttonCreate = new JButton("+ Neues Spiel erstellen");
        buttonCreate.setFont(buttonCreate.getFont().deriveFont(Font.BOLD, FONT_HEADER_BTN));
        buttonCreate.setFocusPainted(false);
        buttonCreate.setPreferredSize(new Dimension(0, 50));
        buttonCreate.setBackground(new Color(96, 165, 250));  // blue-400 — softer than blue-500
        buttonCreate.setForeground(Color.WHITE);
        buttonCreate.setBorderPainted(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        panel.add(buttonCreate, BorderLayout.CENTER);
        return panel;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    // Two columns: scrollable config list (CENTER) + action buttons (EAST)
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildConfigList(),   BorderLayout.CENTER);
        panel.add(buildActionColumn(), BorderLayout.EAST);
        return panel;
    }

    // Left section ─ fills full height, slate-50 background
    private JPanel buildConfigList() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(SURFACE);   // slate-50
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel header = new JLabel("Spiele");
        header.setFont(header.getFont().deriveFont(Font.BOLD, FONT_SECTION_HDR));
        header.setForeground(TEXT_DARK);
        panel.add(header, BorderLayout.NORTH);

        panelConfigurationButtons.setLayout(
                new BoxLayout(panelConfigurationButtons, BoxLayout.Y_AXIS));
        panelConfigurationButtons.setBackground(SURFACE);
        panelConfigurationButtons.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SURFACE);
        wrapper.add(panelConfigurationButtons, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(SURFACE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Right section ─ fills full height, blue-50 background
    private JPanel buildActionColumn() {
        buttonEdit      = makeActionButton("Bearbeiten");
        buttonDuplicate = makeActionButton("Duplizieren");
        buttonDelete    = makeActionButton("Löschen");
        buttonDelete.setBackground(DANGER);
        buttonDelete.setForeground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(3, 1, 0, ACTION_GAP));
        grid.setOpaque(false);
        grid.add(buttonEdit);
        grid.add(buttonDuplicate);
        grid.add(buttonDelete);

        // Full-height colored section — buttons sit at top with padding
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(239, 246, 255));  // blue-50
        outer.setOpaque(true);
        outer.setPreferredSize(new Dimension(ACTION_BTN_WIDTH + 32, 0));
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        outer.add(grid, BorderLayout.NORTH);
        return outer;
    }

    private JButton makeActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, FONT_NORMAL));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
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
                btn.setFont(btn.getFont().deriveFont(Font.BOLD, FONT_NORMAL));
                btn.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setBackground(DEFAULT_BG);
                btn.setForeground(TEXT_DARK);
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
            btn.setBackground(sel ? SEL_BG : DEFAULT_BG);
            btn.setForeground(sel ? Color.WHITE : TEXT_DARK);
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
