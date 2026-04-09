import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.List;

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
        configurationPanel.buttonDone.addActionListener(e -> mainView.showPage(configuratorPanel));

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

        configurationPanel.textFieldNumberOfQuestions.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
                    Object valueObj = configurationPanel.textFieldNumberOfQuestions.getValue();
                    if (valueObj instanceof Number) {
                        configuratorOverview.configurationBuilder.setNumberOfQuestions(((Number) valueObj).intValue());
                    } else {
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

        configurationPanel.buttonAddCategory.addActionListener(e -> {
            configuratorOverview.configurationBuilder
                    .createNewCategory(configurationPanel.textFieldCategoryName.getText());
            configurationPanel.textFieldCategoryName.setText("");
            addCategoryPanel();
        });
    }

    private void addCategoryPanel(){
        CategoryPanel categoryPanel = new CategoryPanel();
        CategoryController categoryController = new CategoryController(categoryPanel, mainView, configuratorOverview);

        String categoryName = null;
        if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
            Configuration cfg = configuratorOverview.configurationBuilder.getConfiguration();
            List<Category> cats = cfg.getCategories();
            if (cats != null && !cats.isEmpty()) {
                Category last = cats.get(cats.size() - 1);
                categoryName = last.getName();
                categoryPanel.setCategoryName(categoryName);
                categoryController.populateFromCategory(last);
            }
        }

        String pageName = "categoryPanel_" + System.currentTimeMillis();
        mainView.addPage(pageName, categoryPanel);

        categoryPanel.buttonDone.addActionListener(evt -> mainView.showPage(configurationPanel));

        if (categoryName == null || categoryName.isBlank()) {
            categoryName = "Kategorie";
        }
        Category catModel = null;
        if (configuratorOverview != null && configuratorOverview.configurationBuilder != null) {
            Configuration cfg2 = configuratorOverview.configurationBuilder.getConfiguration();
            List<Category> cats2 = cfg2.getCategories();
            if (cats2 != null && !cats2.isEmpty()) {
                catModel = cats2.get(cats2.size() - 1);
            }
        }
        CategoryExpandablePanel exp = new CategoryExpandablePanel(catModel);
        exp.setCategoryName(categoryName);
        CategoryPanel targetPanel = categoryPanel;
        exp.editButton.addActionListener(evt -> mainView.showPage(targetPanel));
        configurationPanel.panelCategoryButtons.add(exp);
        configurationPanel.panelCategoryButtons.revalidate();
        configurationPanel.panelCategoryButtons.repaint();

        mainView.showPage(categoryPanel);
    }
}
