package Connection;

import Model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DataBase {
    private Connection con;
    private Logger log;

    //todo db connection keine prio
    public DataBase() {
        log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        try {

            con = DriverManager.getConnection("jdbc:sqlite:database.db");
            createTable();
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private void createTable(){
        try(Statement stmt = con.createStatement()){
            String createQuestionTable =
                    "CREATE TABLE IF NOT EXISTS Question (" +
                            "questionText varchar(255), " +
                            "category varchar(255), " +
                            "rightAnswer varchar(255), " +
                            "score integer, " +
                            "questionId integer primary key autoincrement" +
                            ");";

            String createWrongAnswerTable =
                    "CREATE TABLE IF NOT EXISTS WrongAnswer (" +
                            "wrongAnswerText varchar(255), " +
                            "questionId integer, " +
                            "WrongAnswerID integer primary key autoincrement" +
                            ");";

            stmt.executeUpdate(createQuestionTable);
            stmt.executeUpdate(createWrongAnswerTable);
            log.info("Created database successfully");
        }catch (SQLException e){
            log.severe(e.getMessage());
            log.severe("Failed to create database");
        }

    }

    public boolean addQuestion(Question question){
        return addQuestion(question.getQuestionText(), question.getCategory(), question.getRightAnswer(), question.getWrongAnswers(), question.getScore());
    }

    public boolean addQuestion(String questionText, String category, String rightAnswer, String[] wrongAnswer, int score){
        String insertQuestion = "INSERT INTO Question(questionText, category, rightAnswer, score) VALUES(?,?,?,?)";
        int questionId = 0;

        //einfügen der Frage in die Model.Question DB; in questionId wird der primaryKey gespeichert für die Wrong answers(1:m beziehung)
        try(PreparedStatement preparedStatement = con.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, questionText);
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, rightAnswer);

            preparedStatement.setInt(4, score);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                questionId = resultSet.getInt(1);
            }
        }catch (SQLException e){
            log.severe("Error adding question to question database");
            return false;
        }

        //hier werden noch die falschen Antwortmöglichkeiten eingefügt
        String insertWrongAnswer = "INSERT INTO WrongAnswer(wrongAnswerText, questionId) VALUES(?,?)";

        try(PreparedStatement preparedStatement = con.prepareStatement(insertWrongAnswer)){
            for(String wrongAnswerText : wrongAnswer){
                preparedStatement.setString(1, wrongAnswerText);
                preparedStatement.setInt(2, questionId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }catch (SQLException e){

            //falls es ein problem beim einfügen der falschen antworten geben soll wird die Neu eingetragene Frage gelöscht
            //um zu verhindern das es eine frage ohne falsche antworten gibt
            String deleteQuestion = "DELETE FROM Question WHERE questionId = ?";
            try(PreparedStatement delete = con.prepareStatement(deleteQuestion)){
                delete.setInt(1, questionId);
                delete.executeUpdate();

            }catch (SQLException er){
                log.severe("Error deleting question from database");
            }
            log.severe("Error adding question to database");
            return false;
        }
        log.info("Added question and Wrong Answers to database");
        return true;
    }

    public Question[] getQuestions(int amount, String[] categories) {

        ArrayList<Question> questions = new ArrayList<>();
        int amountPerCategory = amount / categories.length;

        for (String category : categories) {
            String selectQuestions = "SELECT questionText, rightAnswer, score, questionId FROM Question WHERE category = ? LIMIT ?";
            try (PreparedStatement prep = con.prepareStatement(selectQuestions)) {

                prep.setString(1, category);
                prep.setInt(2, amountPerCategory);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {
                    questions.add(new Question(rs.getString("questionText"), rs.getString("category"), rs.getString("rightAnswer"), rs.getInt("score"), rs.getInt("questionId")));
                }

            } catch (SQLException e) {
                log.severe(e.getMessage());
                return new Question[0];
            }

        }
        String selectWrongAnswers = "SELECT wrongAnswerText FROM WrongAnswer WHERE questionId = ?";

        for (Question question : questions) {

            try (PreparedStatement prep = con.prepareStatement(selectWrongAnswers)) {

                prep.setInt(1, question.getQuestionId());

                ResultSet rs = prep.executeQuery();

                question.setWrongAnswers(rs.toString().split(","));
            } catch (SQLException e) {
                log.severe(e.getMessage());
                return new Question[0];
            }
        }

        return questions.toArray(new Question[0]);
    }

    //todo hier infos es soll nicht ausversehen eine neue category erstellt werden können durch tippfehler
    public String[] getCategories(){
        try(Statement stmt = con.createStatement()){
            String selectCategories = "SELECT DISTINCT category FROM Question";

            return stmt.executeQuery(selectCategories).toString().split(",");
        }catch (SQLException e){
            log.severe(e.getMessage());
        }
        return new String[0];
    }
}
