package Connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.logging.Logger;

public class QuestionSeeder {

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void seedIfEmpty(DataBase dataBase) {
        if (dataBase.hasSeedData()) {
            log.info("Database already contains questions, skipping seed.");
            return;
        }

        try (InputStream is = QuestionSeeder.class.getClassLoader().getResourceAsStream("questions.json")) {
            if (is == null) {
                log.severe("questions.json not found in resources.");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode categories = root.get("categories");

            int inserted = 0;
            for (JsonNode category : categories) {
                String categoryName = category.get("name").asText();
                for (JsonNode q : category.get("questions")) {
                    String questionText = q.get("questionText").asText();
                    String rightAnswer = q.get("rightAnswer").asText();
                    JsonNode wrongAnswersNode = q.get("wrongAnswers");
                    String[] wrongAnswers = new String[wrongAnswersNode.size()];
                    for (int i = 0; i < wrongAnswersNode.size(); i++) {
                        wrongAnswers[i] = wrongAnswersNode.get(i).asText();
                    }
                    if (dataBase.addQuestion(questionText, categoryName, rightAnswer, wrongAnswers, 0)) {
                        inserted++;
                    }
                }
            }
            log.info("Seeded " + inserted + " questions from questions.json");
        } catch (Exception e) {
            log.severe("Failed to seed questions: " + e.getMessage());
        }
    }
}
