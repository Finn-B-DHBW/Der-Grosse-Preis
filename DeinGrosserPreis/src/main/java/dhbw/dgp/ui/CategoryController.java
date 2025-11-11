package dhbw.dgp.ui;

import dhbw.dgp.Category;
import dhbw.dgp.ConfiguratorOverview;

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

    /**
     * Adds multiple QuestionPanel instances to the CategoryPanel container.
     * The CategoryPanel is revalidated and repainted afterwards.
     *
     * @param questionPanels list of QuestionPanel instances to add
     */
    public void addQuestionPanels(List<QuestionPanel> questionPanels) {
        if (questionPanels == null || questionPanels.isEmpty()) {
            return;
        }
        JPanel container = categoryPanel.questionsContainer != null ? categoryPanel.questionsContainer : categoryPanel;
        for (JPanel qp : questionPanels) {
            // Ensure compact stacking in BoxLayout: do not let panels stretch vertically
            qp.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            Dimension pref = qp.getPreferredSize();
            if (pref != null) {
                // Allow any width, but cap height to preferred so BoxLayout won't stretch it
                qp.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
            } else {
                qp.setMaximumSize(new Dimension(Integer.MAX_VALUE, qp.getHeight() > 0 ? qp.getHeight() : Short.MAX_VALUE));
            }
            container.add(qp);
        }
        container.revalidate();
        container.repaint();
    }

    /**
     * Convenience method to create and add a given number of new QuestionPanel instances
     * to the CategoryPanel.
     *
     * @param count number of QuestionPanel instances to create and add (ignored if <= 0)
     */
    public void addQuestionPanels(int count) {
        if (count <= 0) return;
        List<QuestionPanel> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new QuestionPanel());
        }
        addQuestionPanels(list);
    }

    /**
     * Populate the CategoryPanel with QuestionPanels bound to the model from the given Category.
     * The QuestionPanel will display the points header and write back to the Question model.
     */
    public void populateFromCategory(Category category) {
        if (category == null) return;
        List<QuestionPanel> panels = new ArrayList<>();
        for (Map.Entry<Integer, dhbw.dgp.Question> entry : category.getPointQuestionMap().entrySet()) {
            Integer points = entry.getKey();
            dhbw.dgp.Question q = entry.getValue();
            panels.add(new QuestionPanel(points, q));
        }
        addQuestionPanels(panels);
    }
}
