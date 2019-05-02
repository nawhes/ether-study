package com.etherstudy.quizdapp;

import java.util.ArrayList;

public class QuizModel {

    public int id;
    public String quiz;
    public String answer;
    public int difficulty;
    public String category;
    public String createdAt;
    public int round;

    public ArrayList<QuizAnswerModel> quizAnswerList;
}
