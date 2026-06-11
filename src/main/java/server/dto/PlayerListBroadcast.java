package server.dto;

import java.util.List;

public class PlayerListBroadcast {

    public static class PlayerInfo {
        private String name;
        private int score;

        public PlayerInfo() {
        }

        public PlayerInfo(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    private List<PlayerInfo> players;

    public PlayerListBroadcast() {
    }

    public PlayerListBroadcast(List<PlayerInfo> players) {
        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }
}
