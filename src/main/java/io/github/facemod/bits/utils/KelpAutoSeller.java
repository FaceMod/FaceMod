package io.github.facemod.bits.utils;

import io.github.facemod.config.FaceConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class KelpAutoSeller {
    private static boolean active = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
                active = false;
                return;
            }

            if (!FaceConfig.General.autoSell){
                active = false;
                return;
            }

            String title = screen.getTitle().getString();
            if (!title.contains("倱")) {
                active = false;
                return;
            }

            if (!active) {
                active = true;
            }

            autoSellKelp(client, screen);
        });
    }

    private static long lastClickTime = 0;

    private static void autoSellKelp(MinecraftClient client, HandledScreen<?> screen) {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 200) return;
        lastClickTime = now;

        ClientPlayerEntity player = client.player;
        if (player == null || player.currentScreenHandler == null) return;

        int slotCount = player.currentScreenHandler.slots.size();
        int playerInvStart = slotCount - 36;
        for (int i = playerInvStart; i < slotCount; i++) {
            if (player.currentScreenHandler.getSlot(i).getStack().getItem() == Items.KELP) {
                client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
                client.interactionManager.clickSlot(player.currentScreenHandler.syncId, 32, 0, SlotActionType.PICKUP, player);
                client.interactionManager.clickSlot(player.currentScreenHandler.syncId, 32, 0, SlotActionType.PICKUP, player);
                return;
            }
        }
    }
}
