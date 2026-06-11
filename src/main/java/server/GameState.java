package server;

import Model.Player;
import Model.Question;
import server.dto.AnswerResult;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-sicherer Spielzustand, der zwischen Spring (WebSocket-Threads) und
 * Swing (EDT) geteilt wird. Spring schreibt, Swing liest. Benachrichtigungen
 * an Swing laufen immer ueber SwingUtilities.invokeLater.
 */
public final class GameState {

    private static final GameState INSTANCE = new GameState();

    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final AtomicReference<Question> currentQuestion = new AtomicReference<>();
    private final ConcurrentHashMap<String, String> playerAnswers = new ConcurrentHashMap<>();
    private final List<GameStateListener> listeners = new CopyOnWriteArrayList<>();
    private final CountDownLatch serverReady = new CountDownLatch(1);

    private GameState() {
    }

    public static GameState getInstance() {
        return INSTANCE;
    }

    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    public void markServerReady() {
        serverReady.countDown();
    }

    public boolean awaitReady(long timeout, TimeUnit unit) {
        try {
            return serverReady.await(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Registriert einen neuen Spieler. Gibt false zurueck, wenn der Name
     * bereits vergeben ist.
     */
    public boolean addPlayer(String name) {
        if (name == null || name.isBlank() || findPlayer(name) != null) {
            return false;
        }
        Player player = new Player(name.trim());
        players.add(player);
        notifyOnEdt(listener -> listener.onPlayerJoined(player));
        notifyPlayerListChanged();
        return true;
    }

    public Player findPlayer(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(trimmed)) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void openQuestion(Question question) {
        currentQuestion.set(question);
        playerAnswers.clear();
    }

    public void closeQuestion() {
        currentQuestion.set(null);
    }

    public Question getCurrentQuestion() {
        return currentQuestion.get();
    }

    /**
     * Verarbeitet die Antwort eines Spielers. Punkte werden hier (Server-seitig)
     * vergeben. Gibt null zurueck, wenn die Antwort ungueltig ist (falsche Frage,
     * unbekannter Spieler oder bereits geantwortet).
     */
    public AnswerResult recordAnswer(String playerName, String selectedAnswer, int questionId) {
        Question question = currentQuestion.get();
        Player player = findPlayer(playerName);
        if (question == null || question.getQuestionId() != questionId || player == null) {
            return null;
        }
        if (playerAnswers.putIfAbsent(player.getName(), selectedAnswer) != null) {
            return null;
        }

        boolean correct = question.getRightAnswer().equals(selectedAnswer);
        if (correct) {
            player.addRightAnswerToList(question);
        }

        AnswerResult result = new AnswerResult(player.getName(), selectedAnswer, correct, player.getScore());
        notifyOnEdt(listener -> listener.onAnswerReceived(result));
        notifyPlayerListChanged();
        return result;
    }

    private void notifyPlayerListChanged() {
        List<Player> snapshot = new ArrayList<>(players);
        notifyOnEdt(listener -> listener.onPlayerListChanged(snapshot));
    }

    private void notifyOnEdt(java.util.function.Consumer<GameStateListener> callback) {
        SwingUtilities.invokeLater(() -> {
            for (GameStateListener listener : listeners) {
                callback.accept(listener);
            }
        });
    }
}
