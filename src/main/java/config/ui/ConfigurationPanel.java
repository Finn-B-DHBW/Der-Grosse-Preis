package config.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigurationPanel extends JPanel {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final JTextField textFieldConfigurationTitle = new JTextField(14);

    private JFormattedTextField textFieldNumberOfQuestions;
    private JSpinner spinnerNumberOfQuestions;

    private final JTextField textFieldCategoryName = new JTextField(14);
    private final JButton buttonAddCategory  = new JButton("Hinzufügen");
    private final JButton buttonAutoGenerate = new JButton("Generieren");

    private final JLabel labelTitle      = new JLabel("Neues Spiel");
    private final JPanel panelCategoryButtons = new JPanel();

    private final JButton buttonSave   = new JButton("Speichern");
    private final JButton buttonCancel = new JButton("Abbrechen");

    private static final int   DEFAULT_QUESTIONS_PER_CATEGORY = 5;
    private static final float FONT_LABEL   = 15f;
    private static final float FONT_TITLE   = 18f;
    private static final int   BTN_HEIGHT   = 36;
    private static final Color BORDER_CLR   = new Color(226, 232, 240);  // slate-200
    private static final Color HDR_BG       = new Color(191, 219, 254);  // blue-200
    private static final Color HDR_BORDER   = new Color(147, 197, 253);  // blue-300
    private static final Color HDR_FG       = new Color(30,   64, 175);  // blue-800

    // ── Public API ────────────────────────────────────────────────────────────
    public JTextField         getTextFieldConfigurationTitle()  { return textFieldConfigurationTitle; }
    public JFormattedTextField getTextFieldNumberOfQuestions()  { return textFieldNumberOfQuestions; }
    public JSpinner            getSpinnerNumberOfQuestions()    { return spinnerNumberOfQuestions; }
    public JTextField         getTextFieldCategoryName()        { return textFieldCategoryName; }
    public JButton            getButtonAddCategory()            { return buttonAddCategory; }
    public JButton            getButtonAutoGenerate()           { return buttonAutoGenerate; }
    public JPanel             getPanelCategoryButtons()         { return panelCategoryButtons; }
    public JButton            getButtonSave()                   { return buttonSave; }
    public JButton            getButtonCancel()                 { return buttonCancel; }

    // ── Constructor ───────────────────────────────────────────────────────────
    public ConfigurationPanel() {
        setLayout(new BorderLayout());
        // No outer border — header stretches full-width; margins live in each section

        initNumberOfQuestionsField();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── State helpers ─────────────────────────────────────────────────────────
    public void setHeaderTitle(String title) {
        labelTitle.setText(title);
    }

    public void clearForNew() {
        labelTitle.setText("Neues Spiel");
        textFieldConfigurationTitle.setText("");
        spinnerNumberOfQuestions.setValue(DEFAULT_QUESTIONS_PER_CATEGORY);
        textFieldCategoryName.setText("");
        clearCategoryArea();
    }

    public void clearCategoryArea() {
        panelCategoryButtons.removeAll();
        panelCategoryButtons.revalidate();
        panelCategoryButtons.repaint();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        labelTitle.setFont(labelTitle.getFont().deriveFont(Font.BOLD, FONT_TITLE));
        labelTitle.setForeground(HDR_FG);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HDR_BG);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, HDR_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        panel.add(labelTitle, BorderLayout.WEST);
        return panel;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        // Left blue sidebar — same blue-50 as the header, spans full height
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(HDR_BG);
        sidebar.setOpaque(true);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.add(buildFormFields(), BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sidebar,             BorderLayout.WEST);
        panel.add(buildCategoryArea(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFormFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // ── Titel des Spiels ──────────────────────────────────────────────────
        JLabel l1 = styledLabel("Titel des Spiels:");
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l1);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        textFieldConfigurationTitle.setFont(
                textFieldConfigurationTitle.getFont().deriveFont(FONT_LABEL));
        textFieldConfigurationTitle.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        textFieldConfigurationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textFieldConfigurationTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                textFieldConfigurationTitle.getPreferredSize().height));
        panel.add(textFieldConfigurationTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        // ── Anzahl Fragen pro Kategorie ───────────────────────────────────────
        JLabel l2 = styledLabel("Anzahl Fragen pro Kategorie:");
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l2);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        JPanel spinnerWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spinnerWrap.setBackground(HDR_BG);
        spinnerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinnerWrap.add(spinnerNumberOfQuestions);
        panel.add(spinnerWrap);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        // ── Neue Kategorie ────────────────────────────────────────────────────
        JLabel l3 = styledLabel("Neue Kategorie:");
        l3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l3);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        textFieldCategoryName.setFont(
                textFieldCategoryName.getFont().deriveFont(FONT_LABEL));
        textFieldCategoryName.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        textFieldCategoryName.setAlignmentX(Component.LEFT_ALIGNMENT);
        textFieldCategoryName.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                textFieldCategoryName.getPreferredSize().height));
        panel.add(textFieldCategoryName);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        styleButton(buttonAddCategory);
        styleButton(buttonAutoGenerate);
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 6, 0));
        buttonRow.setBackground(HDR_BG);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, BTN_HEIGHT));
        buttonRow.add(buttonAddCategory);
        buttonRow.add(buttonAutoGenerate);
        panel.add(buttonRow);

        return panel;
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, FONT_LABEL));
        lbl.setForeground(new Color(51, 65, 85));   // slate-700
        return lbl;
    }

    private void styleButton(JButton btn) {
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, FONT_LABEL));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, BTN_HEIGHT));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
    }

    // ── Category area (scrollable) ────────────────────────────────────────────
    private JComponent buildCategoryArea() {
        Color catBg = new Color(241, 245, 249);  // slate-100 — subtle tint, rows float as white cards

        JLabel sectionLabel = new JLabel("Kategorien");
        sectionLabel.setFont(sectionLabel.getFont().deriveFont(Font.BOLD, FONT_LABEL));
        sectionLabel.setForeground(new Color(51, 65, 85));   // slate-700

        panelCategoryButtons.setLayout(new BoxLayout(panelCategoryButtons, BoxLayout.Y_AXIS));
        panelCategoryButtons.setAlignmentX(LEFT_ALIGNMENT);
        panelCategoryButtons.setBackground(catBg);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(catBg);
        scrollWrapper.add(panelCategoryButtons, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(catBg);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(catBg);
        wrapper.setOpaque(true);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        wrapper.add(sectionLabel, BorderLayout.NORTH);
        wrapper.add(scrollPane,   BorderLayout.CENTER);
        return wrapper;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        styleButton(buttonSave);
        buttonSave.setBackground(new Color(59, 130, 246));
        buttonSave.setForeground(Color.WHITE);

        styleButton(buttonCancel);
        buttonCancel.setBackground(new Color(241, 245, 249));  // slate-100
        buttonCancel.setForeground(new Color(100, 116, 139));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        panel.add(buttonSave);
        panel.add(buttonCancel);
        return panel;
    }

    // ── Spinner init ──────────────────────────────────────────────────────────
    private void initNumberOfQuestionsField() {
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 100, 1);
        spinnerNumberOfQuestions = new JSpinner(model);
        spinnerNumberOfQuestions.setFont(spinnerNumberOfQuestions.getFont().deriveFont(FONT_LABEL));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerNumberOfQuestions, "#");
        spinnerNumberOfQuestions.setEditor(editor);
        textFieldNumberOfQuestions = editor.getTextField();
        textFieldNumberOfQuestions.setColumns(6);
        textFieldNumberOfQuestions.setFont(textFieldNumberOfQuestions.getFont().deriveFont(FONT_LABEL));

        javax.swing.JFormattedTextField.AbstractFormatter fmt =
                textFieldNumberOfQuestions.getFormatter();
        if (fmt instanceof javax.swing.text.NumberFormatter nf) {
            nf.setAllowsInvalid(true);
            nf.setOverwriteMode(false);
            nf.setCommitsOnValidEdit(true);
        }
    }
}
