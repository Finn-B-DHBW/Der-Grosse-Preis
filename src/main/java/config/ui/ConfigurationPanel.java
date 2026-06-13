package config.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigurationPanel extends JPanel {

    private final JTextField textFieldConfigurationTitle = new JTextField(20);

    private JFormattedTextField textFieldNumberOfQuestions;
    private JSpinner spinnerNumberOfQuestions;

    private final JTextField textFieldCategoryName = new JTextField(20);
    private final JButton buttonAddCategory    = new JButton("hinzufügen");
    private final JButton buttonAutoGenerate   = new JButton("Auto-generieren");

    private final JLabel labelTitle = new JLabel("Neue Konfiguration");
    private final JLabel labelCategories = new JLabel("Kategorien");
    private final JPanel panelCategoryButtons = new JPanel();

    private final JButton buttonSave   = new JButton("Speichern");
    private final JButton buttonCancel = new JButton("Abbrechen");

    private static final int DEFAULT_QUESTIONS_PER_CATEGORY = 5;

    public JTextField getTextFieldConfigurationTitle() { return textFieldConfigurationTitle; }
    public JFormattedTextField getTextFieldNumberOfQuestions() { return textFieldNumberOfQuestions; }
    public JSpinner getSpinnerNumberOfQuestions() { return spinnerNumberOfQuestions; }
    public JTextField getTextFieldCategoryName() { return textFieldCategoryName; }
    public JButton getButtonAddCategory()  { return buttonAddCategory; }
    public JButton getButtonAutoGenerate() { return buttonAutoGenerate; }
    public JPanel getPanelCategoryButtons() { return panelCategoryButtons; }
    public JButton getButtonSave()   { return buttonSave; }
    public JButton getButtonCancel() { return buttonCancel; }

    public ConfigurationPanel() {
        setLayout(new BorderLayout());

        InitNumberOfQuestionsField();

        add(Header(),  BorderLayout.NORTH);
        add(Center(), BorderLayout.CENTER);
        add(Footer(),  BorderLayout.SOUTH);
    }

    private JPanel Header()
    {
        JPanel panel = new JPanel();
        panel.add(labelTitle);
        return  panel;
    }

    public void setHeaderTitle(String title) {
        labelTitle.setText(title);
    }

    public void clearForNew() {
        labelTitle.setText("Neue Konfiguration");
        textFieldConfigurationTitle.setText("");
        spinnerNumberOfQuestions.setValue(DEFAULT_QUESTIONS_PER_CATEGORY);
        textFieldCategoryName.setText("");
        clearCategoryArea();
    }

    public void clearCategoryArea() {
        panelCategoryButtons.removeAll();
        panelCategoryButtons.add(labelCategories);
        panelCategoryButtons.revalidate();
        panelCategoryButtons.repaint();
    }

    private JPanel Center(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(TextFields(),BorderLayout.NORTH);
        panel.add(CategoryButtons(),BorderLayout.CENTER);

        return  panel;
    }

    private JPanel TextFields()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,1));

        JPanel panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitle.add(new JLabel("Titel des Spiels: "));
        panelTitle.add(textFieldConfigurationTitle);
        panel.add(panelTitle);

        JPanel panelNumber = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNumber.add(new JLabel("Anzahl Frage pro Kategorie: "));
        panelNumber.add(spinnerNumberOfQuestions);
        panel.add(panelNumber);

        JPanel panelAddCategory = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAddCategory.add(new JLabel("Neue Kategorie: "));
        panelAddCategory.add(textFieldCategoryName);
        panelAddCategory.add(buttonAddCategory);
        panelAddCategory.add(buttonAutoGenerate);
        panel.add(panelAddCategory);

        return  panel;
    }

    private JComponent CategoryButtons()
    {
        panelCategoryButtons.setLayout(new BoxLayout(panelCategoryButtons, BoxLayout.Y_AXIS));
        panelCategoryButtons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        panelCategoryButtons.setAlignmentX(LEFT_ALIGNMENT);
        panelCategoryButtons.add(labelCategories);

        JPanel scrollableWrapper = new JPanel(new BorderLayout());
        scrollableWrapper.add(panelCategoryButtons, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollableWrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return  scrollPane;
    }

    private JPanel Footer(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panel.add(buttonSave);
        panel.add(buttonCancel);
        return panel;
    }

    private void InitNumberOfQuestionsField()
    {
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 100, 1);
        spinnerNumberOfQuestions = new JSpinner(model);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerNumberOfQuestions, "#");
        spinnerNumberOfQuestions.setEditor(editor);
        textFieldNumberOfQuestions = editor.getTextField();
        textFieldNumberOfQuestions.setColumns(10);

        javax.swing.JFormattedTextField.AbstractFormatter fmt = textFieldNumberOfQuestions.getFormatter();
        if (fmt instanceof javax.swing.text.NumberFormatter nf) {
            nf.setAllowsInvalid(true);
            nf.setOverwriteMode(false);
            nf.setCommitsOnValidEdit(true);
        }
    }
}
