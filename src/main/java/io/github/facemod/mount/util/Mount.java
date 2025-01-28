package io.github.facemod.mount.util;

import io.github.facemod.FaceModInitializer;
import net.minecraft.client.option.Perspective;

public class Mount {
    public static boolean mounted = false;

    public static void update() {
        if (FaceModInitializer.INSTANCE.CLIENT.player == null) {
            return;
        }

        if (FaceModInitializer.INSTANCE.CLIENT.player.getVehicle() != null &&
                FaceModInitializer.INSTANCE.CLIENT.options.getPerspective() != Perspective.THIRD_PERSON_BACK) {
            FaceModInitializer.INSTANCE.CLIENT.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            mounted = true;
        } else if (mounted && FaceModInitializer.INSTANCE.CLIENT.player.getVehicle() == null &&
                FaceModInitializer.INSTANCE.CLIENT.options.getPerspective() != Perspective.FIRST_PERSON) {
            mounted = false;
            FaceModInitializer.INSTANCE.CLIENT.options.setPerspective(Perspective.FIRST_PERSON);

        }
    }
}
