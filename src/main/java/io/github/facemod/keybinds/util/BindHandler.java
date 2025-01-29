package io.github.facemod.keybinds.util;

import io.github.facemod.FaceModInitializer;

public class BindHandler {
    private static final FaceModInitializer INSTANCE = FaceModInitializer.INSTANCE;

    public static void MOUNT() {
        if(INSTANCE.CLIENT.player == null){
            return;
        }

        boolean isMounted = INSTANCE.CLIENT.player.hasVehicle();

        if (!isMounted) {
            INSTANCE.CLIENT.player.setSprinting(false);
            INSTANCE.sendCommand("mount");
        }
    }

    public static void ESCAPE() {
        INSTANCE.sendCommand("escape");
    }

    public static void SPELL_ONE() {
        FaceModInitializer.INSTANCE.swapHotbar(0);
    }

    public static void SPELL_TWO() {
        FaceModInitializer.INSTANCE.swapHotbar(1);
    }

    public static void SPELL_THREE() {
        FaceModInitializer.INSTANCE.swapHotbar(2);
    }

    public static void SPELL_FOUR() {
        FaceModInitializer.INSTANCE.swapHotbar(3);
    }
}
