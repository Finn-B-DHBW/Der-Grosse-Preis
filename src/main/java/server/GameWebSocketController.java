package server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import server.dto.AnswerMessage;
import server.dto.AnswerResult;
import server.dto.JoinMessage;

/**
 * Nimmt STOMP-Nachrichten der Spieler-Browser entgegen
 * (/app/join und /app/answer).
 */
@Controller
public class GameWebSocketController {

    private final GameState gameState;
    private final GameWebSocketService webSocketService;

    public GameWebSocketController(GameState gameState, GameWebSocketService webSocketService) {
        this.gameState = gameState;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/join")
    public void handleJoin(JoinMessage message) {
        if (gameState.addPlayer(message.getPlayerName())) {
            webSocketService.broadcastPlayerList();
        }
    }

    @MessageMapping("/answer")
    public void handleAnswer(AnswerMessage message) {
        AnswerResult result = gameState.recordAnswer(
                message.getPlayerName(), message.getSelectedAnswer(), message.getQuestionId());
        if (result != null) {
            webSocketService.broadcastAnswerResult(result);
            webSocketService.broadcastPlayerList();
        }
    }
}
