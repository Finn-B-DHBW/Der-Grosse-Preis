package dhbw.dgp;

import java.util.List;

public class GameManager {
    private List<Player> players;
    private List<Question> questions;
    private SocketServer serverSocket;
    private DataBase db;

    public GameManager() {
        this.serverSocket = new SocketServer(this);
        this.db = new DataBase(this);
    }

    public void addPlayer(String name) {
        this.players.add(new Player(name));
    }
}
