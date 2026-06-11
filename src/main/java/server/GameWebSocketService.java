package server;

import Model.Player;
import Model.Question;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.dto.AnswerResult;
import server.dto.PlayerListBroadcast;
import server.dto.QuestionBroadcast;
import server.dto.QuestionClosedBroadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Sendet Spielereignisse an alle verbundenen Browser-Clients.
 * Wird vom GameWebSocketController (Spieler-Aktionen) und ueber die
 * statische Instanz vom Swing-Teil (Admin-Aktionen) verwendet.
 */
@Service
public class GameWebSocketService {

    private static volatile GameWebSocketService instance;

    private final SimpMessagingTemplate messagingTemplate;
    private final GameState gameState;

    public GameWebSocketService(SimpMessagingTemplate messagingTemplate, GameState gameState) {
        this.messagingTemplate = messagingTemplate;
        this.gameState = gameState;
        instance = this;
    }

    /** Zugriff fuer den Swing-Teil; null solange Spring nicht gestartet ist. */
    public static GameWebSocketService getInstance() {
        return instance;
    }

    public void broadcastQuestion(Question question) {
        gameState.openQuestion(question);

        List<String> answers = new ArrayList<>();
        answers.add(question.getRightAnswer());
        if (question.getWrongAnswers() != null) {
            answers.addAll(Arrays.asList(question.getWrongAnswers()));
        }
        Collections.shuffle(answers);

        messagingTemplate.convertAndSend("/topic/question",
                new QuestionBroadcast(question.getQuestionId(), question.getQuestionText(),
                        answers, question.getScore()));
    }

    public void broadcastQuestionClosed(Question question) {
        gameState.closeQuestion();
        messagingTemplate.convertAndSend("/topic/question-closed",
                new QuestionClosedBroadcast(question.getQuestionId(), question.getRightAnswer()));
        broadcastPlayerList();
    }

    public void broadcastAnswerResult(AnswerResult result) {
        messagingTemplate.convertAndSend("/topic/answer-result", result);
    }

    public void broadcastPlayerList() {
        List<PlayerListBroadcast.PlayerInfo> infos = new ArrayList<>();
        for (Player player : gameState.getPlayers()) {
            infos.add(new PlayerListBroadcast.PlayerInfo(player.getName(), player.getScore()));
        }
        messagingTemplate.convertAndSend("/topic/players", new PlayerListBroadcast(infos));
    }
}
