package io.github.facemod.keybinds.util;

import io.github.facemod.FaceModInitializer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.Objects;

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

    public static void POTION_HEALING() {
        if(INSTANCE.CLIENT.player == null){
            return;
        }
        PlayerInventory inventory = INSTANCE.CLIENT.player.getInventory();
        int previousSlot = inventory.getSelectedSlot();
        int potionSlot = findLifePotion(inventory);

        if (potionSlot == -1){
            INSTANCE.CLIENT.player.sendMessage(Text.of("[FaceMod] >> No Health Potion in HotBar."),false);
            return;
        }

        if (potionSlot < 0 || potionSlot > 8) return;

        INSTANCE.CLIENT.player.getInventory().setSelectedSlot(potionSlot);
        Objects.requireNonNull(INSTANCE.CLIENT.getNetworkHandler()).sendPacket(new UpdateSelectedSlotC2SPacket(potionSlot));

        Hand hand = Hand.MAIN_HAND;
        assert INSTANCE.CLIENT.interactionManager != null;
        INSTANCE.CLIENT.interactionManager.interactItem(INSTANCE.CLIENT.player, hand);

        INSTANCE.CLIENT.execute(() -> {
            try {
                Thread.sleep(100); // Small delay to ensure action is sent before switching back
            } catch (InterruptedException ignored) {}

            // Swap back to previous item
            INSTANCE.CLIENT.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            inventory.setSelectedSlot(previousSlot);
        });    }

    public static void POTION_ENERGY() {
        if(INSTANCE.CLIENT.player == null){
            return;
        }
        PlayerInventory inventory = INSTANCE.CLIENT.player.getInventory();
        int previousSlot = inventory.getSelectedSlot();
        int potionSlot = findEnergyPotion(inventory);

        if (potionSlot == -1) {
            INSTANCE.CLIENT.player.sendMessage(Text.of("[FaceMod] >> No Energy Potion in HotBar."),false);
            return;
        }

        if (potionSlot < 0 || potionSlot > 8) return;

        INSTANCE.CLIENT.player.getInventory().setSelectedSlot(potionSlot);
        Objects.requireNonNull(INSTANCE.CLIENT.getNetworkHandler()).sendPacket(new UpdateSelectedSlotC2SPacket(potionSlot));

        Hand hand = Hand.MAIN_HAND; // Assuming potion is in main hand
        assert INSTANCE.CLIENT.interactionManager != null;
        INSTANCE.CLIENT.interactionManager.interactItem(INSTANCE.CLIENT.player, hand);

        INSTANCE.CLIENT.execute(() -> {
            try {
                Thread.sleep(100); // Small delay to ensure action is sent before switching back
            } catch (InterruptedException ignored) {}

            // Swap back to previous item
            INSTANCE.CLIENT.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            inventory.setSelectedSlot(previousSlot);
        });
    }

    public static int findLifePotion(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getName().getString().contains("Life Potion")) {
                return i;
            }
        }
        return -1;
    }

    public static int findEnergyPotion(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getName().getString().contains("Energy Potion")) {
                return i;
            }
        }
        return -1;
    }

}
