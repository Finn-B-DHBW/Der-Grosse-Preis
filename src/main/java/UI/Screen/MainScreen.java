package UI.Screen;

import Manager.GameManager;
import Model.Player;
import Model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MainScreen {

    private static final Color BG        = new Color(15, 20, 40);
    private static final Color CARD      = new Color(25, 35, 60);
    private static final Color ACCENT    = new Color(52, 152, 219);
    private static final Color ANSWER_BG = new Color(30, 45, 80);
    private static final Color DISABLED  = new Color(18, 24, 44);
    private static final Color GOLD      = new Color(255, 200, 0);
    private static final Color SIDEBAR   = new Color(20, 28, 50);

    public void showMainScreen(GameManager gameManager) {
        gameManager.cleanScreen();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(buildGridPanel(gameManager), BorderLayout.CENTER);
        mainPanel.add(buildBottomPanel(gameManager), BorderLayout.SOUTH);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(28, 0, 8, 0));

        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 54));
        title.setForeground(Color.WHITE);
        panel.add(title);
        return panel;
    }

    private JPanel buildGridPanel(GameManager gameManager) {
        String[] categories = gameManager.getCategoryList();
        List<Integer> pointValues = getPointValues(gameManager);

        JPanel grid = new JPanel(new GridLayout(pointValues.size() + 1, categories.length, 10, 10));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(16, 32, 16, 32));

        for (String category : categories) {
            grid.add(buildCategoryLabel(category));
        }

        for (Integer points : pointValues) {
            for (String category : categories) {
                Question question = findQuestion(gameManager, category, points);
                boolean answered = question != null && isAnswered(gameManager, question);
                grid.add(buildQuestionButton(gameManager, question, points, answered));
            }
        }

        return grid;
    }

    private JPanel buildCategoryLabel(String category) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        JLabel label = new JLabel(category, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(ACCENT);
        label.setOpaque(false);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JButton buildQuestionButton(GameManager gameManager, Question question, int points, boolean answered) {
        Color bg = (answered || question == null) ? DISABLED : ANSWER_BG;

        JButton btn = new JButton(answered ? "" : points + " Pts") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!answered && question != null) {
                    g2.setColor(ACCENT);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setForeground(GOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        if (answered || question == null) {
            btn.setEnabled(false);
        } else {
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> gameManager.getQuestionScreen().showQuestionScreen(gameManager, question));
        }

        return btn;
    }

    private JPanel buildBottomPanel(GameManager gameManager) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR);
        panel.setBorder(new EmptyBorder(18, 30, 18, 30));
        panel.setPreferredSize(new Dimension(0, 150));

        panel.add(buildTurnPanel(gameManager), BorderLayout.WEST);
        panel.add(buildScorePanel(gameManager), BorderLayout.EAST);

        return panel;
    }

    private JPanel buildTurnPanel(GameManager gameManager) {
        String name = gameManager.getPlayerList().get(gameManager.getCurrentPlayerIndex()).getName();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR);

        JLabel turnLabel = new JLabel("An der Reihe:");
        turnLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        turnLabel.setForeground(new Color(130, 160, 210));
        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel playerLabel = new JLabel(name);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        playerLabel.setForeground(ACCENT);
        playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(turnLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(playerLabel);
        return panel;
    }

    private JPanel buildScorePanel(GameManager gameManager) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR);

        for (Player player : gameManager.getPlayerList()) {
            JPanel row = new JPanel(new BorderLayout(24, 0));
            row.setBackground(SIDEBAR);
            row.setMaximumSize(new Dimension(320, 30));
            row.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            nameLabel.setForeground(Color.WHITE);

            JLabel scoreLabel = new JLabel(player.getScore() + " Pts");
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
            scoreLabel.setForeground(ACCENT);

            row.add(nameLabel, BorderLayout.WEST);
            row.add(scoreLabel, BorderLayout.EAST);
            panel.add(row);
            panel.add(Box.createVerticalStrut(4));
        }

        return panel;
    }

    private List<Integer> getPointValues(GameManager gameManager) {
        TreeSet<Integer> pointValues = new TreeSet<>();
        if (gameManager.getQuestionList() != null) {
            for (Question question : gameManager.getQuestionList()) {
                pointValues.add(question.getScore());
            }
        }
        return new ArrayList<>(pointValues);
    }

    private Question findQuestion(GameManager gameManager, String category, int points) {
        if (gameManager.getQuestionList() == null) return null;
        for (Question question : gameManager.getQuestionList()) {
            if (question.getCategory().equals(category) && question.getScore() == points) {
                return question;
            }
        }
        return null;
    }

    private boolean isAnswered(GameManager gameManager, Question question) {
        return gameManager.getAnsweredQuestionList().stream()
                .anyMatch(q -> q.getQuestionId() == question.getQuestionId());
    }
}
