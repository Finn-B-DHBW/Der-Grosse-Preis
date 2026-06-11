package server.dto;

public class JoinMessage {

    private String playerName;

    public JoinMessage() {
    }

    public JoinMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
