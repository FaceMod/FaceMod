package io.github.facemod;

import io.github.facemod.keybinds.util.BindHandler;
import io.github.facemod.keybinds.util.FaceBinds;
import io.github.facemod.config.FaceConfig;
import io.github.facemod.mount.util.Mount;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FaceModInitializer implements ClientModInitializer {
    public final Logger logger = LoggerFactory.getLogger(FaceModInitializer.class);
    public static FaceModInitializer INSTANCE;
    public FaceConfig CONFIG;
    public MinecraftClient CLIENT;
    public FaceBinds FACE_BINDS = new FaceBinds(); // FACE_BINDS must be defined here, despite it being a static method it crashes otherwise.

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

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FaceConfig.General.onFaceLand = false);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FaceConfig.General.onFaceLand || client == null || client.player == null) {
                return;
            }

            Mount.update();

            handleKeyBinds();

        });
    }

    public void sendCommand(String command) {
        if (CLIENT.player == null) {
            return;
        }
        CLIENT.player.networkHandler.sendChatCommand(command);
    }

    public void swapHotbar(int slotIndex) {
        if (CLIENT.player == null) {
            return;
        }
        CLIENT.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slotIndex));
    }

    public void handleKeyBinds() {
        if (FaceBinds.MOUNT.wasPressed()) {
            BindHandler.MOUNT();
        }

        if (FaceBinds.ESCAPE.wasPressed()) {
            BindHandler.ESCAPE();
        }

        if (FaceBinds.SPELL_ONE.wasPressed()) {
            BindHandler.SPELL_ONE();
        }

        if (FaceBinds.SPELL_TWO.wasPressed()) {
            BindHandler.SPELL_TWO();
        }

        if (FaceBinds.SPELL_THREE.wasPressed()) {
            BindHandler.SPELL_THREE();
        }

        if (FaceBinds.SPELL_FOUR.wasPressed()) {
            BindHandler.SPELL_FOUR();
        }

        if (FaceBinds.POTION_HEALING.wasPressed()) {
            BindHandler.POTION_HEALING();
        }

        if (FaceBinds.POTION_ENERGY.wasPressed()) {
            BindHandler.POTION_ENERGY();
        }
    }

}