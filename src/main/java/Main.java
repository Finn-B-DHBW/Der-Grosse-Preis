import Manager.GameManager;
import server.GameState;
import server.SpringBootApp;

import javax.swing.SwingUtilities;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        SpringBootApp.startInBackground(args);

        // Warten bis der Server laeuft, damit der JoinScreen die richtige URL
        // anzeigen kann. Bei Timeout startet das Spiel trotzdem (offline Modus).
        boolean serverStarted = GameState.getInstance().awaitReady(15, TimeUnit.SECONDS);
        if (!serverStarted) {
            System.out.println("Warnung: Webserver konnte nicht gestartet werden, Spieler koennen nicht beitreten.");
        }

        SwingUtilities.invokeLater(GameManager::new);
    }
}
