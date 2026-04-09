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
        JPanel container = categoryPanel.questionsContainer != null ? categoryPanel.questionsContainer : categoryPanel;
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
