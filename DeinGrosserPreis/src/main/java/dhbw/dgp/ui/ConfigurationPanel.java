package dhbw.dgp.ui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ConfigurationPanel extends JPanel {

    public JTextField textFieldConfigurationTitle = new JTextField(20);

    public JFormattedTextField textFieldNumberOfQuestions;
    public JSpinner spinnerNumberOfQuestions;

    public JTextField textFieldCategoryName = new JTextField(20);
    public JButton buttonAddCategory = new JButton("hinzufügen");

    private JLabel labelCategories = new JLabel("Kategorien");
    public JPanel panelCategoryButtons = new JPanel();

    public JButton buttonDone = new JButton("Fertig");

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
        panel.add(new JLabel("Neue Konfiguration"));
        return  panel;
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
        panel.add(panelAddCategory);

        return  panel;
    }

    private JPanel CategoryButtons()
    {
        panelCategoryButtons.setLayout(new BoxLayout(panelCategoryButtons, BoxLayout.Y_AXIS));
        panelCategoryButtons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        panelCategoryButtons.add(labelCategories);
        return  panelCategoryButtons;
    }

    private JPanel Footer(){
        JPanel panel = new JPanel();
        panel.add(buttonDone);
        return  panel;
    }

    private void InitNumberOfQuestionsField() // TODO: is that correct? -> KI
    {
        // Initialize spinner with arrows and step +/-1, min 1, max 100
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 100, 1);
        spinnerNumberOfQuestions = new JSpinner(model);
        // Use number editor without grouping
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerNumberOfQuestions, "#");
        spinnerNumberOfQuestions.setEditor(editor);
        textFieldNumberOfQuestions = editor.getTextField();
        textFieldNumberOfQuestions.setColumns(10);

        // Allow temporary empty/invalid input so the user can clear with Delete/Backspace
        javax.swing.JFormattedTextField.AbstractFormatter fmt = textFieldNumberOfQuestions.getFormatter();
        if (fmt instanceof javax.swing.text.NumberFormatter nf) {
            nf.setAllowsInvalid(true);     // permit clearing field
            nf.setOverwriteMode(false);    // normal insert mode
            nf.setCommitsOnValidEdit(true);
        }
    }
}
