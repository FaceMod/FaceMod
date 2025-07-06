package io.github.facemod.exp.mixins;

import io.github.facemod.exp.utils.ExpGain;
import io.github.facemod.exp.utils.FaceExp;
import io.github.facemod.exp.utils.FaceSkill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.facemod.exp.utils.ExpGain.getExpPerHour;
import static io.github.facemod.exp.utils.ExpGain.trimOldEntries;
import static io.github.facemod.exp.utils.FaceExp.getCombatLevelExp;
import static io.github.facemod.exp.utils.FaceExp.xpHistory;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String txt = packet.content().getString();

        Pattern normalPattern = Pattern.compile("Gained (\\w+) XP! \\(\\+(\\d+)XP\\)");
        Pattern combatPattern = Pattern.compile("\\+(\\d{1,3}(,\\d{3})*)XP");

        Matcher normalMatcher = normalPattern.matcher(txt);
        Matcher combatMatcher = combatPattern.matcher(txt);
        int amount = 0;
        String category = "";

        if (normalMatcher.find()) {
            category = normalMatcher.group(1);
            amount = Integer.parseInt(normalMatcher.group(2));
            xpHistory.add(new ExpGain(category, amount));
            trimOldEntries(xpHistory);
            double currentRate = getExpPerHour(category, xpHistory);
            FaceExp.lastCategory = category;
            FaceExp.lastExpPerHour = currentRate;
        } else if (combatMatcher.find()) {
            String amountStr = combatMatcher.group(1).replace(",", "");
            amount = Integer.parseInt(amountStr);
            category = "Combat";
            xpHistory.add(new ExpGain(category, amount));
            trimOldEntries(xpHistory);
            double currentRate = getExpPerHour(category, xpHistory);
            FaceExp.lastCategory = category;
            FaceExp.lastExpPerHour = currentRate;
        }

        for (FaceSkill skill : FaceExp.skillCache) {
            if (skill.category.equalsIgnoreCase(category)) {
                skill.currentExp += amount;
                break;
            }
        }
    }

    @Inject(method = "onExperienceBarUpdate", at = @At("HEAD"), cancellable = true)
    private void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            int combatLevel = packet.getExperienceLevel();
            float progress = packet.getBarProgress();

            double requiredExp = getCombatLevelExp(combatLevel);

            double currentExpProgress = requiredExp * progress;

            FaceSkill combat = new FaceSkill("Combat", combatLevel, (int) currentExpProgress, (int) requiredExp);

            FaceExp.skillCache.add(combat);
        }
    }
}
