package io.github.facemod.exp.mixins;

import io.github.facemod.config.FaceConfig;
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

        if(!FaceConfig.General.expCalculator) return;

        if (!FaceExp.hasRecentExpGain(Duration.ofMinutes(1))) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int padding = 5;

        String expText = String.format("%s EXP/hr: %.0f", FaceExp.lastCategory, FaceExp.lastExpPerHour);
        String ttlText = "TTL: 360.2m";  // TODO: get actual time-to-level
        String recentText = String.format("+%d EXP (last 60s)", FaceExp.getRecentExpGain(FaceExp.lastCategory, Duration.ofSeconds(60)));

        int lineHeight = client.textRenderer.fontHeight + 2;
        int lineCount = 3;

        int boxWidth = 140;
        int boxHeight = (lineHeight * lineCount) + 4;

        int x = padding;
        int y = padding;

        context.fill(x, y, x + boxWidth, y + boxHeight, 0x1ACCCCCC);

        int textX = x + 5;
        int textY = y + 4;

        context.drawText(client.textRenderer, expText, textX, textY, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, ttlText, textX, textY + lineHeight, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, recentText, textX, textY + lineHeight * 2, 0xFFFFFFFF, true);
    }
}
