package io.github.facemod.exp.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class ExpGain {
    public final Instant time;
    public final String category;
    public final int amount;

    public ExpGain(String category, int amount) {
        this.time = Instant.now(); // precise timestamp
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "[" + time + "] " + category + " +" + amount + "XP";
    }

    public static double getExpPerHour(String category, List<ExpGain> history) {
        List<ExpGain> filtered = history.stream()
                .filter(g -> g.category.equalsIgnoreCase(category))
                .sorted(Comparator.comparing(g -> g.time))
                .toList();

        if (filtered.size() < 2) return 0;

        int totalXP = filtered.stream().mapToInt(g -> g.amount).sum();

        Instant start = filtered.get(0).time;
        Instant end = filtered.get(filtered.size() - 1).time;

        double hours = Math.max(Duration.between(start, end).toMillis() / 3600000.0, 0.01); // prevent divide by 0

        return totalXP / hours;
    }

    public static void trimOldEntries(List<ExpGain> history) {
        Instant cutoff = Instant.now().minus(Duration.ofHours(1));
        history.removeIf(entry -> entry.time.isBefore(cutoff));
    }
}
