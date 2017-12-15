package e.natasja.natasjawezel__pset6;

/**
 * Created by Natasja on 15-12-2017.
 * A question class to construct the data source for the questions.
 */

public class Question {
    // properties of the class
    String question;
    String correctAnswer;
    String answer1;
    String answer2;
    String answer3;

    // constructor of the class
    public Question(String aQuestion, String aCorrectAnswer, String anAnswer1, String anAnswer2, String anAnswer3) {
        this.question = aQuestion;
        this.correctAnswer = aCorrectAnswer;
        this.answer1 = anAnswer1;
        this.answer2 = anAnswer2;
        this.answer3 = anAnswer3;
    }
}
