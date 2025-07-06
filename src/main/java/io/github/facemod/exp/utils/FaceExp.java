package io.github.facemod.exp.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FaceExp {
    public static boolean hasCachedSkills = false;
    public static String lastCategory = "";
    public static double lastExpPerHour = 0;
    public static List<ExpGain> xpHistory = new ArrayList<>();
    public static List<FaceSkill> skillCache = new ArrayList<>();

    public static boolean hasRecentExpGain(Duration within) {
        Instant now = Instant.now();
        return xpHistory.stream().anyMatch(g -> Duration.between(g.time, now).compareTo(within) <= 0);
    }

    public static int getRecentExpGain(String category, Duration within) {
        Instant now = Instant.now();
        return xpHistory.stream()
                .filter(g -> g.category.equalsIgnoreCase(category))
                .filter(g -> Duration.between(g.time, now).compareTo(within) <= 0)
                .mapToInt(g -> g.amount)
                .sum();
    }

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
