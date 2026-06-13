package config;

import config.model.Category;
import config.model.ConfigQuestion;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Reads questions from the World-B pool (Question / WrongAnswer tables)
 * and converts them into World-A ConfigQuestion / Category objects that
 * can be inserted into a Configurator configuration.
 */
public class QuestionPoolService {

    private static final Logger log = Logger.getLogger(QuestionPoolService.class.getName());
    private static final String DB_URL = "jdbc:sqlite:database.db";

    private QuestionPoolService() {}

    /**
     * Returns all distinct category names from the question pool,
     * sorted alphabetically. Returns an empty list if the DB is not
     * seeded yet (first run before any game has started).
     */
    public static List<String> getAvailableCategories() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM Question ORDER BY category";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat != null && !cat.isBlank()) result.add(cat);
            }
        } catch (SQLException e) {
            log.warning("Could not load categories from question pool: " + e.getMessage());
        }
        return result;
    }

    /**
     * Draws {@code count} random questions from {@code categoryName},
     * maps them to ConfigQuestion objects (answers shuffled, correct
     * answer index set), and returns a ready-to-use Category.
     * Point values are assigned as 10, 20, 30, … (slot × 10).
     */
    public static Category generateCategory(String categoryName, int count) {
        Map<Integer, ConfigQuestion> map = new TreeMap<>();
        String sql = "SELECT questionId, questionText, rightAnswer FROM Question " +
                     "WHERE category = ? ORDER BY RANDOM() LIMIT ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ps.setInt(2, count);
            try (ResultSet rs = ps.executeQuery()) {
                int slot = 1;
                while (rs.next()) {
                    int qId      = rs.getInt("questionId");
                    String qText = rs.getString("questionText");
                    String right = rs.getString("rightAnswer");
                    List<String> wrong = getWrongAnswers(con, qId);
                    map.put(slot * 10, buildConfigQuestion(qText, right, wrong));
                    slot++;
                }
            }
        } catch (SQLException e) {
            log.warning("Could not load questions from pool: " + e.getMessage());
        }
        return new Category(categoryName, map);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private static List<String> getWrongAnswers(Connection con, int questionId) throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT wrongAnswerText FROM WrongAnswer WHERE questionId = ? LIMIT 2";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String a = rs.getString("wrongAnswerText");
                    if (a != null) result.add(a);
                }
            }
        }
        return result;
    }

    /**
     * Builds a ConfigQuestion with the correct answer shuffled into a random
     * position so the correct slot index is not always the same.
     */
    private static ConfigQuestion buildConfigQuestion(String questionText,
                                                      String rightAnswer,
                                                      List<String> wrongAnswers) {
        // Build a 3-slot list: right answer + up to 2 wrong answers (padded if needed)
        List<String> answers = new ArrayList<>(3);
        answers.add(rightAnswer != null ? rightAnswer : "");
        for (int i = 0; i < 2; i++) {
            answers.add(i < wrongAnswers.size() ? wrongAnswers.get(i) : "");
        }

        Collections.shuffle(answers);

        String right = rightAnswer != null ? rightAnswer : "";
        int correctIndex = answers.indexOf(right) + 1; // 1-based; indexOf returns -1 if missing → 0

        ConfigQuestion cq = new ConfigQuestion(3);
        cq.setQuestion(questionText != null ? questionText : "");
        for (int i = 0; i < 3; i++) {
            cq.setAnswer(i, answers.get(i));
        }
        cq.setCorrectAnswer(correctIndex);
        return cq;
    }
}
