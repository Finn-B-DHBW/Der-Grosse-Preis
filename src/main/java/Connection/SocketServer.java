package Connection;

import Manager.GameManager;
import Model.Question;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer implements Runnable{


    private ServerSocket socket;
    private List<Socket> clients;
    private List<String> names;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private GameManager gameManager;

    public SocketServer(GameManager gameManager){
        this.gameManager = gameManager;
        clients = new ArrayList<>();
        names = new ArrayList<>();

        try{
            socket = new ServerSocket(9999);
        }catch (IOException e){
            System.out.println("Could not create socket");
        }

    }

    @Override
    public void run() {
        try {
            clients.add(socket.accept());
            //gameManager.addPlayer(names.get(clients.indexOf(socket.accept())));
            //todo das hier wird so auch nicht funktionieren denn wir wissen noch nicht wie es client seitig realisiert wird
            //das man den namen eingibt weshalb ich addPlayer methode ausklammer man kann überlegen aber namen randome zu machen
        } catch (IOException e) {
            System.out.println("Error accepting an Client");
        }
    }

    public void sendQuestions(Question question){
        for(Socket client : clients){
            try {
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(question);
            } catch (IOException e) {
                System.out.println("Error sending question to client");
            }
        }
    }

    public void recievAnswer(){
        Question question = null;
        for(Socket client : clients){
            try {
                input = new ObjectInputStream(client.getInputStream());
                question = (Question) input.readObject();
                gameManager.answerQuestion(question, names.get((clients.indexOf(client))));

                //todo hier noch einiges zu machen das es funktioniert vorallem so das polling funktioniert
                // hab gerade nur die basic functionallity gemacht sodas gui methoden theorethisch drauf
                // aufbauen können. Kann auch sein das man die main.java.Model.Question klasse erweitern muss auf eine
                // antwort oder man macht noch eine klasse answer und dann sagt man der server schickt die fragen clients antworten
                //aber das ist noch nicht relevant weshalb ich es noch nicht fertig implementiere
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error recieving answer from client");
            }
        }
    }

    public List<Socket> getClients() {
        return clients;
    }
}
