import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private Configuration configuration;
    private List<Team> teams;
    private int currentTeamIndex;
    private PointsManager pointsManager;
    private GameOverview gameOverview;

    public GameLogic() {
        this.teams = new ArrayList<>();
        this.currentTeamIndex = 0;
        this.pointsManager = new PointsManager();
        this.gameOverview = new GameOverview();
    }

    public void loadGame(Configuration configuration) {
        this.configuration = configuration;

        this.gameOverview = new GameOverview();
        if (configuration != null) {
            for (Category category : configuration.getCategories()) {
                gameOverview.addCategory(category);
            }
        }
    }

    public void createTeam(String name) {
        Team team = new Team(name);
        teams.add(team);
        pointsManager.addTeam(team);
    }

    public Team getCurrentTeam() {
        return teams.get(currentTeamIndex);
    }

    public void nextTeam() {
        if (teams.isEmpty()) {
            return;
        }

        boolean hasAvailableQuestions = false;
        for (List<Integer> questionPoints : gameOverview.getAvailableQuestions().values()) {
            if (!questionPoints.isEmpty()) {
                hasAvailableQuestions = true;
                break;
            }
        }

        if (!hasAvailableQuestions) {
            finishGame();
            return;
        }

        currentTeamIndex = (currentTeamIndex + 1) % teams.size();
    }

    public void finishGame() {
        // Called when the game is finished (no more available questions)
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public GameOverview getGameOverview() {
        return gameOverview;
    }

    public PointsManager getPointsManager() {
        return pointsManager;
    }
}
