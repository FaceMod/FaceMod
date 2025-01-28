package io.github.facemod;

import io.github.facemod.config.FaceConfig;
import io.github.facemod.mount.util.Mount;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FaceModInitializer implements ClientModInitializer {
    public final Logger logger = LoggerFactory.getLogger(FaceModInitializer.class);
    public static FaceModInitializer INSTANCE;
    public FaceConfig CONFIG;
    public MinecraftClient CLIENT;

    @Override
    public void onInitializeClient() {
        logger.info("FaceMod initialized!");
        INSTANCE = this;
        CLIENT = MinecraftClient.getInstance();
        AutoConfig.register(FaceConfig.class, GsonConfigSerializer::new);
        var holder = AutoConfig.getConfigHolder(FaceConfig.class);
        CONFIG = holder.getConfig();
        FaceConfig.holder = holder;

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> client.execute(() -> {
            ClientConnection connection = Objects.requireNonNull(client.getNetworkHandler()).getConnection();
            if (connection != null && connection.getAddress() != null) {
                String serverAddress = connection.getAddress().toString().toLowerCase();
                FaceConfig.General.onFaceLand = serverAddress.startsWith("local") || serverAddress.contains("face.land");
            }
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            FaceConfig.General.onFaceLand = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FaceConfig.General.onFaceLand || client == null || client.player == null) {
                return;
            }

            Mount.update();

        });
    }

}