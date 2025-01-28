package io.github.facemod;

import io.github.facemod.mount.util.Mount;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaceModInitializer implements ClientModInitializer {
    public final Logger logger = LoggerFactory.getLogger(FaceModInitializer.class);
    public static FaceModInitializer INSTANCE;

    public MinecraftClient CLIENT;

    @Override
    public void onInitializeClient() {
        logger.info("FaceMod initialized!");
        INSTANCE = this;
        CLIENT = MinecraftClient.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            /*if(!onFaceland){
                return;
            }*/

            Mount.update();

        });
    }

}