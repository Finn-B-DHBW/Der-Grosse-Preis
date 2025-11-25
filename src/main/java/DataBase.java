import java.sql.*;

public class DataBase {
    private GameManager gameManager;
    private Connection con;

    //todo db connection keine prio
    DataBase(GameManager gameManager) {
        this.gameManager = gameManager;

        try {
            con = DriverManager.getConnection("jdbc:sqlite:database.db");
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
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

        Statement stmt = con.createStatement();
        stmt.executeUpdate(createQuestionTable);
        stmt.executeUpdate(createWrongAnswerTable);
        System.out.println("Created database successfully");

        stmt.close();
    }

    public boolean addQuestion(Question question){
        return addQuestion(question.getQuestionText(), question.getCategory(), question.getRightAnswer(), question.getWrongAnswers(), question.getScore());
    }

    public boolean addQuestion(String questionText, String category, String rightAnswer, String[] wrongAnswer, int score){
        try{
            String insertSQL = "INSERT INTO Question(questionText, category, rightAnswer, score) VALUES(?,?,?,?)";

            PreparedStatement preparedStatement = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, questionText);
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, rightAnswer);

            preparedStatement.setInt(4, score);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int questionId = 0;
            if(resultSet.next())
            {
                questionId = resultSet.getInt(1);
            }

            insertSQL = "INSERT INTO WrongAnswer(wrongAnswerText, questionId) VALUES(?,?)";
            preparedStatement = con.prepareStatement(insertSQL);
            for(String wrongAnswerText : wrongAnswer){
                preparedStatement.setString(1, wrongAnswerText);
                preparedStatement.setInt(2, questionId);
                preparedStatement.executeUpdate();
            }
        }catch (SQLException e){
            System.out.println("Error adding question to database");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Question getQuestions(int amount, String[] category){

        int amountPerCategory = amount / category.length;
        

        String selectSQL = "SELECT * FROM Question WHERE category = ? LIMIT ?";

        return null;
    }

}
