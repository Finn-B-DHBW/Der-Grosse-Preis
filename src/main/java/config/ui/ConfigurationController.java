package config.ui;

import config.ConfiguratorOverview;
import config.model.Category;
import config.model.Configuration;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
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
        configurationPanel.buttonDone.addActionListener(e -> {
            configuratorOverview.saveConfigurationToDatabase();
            mainView.showPage(configuratorPanel);
        });

        configurationPanel.textFieldConfigurationTitle.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!hasBuilder()) return;
                configuratorOverview.configurationBuilder
                        .setTitle(configurationPanel.textFieldConfigurationTitle.getText());
                updateConfigButtonTitle();
            }
        });

        configurationPanel.textFieldNumberOfQuestions.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                applyNumberOfQuestionsFromTextField();
            }
        });

        if (configurationPanel.spinnerNumberOfQuestions != null) {
            configurationPanel.spinnerNumberOfQuestions.addChangeListener(e -> {
                if (!hasBuilder()) return;
                Object v = configurationPanel.spinnerNumberOfQuestions.getValue();
                if (v instanceof Number) {
                    configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) v).intValue());
                }
            });
        }

        configurationPanel.buttonAddCategory.addActionListener(e -> {
            configuratorOverview.configurationBuilder
                    .createNewCategory(configurationPanel.textFieldCategoryName.getText());
            configurationPanel.textFieldCategoryName.setText("");
            addCategoryPanel();
        });
    }

    private void applyNumberOfQuestionsFromTextField() {
        if (!hasBuilder()) return;
        Object valueObj = configurationPanel.textFieldNumberOfQuestions.getValue();
        if (valueObj instanceof Number) {
            configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) valueObj).intValue());
            return;
        }
        String text = configurationPanel.textFieldNumberOfQuestions.getText();
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

        String pageName = "categoryPanel_" + System.currentTimeMillis();
        mainView.addPage(pageName, categoryPanel);
        categoryPanel.buttonDone.addActionListener(evt -> mainView.showPage(configurationPanel));

        CategoryExpandablePanel exp = new CategoryExpandablePanel(lastCategory);
        exp.setCategoryName(categoryName);
        exp.editButton.addActionListener(evt -> mainView.showPage(categoryPanel));
        configurationPanel.panelCategoryButtons.add(exp);
        configurationPanel.panelCategoryButtons.revalidate();
        configurationPanel.panelCategoryButtons.repaint();

        mainView.showPage(categoryPanel);
    }
}
