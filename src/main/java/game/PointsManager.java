package game;

import config.model.Team;

import java.util.HashMap;
import java.util.Map;

public class PointsManager {
    private Map<Team, Integer> teamPoints;

    public PointsManager() {
        this.teamPoints = new HashMap<>();
    }

    public void addTeam(Team team) {
        teamPoints.put(team, 0);
    }

    public void updatePoints(Team team, int points) {
        teamPoints.put(team, points);
    }

    public void addPoints(Team team, int points) {
        int currentPoints = getPoints(team);
        teamPoints.put(team, currentPoints + points);
    }

    public int getPoints(Team team) {
        return teamPoints.getOrDefault(team, 0);
    }

    public Map<Team, Integer> getAllTeamPoints() {
        return teamPoints;
    }
}
