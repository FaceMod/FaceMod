package io.github.facemod.exp.utils;

public class FaceSkill {
    public String category;
    public int currentExp;
    public int currentLevel;
    public int maxExp;

    public FaceSkill(String c, int level, int cexp, int mexp){
        category = c;
        currentExp = cexp;
        currentLevel = level;
        maxExp = mexp;
    }
}
