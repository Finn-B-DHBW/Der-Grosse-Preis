package config.builder;

import config.model.Category;
import config.model.ConfigQuestion;
import config.model.Configuration;

import java.util.Map;
import java.util.TreeMap;

public class CategoryBuilder {
   private Configuration configuration;
   public int numberOfQuestions;

   public CategoryBuilder(Configuration configuration, int numberOfQuestions) {
       this.configuration = configuration;
       this.numberOfQuestions = numberOfQuestions;
   }

   public void setNumberOfQuestions(int numberOfQuestions) {
       this.numberOfQuestions = numberOfQuestions;
   }

   public void createNewCategory(String name){
       Map<Integer, ConfigQuestion> questionsMap = new TreeMap<>();

       for (int i = 1; i <= numberOfQuestions; i++) {
           int points = i * 10;
           questionsMap.put(points, new ConfigQuestion(3));
       }

       Category category = new Category(name, questionsMap);
       configuration.addCategory(category);
   }
}
