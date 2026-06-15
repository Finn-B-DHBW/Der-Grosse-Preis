package UI.Screen;

import Manager.GameManager;
import Model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EndScreen {

    private static final Color BG     = new Color(15, 20, 40);
    private static final Color CARD   = new Color(25, 35, 60);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color GOLD   = new Color(255, 200, 0);
    private static final Color SILVER = new Color(180, 188, 200);
    private static final Color BRONZE = new Color(195, 115, 50);

    public void showEndScreen(GameManager gameManager) {
        gameManager.cleanScreen();

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG);
        mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(buildRankingPanel(gameManager), BorderLayout.CENTER);

        gameManager.add(mainPanel);
        gameManager.setVisible(true);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(50, 0, 10, 0));

        JLabel title = new JLabel("ERGEBNISSE");
        title.setFont(new Font("Arial", Font.BOLD, 56));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Top 5 Spieler");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 22));
        subtitle.setForeground(new Color(130, 160, 210));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));

        return panel;
    }

    private JPanel buildRankingPanel(GameManager gameManager) {
        List<Player> sorted = new ArrayList<>(gameManager.getPlayerList());
        sorted.sort(Comparator.comparingInt(Player::getScore).reversed());

        int limit = Math.min(5, sorted.size());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(BG);

        if (sorted.isEmpty()) {
            JLabel empty = new JLabel("Keine Spieler vorhanden");
            empty.setFont(new Font("Arial", Font.PLAIN, 22));
            empty.setForeground(new Color(130, 160, 210));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            column.add(empty);
        } else {
            for (int i = 0; i < limit; i++) {
                column.add(buildPlayerCard(i + 1, sorted.get(i)));
                column.add(Box.createVerticalStrut(14));
            }
        }

        wrapper.add(column);
        return wrapper;
    }

    private JPanel buildPlayerCard(int rank, Player player) {
        Color rankColor = rankColor(rank);
        int cardHeight = rank == 1 ? 100 : rank <= 3 ? 78 : 65;

        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(rankColor);
                g2.setStroke(new BasicStroke(rank == 1 ? 3f : 2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(620, cardHeight));
        card.setMaximumSize(new Dimension(620, cardHeight));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setBorder(new EmptyBorder(0, 24, 0, 28));

        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("Arial", Font.BOLD, rank == 1 ? 38 : rank <= 3 ? 28 : 22));
        rankLabel.setForeground(rankColor);
        rankLabel.setPreferredSize(new Dimension(80, cardHeight));
        rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(rankLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, rank == 1 ? 30 : rank <= 3 ? 22 : 18));
        nameLabel.setForeground(Color.WHITE);
        card.add(nameLabel, BorderLayout.CENTER);

        JLabel scoreLabel = new JLabel(player.getScore() + " Punkte");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, rank == 1 ? 28 : rank <= 3 ? 20 : 17));
        scoreLabel.setForeground(rankColor);
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(scoreLabel, BorderLayout.EAST);

        return card;
    }

    private Color rankColor(int rank) {
        return switch (rank) {
            case 1 -> GOLD;
            case 2 -> SILVER;
            case 3 -> BRONZE;
            default -> ACCENT;
        };
    }
}
