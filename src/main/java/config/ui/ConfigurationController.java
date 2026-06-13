package config.ui;

import config.ConfiguratorOverview;
import config.QuestionPoolService;
import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationController
{
    private static final String DEFAULT_CATEGORY_NAME = "Kategorie";
    private static final String DEFAULT_CONFIG_TITLE = "Konfiguration";
    private static final Logger log = Logger.getLogger(ConfigurationController.class.getName());

    private final ConfigurationPanel configurationPanel;
    private final ConfiguratorOverview configuratorOverview;
    private final MainView mainView;
    private final ConfiguratorPanel configuratorPanel;

    private JButton currentConfigButton;
    private Runnable onDoneCallback;
    private boolean suppressSpinnerListener = false;

    public ConfigurationController(ConfigurationPanel configurationPanel, MainView mainView, ConfiguratorOverview configuratorOverview, ConfiguratorPanel configuratorPanel) {
        this.configurationPanel = configurationPanel;
        this.configuratorOverview = configuratorOverview;
        this.mainView = mainView;
        this.configuratorPanel = configuratorPanel;
        wireListeners();
    }

    public void setCurrentConfigButton(JButton button) {
        this.currentConfigButton = button;
    }

    public void setOnDoneCallback(Runnable callback) {
        this.onDoneCallback = callback;
    }

    public void prepareForNew() {
        suppressSpinnerListener = true;
        try {
            configurationPanel.clearForNew();
            configurationPanel.setHeaderTitle("Neue Konfiguration");
        } finally {
            suppressSpinnerListener = false;
        }
    }

    public void loadFromConfiguration(Configuration config) {
        if (config == null) {
            prepareForNew();
            return;
        }
        int questionsPerCategory = detectQuestionsPerCategory(config);

        suppressSpinnerListener = true;
        try {
            configurationPanel.clearForNew();
            configurationPanel.setHeaderTitle("Konfiguration bearbeiten");
            configurationPanel.getTextFieldConfigurationTitle().setText(
                    config.getTitle() == null ? "" : config.getTitle());
            configurationPanel.getSpinnerNumberOfQuestions().setValue(questionsPerCategory);
        } finally {
            suppressSpinnerListener = false;
        }

        if (configuratorOverview.configurationBuilder != null) {
            configuratorOverview.configurationBuilder.setNumberOfQuestions(questionsPerCategory);
        }

        for (Category cat : config.getCategories()) {
            appendCategoryRow(cat);
        }
        propagateLayoutUp(configurationPanel.getPanelCategoryButtons());
    }

    private int detectQuestionsPerCategory(Configuration config) {
        if (config == null || config.getCategories() == null || config.getCategories().isEmpty()) {
            return 5;
        }
        int max = 0;
        for (Category cat : config.getCategories()) {
            if (cat.getPointQuestionMap() != null) {
                max = Math.max(max, cat.getPointQuestionMap().size());
            }
        }
        return Math.max(1, max);
    }

    private boolean hasBuilder() {
        return configuratorOverview != null && configuratorOverview.configurationBuilder != null;
    }

    private Category getLastCategory() {
        if (!hasBuilder()) return null;
        Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
        List<Category> cats = cfg.getCategories();
        if (cats == null || cats.isEmpty()) return null;
        return cats.get(cats.size() - 1);
    }

    private void updateConfigButtonTitle() {
        if (currentConfigButton == null || !hasBuilder()) return;
        String title = configuratorOverview.configurationBuilder.getConfiguration().getTitle();
        if (title == null || title.isBlank()) title = DEFAULT_CONFIG_TITLE;
        currentConfigButton.setText(title);
        currentConfigButton.revalidate();
        currentConfigButton.repaint();
    }

    private void wireListeners() {
        configurationPanel.getButtonSave().addActionListener(e -> {
            if (!validateBeforeSave()) return;
            configuratorOverview.saveConfigurationToDatabase();
            if (onDoneCallback != null) onDoneCallback.run();
            mainView.showPage(configuratorPanel);
        });

        configurationPanel.getButtonCancel().addActionListener(e -> {
            configuratorOverview.clearSelection();
            if (onDoneCallback != null) onDoneCallback.run();
            mainView.showPage(configuratorPanel);
        });

        configurationPanel.getTextFieldConfigurationTitle().addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!hasBuilder()) return;
                configuratorOverview.configurationBuilder
                        .setTitle(configurationPanel.getTextFieldConfigurationTitle().getText());
                updateConfigButtonTitle();
            }
        });

        configurationPanel.getTextFieldNumberOfQuestions().addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                applyNumberOfQuestionsFromTextField();
            }
        });

        JSpinner spinner = configurationPanel.getSpinnerNumberOfQuestions();
        if (spinner != null) {
            spinner.addChangeListener(e -> {
                if (!hasBuilder()) return;
                if (suppressSpinnerListener) return;
                Object v = spinner.getValue();
                if (!(v instanceof Number)) return;
                int newCount = Math.max(1, ((Number) v).intValue());

                if (wouldLoseFilledQuestions(newCount)) {
                    int choice = JOptionPane.showConfirmDialog(configurationPanel,
                            "Beim Reduzieren werden befüllte Fragen gelöscht. Trotzdem fortfahren?",
                            "Fragen werden gelöscht",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        int revertTo = detectQuestionsPerCategory(
                                configuratorOverview.configurationBuilder.getConfiguration());
                        suppressSpinnerListener = true;
                        try {
                            spinner.setValue(revertTo);
                        } finally {
                            suppressSpinnerListener = false;
                        }
                        return;
                    }
                }

                configuratorOverview.configurationBuilder.setNumberOfQuestions(newCount);
                applyQuestionCountToAllCategories(newCount);
            });
        }

        configurationPanel.getButtonAddCategory().addActionListener(e -> {
            if (!hasBuilder()) return;
            configuratorOverview.configurationBuilder
                    .createNewCategory(configurationPanel.getTextFieldCategoryName().getText());
            configurationPanel.getTextFieldCategoryName().setText("");
            addCategoryPanel();
        });

        configurationPanel.getButtonAutoGenerate().addActionListener(e -> {
            if (!hasBuilder()) return;

            List<String> categories = QuestionPoolService.getAvailableCategories();
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(configurationPanel,
                        "Keine Fragen im Pool gefunden.\n" +
                        "Bitte das Spiel einmal starten, damit die Fragen in die Datenbank geladen werden.",
                        "Fragenpool leer",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> combo = new JComboBox<>(categories.toArray(new String[0]));
            int choice = JOptionPane.showConfirmDialog(
                    configurationPanel,
                    combo,
                    "Kategorie aus Fragenpool wählen",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) return;

            String selected = (String) combo.getSelectedItem();
            if (selected == null) return;

            int count = Math.max(1,
                    ((Number) configurationPanel.getSpinnerNumberOfQuestions().getValue()).intValue());

            Category newCat = QuestionPoolService.generateCategory(selected, count);

            if (newCat.getPointQuestionMap() == null || newCat.getPointQuestionMap().isEmpty()) {
                JOptionPane.showMessageDialog(configurationPanel,
                        "Für die Kategorie \"" + selected + "\" wurden keine Fragen gefunden.",
                        "Keine Fragen",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            configuratorOverview.configurationBuilder.getConfiguration().addCategory(newCat);
            appendCategoryRow(newCat);
            propagateLayoutUp(configurationPanel.getPanelCategoryButtons());
        });
    }

    private boolean validateBeforeSave() {
        if (!hasBuilder()) return false;
        Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
        if (cfg == null) return false;
        if (cfg.getCategories() == null || cfg.getCategories().isEmpty()) {
            JOptionPane.showMessageDialog(configurationPanel,
                    "Bitte mindestens eine Kategorie anlegen.",
                    "Speichern nicht möglich",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String title = cfg.getTitle();
        if (title == null || title.isBlank()) {
            int choice = JOptionPane.showConfirmDialog(configurationPanel,
                    "Kein Titel vergeben. Trotzdem speichern?",
                    "Hinweis",
                    JOptionPane.YES_NO_OPTION);
            return choice == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private boolean wouldLoseFilledQuestions(int newCount) {
        if (!hasBuilder()) return false;
        Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
        if (cfg == null) return false;
        for (Category cat : cfg.getCategories()) {
            Map<Integer, ConfigQuestion> map = cat.getPointQuestionMap();
            if (map == null || map.size() <= newCount) continue;
            List<Integer> sortedKeys = new ArrayList<>(map.keySet());
            int countToRemove = map.size() - newCount;
            for (int i = 0; i < countToRemove; i++) {
                Integer key = sortedKeys.get(sortedKeys.size() - 1 - i);
                if (isFilled(map.get(key))) return true;
            }
        }
        return false;
    }

    private boolean isFilled(ConfigQuestion q) {
        if (q == null) return false;
        if (q.getQuestion() != null && !q.getQuestion().isBlank()) return true;
        if (q.getAnswers() != null) {
            for (String a : q.getAnswers()) {
                if (a != null && !a.isBlank()) return true;
            }
        }
        return false;
    }

    private void applyQuestionCountToAllCategories(int newCount) {
        if (!hasBuilder()) return;
        Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
        if (cfg == null) return;

        boolean anyChange = false;
        for (Category cat : cfg.getCategories()) {
            Map<Integer, ConfigQuestion> map = cat.getPointQuestionMap();
            if (map == null) continue;
            int currentSize = map.size();
            if (currentSize == newCount) continue;

            if (newCount > currentSize) {
                int highestPoints = 0;
                for (Integer pts : map.keySet()) {
                    highestPoints = Math.max(highestPoints, pts);
                }
                int step = 10;
                for (int i = 1; i <= newCount - currentSize; i++) {
                    map.put(highestPoints + i * step, new ConfigQuestion(3));
                }
            } else {
                List<Integer> sortedKeys = new ArrayList<>(map.keySet());
                int countToRemove = currentSize - newCount;
                for (int i = 0; i < countToRemove; i++) {
                    Integer keyToRemove = sortedKeys.get(sortedKeys.size() - 1 - i);
                    map.remove(keyToRemove);
                }
            }
            anyChange = true;
        }

        if (anyChange) {
            rebuildCategoryArea(cfg);
        }
    }

    private void rebuildCategoryArea(Configuration cfg) {
        configurationPanel.clearCategoryArea();
        if (cfg != null) {
            for (Category cat : cfg.getCategories()) {
                appendCategoryRow(cat);
            }
        }
        propagateLayoutUp(configurationPanel.getPanelCategoryButtons());
    }

    private void propagateLayoutUp(java.awt.Component start) {
        java.awt.Component c = start;
        while (c != null) {
            if (c instanceof JComponent) {
                ((JComponent) c).revalidate();
            }
            c.repaint();
            if (c instanceof JScrollPane) break;
            c = c.getParent();
        }
    }

    private void applyNumberOfQuestionsFromTextField() {
        if (!hasBuilder()) return;
        JFormattedTextField field = configurationPanel.getTextFieldNumberOfQuestions();
        Object valueObj = field.getValue();
        if (valueObj instanceof Number) {
            configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) valueObj).intValue());
            return;
        }
        String text = field.getText();
        if (text == null || text.isBlank()) return;
        try {
            int value = Integer.parseInt(text.trim());
            configuratorOverview.configurationBuilder.setNumberOfQuestions(value);
        } catch (NumberFormatException ex) {
            log.log(Level.FINE, "Invalid input for number of questions: " + text, ex);
        }
    }

    private void addCategoryPanel() {
        Category lastCategory = getLastCategory();
        CategoryPanel categoryPanel = new CategoryPanel();
        CategoryController categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);

        String categoryName = DEFAULT_CATEGORY_NAME;
        if (lastCategory != null) {
            categoryName = (lastCategory.getName() != null && !lastCategory.getName().isBlank())
                    ? lastCategory.getName() : DEFAULT_CATEGORY_NAME;
            categoryPanel.setCategoryName(categoryName);
            categoryController.populateFromCategory(lastCategory);
        }

        String pageName = "categoryPanel_" + System.nanoTime();
        mainView.addPage(pageName, categoryPanel);

        CategoryExpandablePanel exp = new CategoryExpandablePanel(lastCategory);
        exp.setCategoryName(categoryName);
        exp.getEditButton().addActionListener(evt -> mainView.showPage(categoryPanel));
        categoryPanel.getButtonDone().addActionListener(evt -> {
            categoryController.saveAll();
            if (!categoryController.validateCorrectAnswers()) {
                JOptionPane.showMessageDialog(categoryPanel,
                        "Bitte für jede ausgefüllte Frage eine richtige Antwort auswählen.",
                        "Fehlende Antwortauswahl",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            exp.rebuildContent();
            mainView.showPage(configurationPanel);
        });

        JPanel categoryButtons = configurationPanel.getPanelCategoryButtons();
        categoryButtons.add(exp);
        categoryButtons.revalidate();
        categoryButtons.repaint();

        mainView.showPage(categoryPanel);
    }

    private void appendCategoryRow(Category cat) {
        CategoryPanel categoryPanel = new CategoryPanel();
        CategoryController categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);
        String name = (cat != null && cat.getName() != null && !cat.getName().isBlank())
                ? cat.getName() : DEFAULT_CATEGORY_NAME;
        categoryPanel.setCategoryName(name);
        categoryController.populateFromCategory(cat);

        String pageName = "categoryPanel_" + System.nanoTime();
        mainView.addPage(pageName, categoryPanel);

        CategoryExpandablePanel exp = new CategoryExpandablePanel(cat);
        exp.setCategoryName(name);
        exp.getEditButton().addActionListener(evt -> mainView.showPage(categoryPanel));
        categoryPanel.getButtonDone().addActionListener(evt -> {
            categoryController.saveAll();
            if (!categoryController.validateCorrectAnswers()) {
                JOptionPane.showMessageDialog(categoryPanel,
                        "Bitte für jede ausgefüllte Frage eine richtige Antwort auswählen.",
                        "Fehlende Antwortauswahl",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            exp.rebuildContent();
            mainView.showPage(configurationPanel);
        });

        JPanel categoryButtons = configurationPanel.getPanelCategoryButtons();
        categoryButtons.add(exp);
        categoryButtons.revalidate();
        categoryButtons.repaint();
    }
}
