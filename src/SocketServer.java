import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketServer {
    private ServerSocket socket;
    private List<Socket> clients;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private GameManager gameManager;

    public SocketServer(GameManager gameManager) {
        this.gameManager = gameManager;


    }

    public List<Socket> getClients() {
        return clients;
    }
}
