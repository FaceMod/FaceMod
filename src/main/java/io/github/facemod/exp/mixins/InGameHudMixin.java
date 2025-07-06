package io.github.facemod.exp.mixins;

import io.github.facemod.exp.utils.FaceExp;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderExpSquare(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (!FaceExp.hasRecentExpGain(Duration.ofMinutes(1))) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int boxWidth = 120;
        int boxHeight = 15;
        int padding = 5;

        int x = padding;
        int y = padding;

        context.fill(x, y, x + boxWidth, y + boxHeight, 0x1ACCCCCC);

        String expText = String.format("%s EXP/hr: %.0f", FaceExp.lastCategory, FaceExp.lastExpPerHour);

        context.drawText(client.textRenderer, expText, x + 5, y + 8, 0xFFFFFFFF, true);
    }
}
