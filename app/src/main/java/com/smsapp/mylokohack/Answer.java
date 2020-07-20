package com.smsapp.mylokohack;

import java.util.List;
import java.util.Map;

public class Answer {

   private String question;
    private String answer;
    private  String optionA;
    private   String optionB;
    private String optionC;
    private String countofA;
    private  String countofB;
    private  String countofC;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    private String max;

    public String getCountofA() {
        return countofA;
    }

    public void setCountofA(String countofA) {
        this.countofA = countofA;
    }

    public String getCountofB() {
        return countofB;
    }

    public void setCountofB(String countofB) {
        this.countofB = countofB;
    }

    public String getCountofC() {
        return countofC;
    }

    public void setCountofC(String countofC) {
        this.countofC = countofC;
    }

    public Answer()
{

}
    public Answer(String question, String optionA, String optionB, String optionC) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
