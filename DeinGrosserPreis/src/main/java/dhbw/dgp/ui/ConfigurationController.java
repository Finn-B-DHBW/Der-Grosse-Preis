package dhbw.dgp.ui;

import dhbw.dgp.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class ConfigurationController
{
    private final ConfigurationPanel configurationPanel;
    private final ConfiguratorOverview configuratorOverview;
    private final MainView mainView;
    private final ConfiguratorPanel configuratorPanel;

    public ConfigurationController(ConfigurationPanel configurationPanel, MainView mainView, ConfiguratorOverview configuratorOverview, ConfiguratorPanel configuratorPanel) {
        this.configurationPanel = configurationPanel;
        this.configuratorOverview = configuratorOverview;
        this.mainView = mainView;
        this.configuratorPanel = configuratorPanel;
        wireButtonsLazily();
    }

    private JButton currentConfigButton;

    public void setCurrentConfigButton(JButton button) {
        this.currentConfigButton = button;
    }

    private void updateConfigButtonTitle() {
        if (currentConfigButton != null && configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
            String t = configuratorOverview.configurationBuilder.getConfiguration().getTitle();
            if (t == null || t.isBlank()) t = "Konfiguration";
            currentConfigButton.setText(t);
            currentConfigButton.revalidate();
            currentConfigButton.repaint();
        }
    }

    private void wireButtonsLazily() {
        // Done button: back to configurator overview
        configurationPanel.buttonDone.addActionListener(e -> mainView.showPage(configuratorPanel));

        // Autosave on focus lost: Title
        configurationPanel.textFieldConfigurationTitle.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
                    configuratorOverview.configurationBuilder
                            .setTitle(configurationPanel.textFieldConfigurationTitle.getText());
                    updateConfigButtonTitle();
                }
            }
        });

        // Autosave on focus lost: Number of questions (editor text field)
        configurationPanel.textFieldNumberOfQuestions.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
                    Object valueObj = configurationPanel.textFieldNumberOfQuestions.getValue();
                    if (valueObj instanceof Number) {
                        configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) valueObj).intValue());
                    } else {
                        // Fallback: try to parse text content
                        try {
                            String text = configurationPanel.textFieldNumberOfQuestions.getText();
                            if (text != null && !text.isBlank()) {
                                int value = Integer.parseInt(text.trim());
                                configuratorOverview.configurationBuilder.setNumberOfQuestions(value);
                            }
                        } catch (NumberFormatException ex) {
                            // ignore invalid input silently
                        }
                    }
                }
            }
        });

        // Also save immediately when spinner value changes via arrow buttons or keyboard
        if (configurationPanel.spinnerNumberOfQuestions != null) {
            configurationPanel.spinnerNumberOfQuestions.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
                        Object v = configurationPanel.spinnerNumberOfQuestions.getValue();
                        if (v instanceof Number) {
                            configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) v).intValue());
                        }
                    }
                }
            });
        }

        // Removed explicit save buttons; autosave via focusLost is active for title and number of questions.
        configurationPanel.buttonAddCategory.addActionListener(e -> {
            configuratorOverview.configurationBuilder
                    .createNewCategory(configurationPanel.textFieldCategoryName.getText());
            // clear the category name input after creating the category
            configurationPanel.textFieldCategoryName.setText("");
            addCategoryPanel();
        });
    }

    private void addCategoryPanel(){
        // Create a new CategoryPanel and populate it with QuestionPanels bound to the model
        CategoryPanel categoryPanel = new CategoryPanel();
        CategoryController categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);

        String categoryName = null;
        if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
            dhbw.dgp.Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
            java.util.List<dhbw.dgp.Category> cats = cfg.getCategories();
            if (cats != null && !cats.isEmpty()) {
                dhbw.dgp.Category last = cats.get(cats.size() - 1);
                categoryName = last.getName();
                // Set the category name in the CategoryPanel title
                categoryPanel.setCategoryName(categoryName);
                // Populate questions for this category
                categoryController.populateFromCategory(last);
            }
        }

        // Add as a new page to the MainView
        String pageName = "categoryPanel_" + System.currentTimeMillis();
        mainView.addPage(pageName, categoryPanel);

        // Wire 'fertig' to return to configuration panel
        categoryPanel.buttonDone.addActionListener(evt -> mainView.showPage(configurationPanel));

        // Add an expandable panel in ConfigurationPanel to reopen and view this category
        if (categoryName == null || categoryName.isBlank()) {
            categoryName = "Kategorie";
        }
        dhbw.dgp.Category catModel = null;
        if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
            dhbw.dgp.Configuration cfg2 = configuratorOverview.configurationBuilder.getConfiguration();
            java.util.List<dhbw.dgp.Category> cats2 = cfg2.getCategories();
            if (cats2 != null && !cats2.isEmpty()) {
                catModel = cats2.get(cats2.size() - 1);
            }
        }
        CategoryExpandablePanel exp = new CategoryExpandablePanel(catModel);
        exp.setCategoryName(categoryName);
        CategoryPanel targetPanel = categoryPanel; // effectively final for lambda
        exp.editButton.addActionListener(evt -> mainView.showPage(targetPanel));
        configurationPanel.panelCategoryButtons.add(exp);
        configurationPanel.panelCategoryButtons.revalidate();
        configurationPanel.panelCategoryButtons.repaint();

        // Now show the category page immediately
        mainView.showPage(categoryPanel);
    }
}
