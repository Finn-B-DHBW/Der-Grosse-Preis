package dhbw.dgp;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private Configuration configuration;
    private List<Team> teams;
    private int currentTeamIndex;
    private PointsManager pointsManager;
    private GameOverview gameOverview;

    public GameManager() {
        this.teams = new ArrayList<>();
        this.currentTeamIndex = 0;
        this.pointsManager = new PointsManager();
        this.gameOverview = new GameOverview();
    }

    /**
     * Loads a game configuration
     * @param configuration The configuration to load
     */
    public void loadGame(Configuration configuration) {
        this.configuration = configuration;

        // Initialize the game overview with categories from the configuration
        this.gameOverview = new GameOverview();
        if (configuration != null) {
            for (Category category : configuration.getCategories()) {
                gameOverview.addCategory(category);
            }
        }
    }

    /**
     * Creates a new team and adds it to the list of teams
     * @param name The name of the team
     * @return The created team
     */
    public void createTeam(String name) {
        Team team = new Team(name);
        teams.add(team);
        pointsManager.addTeam(team);
    }

    /**
     * Gets the currently active team
     * @return The active team
     */
    public Team getCurrentTeam() {
        return teams.get(currentTeamIndex);
    }

    /**
     * Sets the next team as active in a round-robin fashion
     * If there are no more available questions, calls FinishGame
     */
    public void nextTeam() {
        if (teams.isEmpty()) {
            return;
        }

        // Check if there are any available questions left
        boolean hasAvailableQuestions = false;
        for (List<Integer> questionPoints : gameOverview.getAvailableQuestions().values()) {
            if (!questionPoints.isEmpty()) {
                hasAvailableQuestions = true;
                break;
            }
        }

        if (!hasAvailableQuestions) {
            FinishGame();
            return;
        }

        currentTeamIndex = (currentTeamIndex + 1) % teams.size();
    }

    /**
     * Called when the game is finished (no more available questions)
     */
    public void FinishGame() {
        // This method is called when the game is finished
        // Implementation can be added as needed
    }

    /**
     * Gets the list of all teams
     * @return The list of teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Gets the current game configuration
     * @return The configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the game overview
     * @return The game overview
     */
    public GameOverview getGameOverview() {
        return gameOverview;
    }
}
