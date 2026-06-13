package config.ui;

import config.ConfiguratorOverview;
import config.model.Category;
import config.model.ConfigQuestion;

import javax.swing.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryController
{
    private final CategoryPanel categoryPanel;
    private final MainView mainView;
    private final ConfiguratorOverview configuratorOverview;

    public CategoryController(CategoryPanel categoryPanel, MainView mainView, ConfiguratorOverview configuratorOverview)
    {
        this.categoryPanel = categoryPanel;
        this.mainView = mainView;
        this.configuratorOverview = configuratorOverview;
    }

    public void addQuestionPanels(List<QuestionPanel> questionPanels) {
        if (questionPanels == null || questionPanels.isEmpty()) {
            return;
        }
        JPanel container = categoryPanel.getQuestionsContainer();
        for (JPanel qp : questionPanels) {
            qp.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            Dimension pref = qp.getPreferredSize();
            if (pref != null) {
                qp.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
            } else {
                qp.setMaximumSize(new Dimension(Integer.MAX_VALUE, qp.getHeight() > 0 ? qp.getHeight() : Short.MAX_VALUE));
            }
            container.add(qp);
        }
        container.revalidate();
        container.repaint();
    }

    public void addQuestionPanels(int count) {
        if (count <= 0) return;
        List<QuestionPanel> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new QuestionPanel());
        }
        addQuestionPanels(list);
    }

    /**
     * Returns true if every question that has any content also has a correct answer selected.
     * Empty question slots (all fields blank) are skipped.
     */
    public boolean validateCorrectAnswers() {
        JPanel container = categoryPanel.getQuestionsContainer();
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof QuestionPanel qp) {
                if (qp.hasAnyContent() && !qp.hasCorrectAnswerSelected()) return false;
            }
        }
        return true;
    }

    /**
     * Force-flushes all QuestionPanel text fields to their models.
     * Must be called before navigating away from the CategoryPanel so data is
     * persisted even when Swing does not fire focusLost on CardLayout switches.
     */
    public void saveAll() {
        JPanel container = categoryPanel.getQuestionsContainer();
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof QuestionPanel qp) {
                qp.saveAll();
            }
        }
    }

    public void populateFromCategory(Category category) {
        if (category == null) return;
        List<QuestionPanel> panels = new ArrayList<>();
        for (Map.Entry<Integer, ConfigQuestion> entry : category.getPointQuestionMap().entrySet()) {
            Integer points = entry.getKey();
            ConfigQuestion q = entry.getValue();
            panels.add(new QuestionPanel(points, q));
        }
        addQuestionPanels(panels);
    }
}
