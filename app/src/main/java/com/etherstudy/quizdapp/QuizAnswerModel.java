package com.etherstudy.quizdapp;

import com.google.gson.annotations.SerializedName;


public class QuizAnswerModel {

    public int id;

    @SerializedName("case")
    public String answerCase;
}
