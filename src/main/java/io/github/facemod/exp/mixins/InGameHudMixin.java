package io.github.facemod.exp.mixins;

import io.github.facemod.config.FaceConfig;
import io.github.facemod.exp.utils.FaceExp;
import io.github.facemod.exp.utils.FaceSkill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.util.Objects;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderExpSquare(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if(!FaceConfig.General.expCalculator) return;

        if (!FaceExp.hasRecentExpGain(Duration.ofSeconds(30))) return;

        int padding = 5;

        String expText;
        if(FaceExp.lastExpPerHour != 0) {
            expText = String.format("%s EXP/hr: %.0f", FaceExp.lastCategory, FaceExp.lastExpPerHour);
        } else {
            expText = String.format("%s EXP/hr: N/A", FaceExp.lastCategory);
        }

        String ttlText = getTtlText();

        String recentText = String.format("+%d EXP (last 60s)", FaceExp.getRecentExpGain(FaceExp.lastCategory, Duration.ofSeconds(60)));

        int count = FaceExp.getRecentExpCount(FaceExp.lastCategory, Duration.ofHours(1));

        String hourText = String.format("+%d (last 60m)", count);
        FaceSkill sk = null;

        for(FaceSkill skill : FaceExp.skillCache){
            if(Objects.equals(skill.category, FaceExp.lastCategory)){
                sk = skill;
                break;
            }
        }

        String progressText = "N/A / N/A";
        if(sk != null) {
            progressText = String.format("%d/%d (%.0f%%)", sk.currentExp, sk.maxExp,
                    (sk.currentExp * 100.0) / sk.maxExp);
        }

        int lineHeight = client.textRenderer.fontHeight + 2;
        int lineCount = 5;

        int boxWidth = 140;
        int boxHeight = (lineHeight * lineCount) + 4;

        context.fill(padding, padding, padding + boxWidth, padding + boxHeight, 0x1ACCCCCC);

        int textX = padding + 5;
        int textY = padding + 4;

        context.drawText(client.textRenderer, expText, textX, textY, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, ttlText, textX, textY + lineHeight, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, hourText, textX, textY + lineHeight * 2, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, recentText, textX, textY + lineHeight * 3, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, progressText, textX, textY + lineHeight * 4, 0xFFFFFFFF, true);
    }


    @Unique
    private static @NotNull String getTtlText() {
        FaceSkill skillData = null;
        for (FaceSkill skill : FaceExp.skillCache) {
            if (skill.category.equalsIgnoreCase(FaceExp.lastCategory)) {
                skillData = skill;
                break;
            }
        }

        String ttlText = "Next Level in: N/A";
        if (skillData != null && FaceExp.lastExpPerHour > 0) {
            int xpLeft = skillData.maxExp - skillData.currentExp;
            double hoursToNextLevel = (double) xpLeft / FaceExp.lastExpPerHour;
            long seconds = (long)(hoursToNextLevel * 3600);

            long mins = seconds / 60;
            long secs = seconds % 60;
            ttlText = String.format("Next Level in: %dm %02ds", mins, secs);
        }
        return ttlText;
    }
}
