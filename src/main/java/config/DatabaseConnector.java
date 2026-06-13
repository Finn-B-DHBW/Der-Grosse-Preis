package config;

import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

public class DatabaseConnector {
    private static final Logger log = Logger.getLogger(DatabaseConnector.class.getName());
    private static final String DB_URL = "jdbc:sqlite:database.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void ensureTables(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Configuration (" +
                "configId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL" +
                ");"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ConfigCategory (" +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "configId INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "FOREIGN KEY (configId) REFERENCES Configuration(configId) ON DELETE CASCADE" +
                ");"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ConfigQuestionEntry (" +
                "questionEntryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryId INTEGER NOT NULL, " +
                "points INTEGER NOT NULL, " +
                "questionText TEXT, " +
                "answer1 TEXT, " +
                "answer2 TEXT, " +
                "answer3 TEXT, " +
                "correctAnswer INTEGER DEFAULT 0, " +
                "FOREIGN KEY (categoryId) REFERENCES ConfigCategory(categoryId) ON DELETE CASCADE" +
                ");"
            );
        }
    }

    public static List<Configuration> loadConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        try (Connection con = getConnection()) {
            ensureTables(con);

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT configId, title FROM Configuration ORDER BY configId")) {
                while (rs.next()) {
                    int configId = rs.getInt("configId");
                    String title = rs.getString("title");
                    Configuration config = new Configuration(title);
                    config.setConfigId(configId);

                    List<Category> categories = loadCategories(con, configId);
                    for (Category cat : categories) {
                        config.addCategory(cat);
                    }
                    configurations.add(config);
                }
            }
        } catch (SQLException e) {
            log.severe("Error loading configurations: " + e.getMessage());
        }
        return configurations;
    }

    private static List<Category> loadCategories(Connection con, int configId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT categoryId, name FROM ConfigCategory WHERE configId = ?")) {
            ps.setInt(1, configId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int categoryId = rs.getInt("categoryId");
                    String name = rs.getString("name");
                    Map<Integer, ConfigQuestion> questionsMap = loadQuestions(con, categoryId);
                    categories.add(new Category(name, questionsMap));
                }
            }
        }
        return categories;
    }

    private static Map<Integer, ConfigQuestion> loadQuestions(Connection con, int categoryId) throws SQLException {
        Map<Integer, ConfigQuestion> questionsMap = new TreeMap<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT points, questionText, answer1, answer2, answer3, correctAnswer " +
                "FROM ConfigQuestionEntry WHERE categoryId = ? ORDER BY points")) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int points = rs.getInt("points");
                    ConfigQuestion q = new ConfigQuestion(3);
                    q.setQuestion(rs.getString("questionText"));
                    q.setAnswer(0, rs.getString("answer1"));
                    q.setAnswer(1, rs.getString("answer2"));
                    q.setAnswer(2, rs.getString("answer3"));
                    q.setCorrectAnswer(rs.getInt("correctAnswer"));
                    questionsMap.put(points, q);
                }
            }
        }
        return questionsMap;
    }

    public static void saveConfiguration(Configuration configuration) {
        if (configuration == null) return;
        try (Connection con = getConnection()) {
            ensureTables(con);
            con.setAutoCommit(false);

            int configId = insertConfiguration(con, configuration);
            configuration.setConfigId(configId);
            for (Category cat : configuration.getCategories()) {
                int categoryId = insertCategory(con, configId, cat);
                for (Map.Entry<Integer, ConfigQuestion> entry : cat.getPointQuestionMap().entrySet()) {
                    insertQuestion(con, categoryId, entry.getKey(), entry.getValue());
                }
            }

            con.commit();
            log.info("Configuration saved successfully: " + configuration.getTitle());
        } catch (SQLException e) {
            log.severe("Error saving configuration: " + e.getMessage());
        }
    }

    public static void deleteConfiguration(int configId) {
        try (Connection con = getConnection()) {
            ensureTables(con);
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM ConfigQuestionEntry WHERE categoryId IN " +
                    "(SELECT categoryId FROM ConfigCategory WHERE configId = ?)")) {
                ps.setInt(1, configId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM ConfigCategory WHERE configId = ?")) {
                ps.setInt(1, configId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Configuration WHERE configId = ?")) {
                ps.setInt(1, configId);
                ps.executeUpdate();
            }

            con.commit();
            log.info("Configuration deleted: id=" + configId);
        } catch (SQLException e) {
            log.severe("Error deleting configuration: " + e.getMessage());
        }
    }

    private static int insertConfiguration(Connection con, Configuration configuration) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Configuration(title) VALUES(?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, configuration.getTitle());
            ps.executeUpdate();
            return generatedKey(ps);
        }
    }

    private static int insertCategory(Connection con, int configId, Category category) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ConfigCategory(configId, name) VALUES(?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, configId);
            ps.setString(2, category.getName());
            ps.executeUpdate();
            return generatedKey(ps);
        }
    }

    private static void insertQuestion(Connection con, int categoryId, int points, ConfigQuestion question) throws SQLException {
        List<String> answers = question.getAnswers();
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ConfigQuestionEntry(categoryId, points, questionText, " +
                "answer1, answer2, answer3, correctAnswer) VALUES(?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, categoryId);
            ps.setInt(2, points);
            ps.setString(3, question.getQuestion());
            ps.setString(4, answerOrEmpty(answers, 0));
            ps.setString(5, answerOrEmpty(answers, 1));
            ps.setString(6, answerOrEmpty(answers, 2));
            ps.setInt(7, question.getCorrectAnswer());
            ps.executeUpdate();
        }
    }

    private static int generatedKey(PreparedStatement ps) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            keys.next();
            return keys.getInt(1);
        }
    }

    private static String answerOrEmpty(List<String> answers, int index) {
        return answers.size() > index ? answers.get(index) : "";
    }
}
