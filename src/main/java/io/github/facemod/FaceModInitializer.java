package io.github.facemod;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaceModInitializer implements ClientModInitializer {
    private final Logger logger = LoggerFactory.getLogger(FaceModInitializer.class);

    @Override
    public void onInitializeClient() {
        logger.info("FaceMod initialized!");
    }
}