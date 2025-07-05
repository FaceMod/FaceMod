package io.github.facemod.keybinds.util;

import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public class KeypressTracker {
    private static final Map<KeyBinding, Boolean> lastStates = new HashMap<>();

    public static boolean justPressed(KeyBinding key) {
        boolean isDown = key.isPressed();
        boolean last = lastStates.getOrDefault(key, false);
        boolean result = isDown && !last;
        lastStates.put(key, isDown);
        return result;
    }
}
