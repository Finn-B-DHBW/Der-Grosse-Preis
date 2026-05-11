package Connection;

import Model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseIT {

    private DataBase dataBase;

    @BeforeEach
    void setUp() {
        dataBase = new DataBase("jdbc:sqlite::memory:");
    }

    @Test
    void addReadAndDeleteQuestion_fullCycleSucceeds() {
        // --- ADD ---
        boolean added = dataBase.addQuestion(
                "Was ist die chemische Formel von Wasser?",
                "Chemie",
                "H2O",
                new String[]{"CO2", "NaCl", "O2"},
                100
        );
        assertTrue(added, "addQuestion should return true on success");

        // --- READ ---
        Question[] questions = dataBase.getQuestions(1, new String[]{"Chemie"});
        assertEquals(1, questions.length, "Exactly one question should be returned");

        Question loaded = questions[0];
        assertEquals("Was ist die chemische Formel von Wasser?", loaded.getQuestionText());
        assertEquals("Chemie", loaded.getCategory());
        assertEquals("H2O", loaded.getRightAnswer());
        assertEquals(100, loaded.getScore());
        assertNotNull(loaded.getWrongAnswers());
        assertEquals(3, loaded.getWrongAnswers().length);

        // --- DELETE ---
        boolean deleted = dataBase.deleteQuestion(loaded.getQuestionId());
        assertTrue(deleted, "deleteQuestion should return true on success");

        // --- VERIFY GONE ---
        Question[] afterDelete = dataBase.getQuestions(10, new String[]{"Chemie"});
        assertEquals(0, afterDelete.length, "No questions should remain after deletion");
    }

    @Test
    void addMultipleQuestions_getCategories_returnsDistinctCategories() {
        dataBase.addQuestion("Frage 1", "Wissenschaft", "Antwort 1", new String[]{"F1", "F2", "F3"}, 100);
        dataBase.addQuestion("Frage 2", "Wissenschaft", "Antwort 2", new String[]{"F1", "F2", "F3"}, 200);
        dataBase.addQuestion("Frage 3", "Geschichte",   "Antwort 3", new String[]{"F1", "F2", "F3"}, 100);

        String[] categories = dataBase.getCategories();

        assertEquals(2, categories.length, "Should return 2 distinct categories");
    }

    @Test
    void deleteQuestion_removesOnlyTargetQuestion() {
        dataBase.addQuestion("Frage A", "Sport", "A", new String[]{"X", "Y", "Z"}, 100);
        dataBase.addQuestion("Frage B", "Sport", "B", new String[]{"X", "Y", "Z"}, 200);

        Question[] before = dataBase.getQuestions(10, new String[]{"Sport"});
        assertEquals(2, before.length);

        dataBase.deleteQuestion(before[0].getQuestionId());

        Question[] after = dataBase.getQuestions(10, new String[]{"Sport"});
        assertEquals(1, after.length, "Only one question should remain after deleting the other");
    }
}
