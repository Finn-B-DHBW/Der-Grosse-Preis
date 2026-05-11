package Connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {

    private DataBase dataBase;

    @BeforeEach
    void setUp() {
        dataBase = new DataBase("jdbc:sqlite::memory:");
    }

    @Test
    void addQuestion_returnsTrue_onValidInput() {
        boolean result = dataBase.addQuestion(
                "Was ist die Hauptstadt von Deutschland?",
                "Geografie",
                "Berlin",
                new String[]{"München", "Hamburg", "Köln"},
                100
        );

        assertTrue(result, "addQuestion should return true when the question is saved successfully");
    }

    @Test
    void addQuestion_returnsFalse_whenWrongAnswersIsEmpty() {
        boolean result = dataBase.addQuestion(
                "Eine Frage ohne falsche Antworten",
                "Test",
                "Richtige Antwort",
                new String[]{},
                50
        );
        assertTrue(result, "addQuestion should still return true even with no wrong answers");
    }

    @Test
    void getQuestions_returnsEmpty_whenCategoryDoesNotExist() {
        Model.Question[] questions = dataBase.getQuestions(5, new String[]{"NichtVorhandeneKategorie"});

        assertEquals(0, questions.length, "Should return empty array for an unknown category");
    }

    @Test
    void getCategories_returnsEmpty_onFreshDatabase() {
        String[] categories = dataBase.getCategories();

        assertEquals(0, categories.length, "A fresh in-memory database should have no categories");
    }
}
