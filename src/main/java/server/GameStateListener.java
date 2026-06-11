package server;

import Model.Player;
import server.dto.AnswerResult;

import java.util.List;

/**
 * Callback-Interface, ueber das der Spring-Teil (WebSocket) den Swing-Teil
 * (GameManager) benachrichtigt. Alle Methoden werden auf dem EDT aufgerufen.
 */
public interface GameStateListener {

    void onPlayerJoined(Player player);

    void onAnswerReceived(AnswerResult result);

    void onPlayerListChanged(List<Player> players);
}
