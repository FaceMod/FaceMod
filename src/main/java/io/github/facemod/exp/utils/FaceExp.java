package io.github.facemod.exp.utils;

public class FaceExp {
    public static double getCombatLevelExp(int level){
        return 75 + (level * 50) + (Math.pow(level,3.6) * (0.5 + (level*0.005))) + Math.pow(10000,((double) level /30 - 1));
    }

    public static double getCombatSkillsExp(int level){
        return 30 + (level * 30) + Math.pow(level,2.75);
    }

    public static double getProfessionExp(int level){
        return 20 + (level * 30) + Math.pow(level,2.71);
    }
}
