package io.github.facemod.exp.mixins;

import io.github.facemod.exp.utils.ExpGain;
import io.github.facemod.exp.utils.FaceExp;
import io.github.facemod.exp.utils.FaceSkill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.facemod.exp.utils.ExpGain.getExpPerHour;
import static io.github.facemod.exp.utils.ExpGain.trimOldEntries;
import static io.github.facemod.exp.utils.FaceExp.*;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!Thread.currentThread().getName().equals("Render thread")) {
            return;
        }

        String txt = packet.content().getString();

        Pattern normalPattern = Pattern.compile("Gained (\\w+) XP! \\(\\+(\\d+)XP\\)");
        Pattern combatPattern = Pattern.compile("\\+(\\d{1,3}(,\\d{3})*)XP");
        Pattern levelPattern = Pattern.compile("Skill Up! Your skill level in (\\w+) has increased to (\\d+)!");

        Matcher normalMatcher = normalPattern.matcher(txt);
        Matcher combatMatcher = combatPattern.matcher(txt);
        Matcher levelMatcher = levelPattern.matcher(txt);
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
        } else if (levelMatcher.find()) {
            category = levelMatcher.group(1);
            int newLevel = Integer.parseInt(levelMatcher.group(2));

            for (FaceSkill skill : FaceExp.skillCache) {
                if (skill.category.equalsIgnoreCase(category)) {
                    skill.currentLevel = newLevel;
                    skill.currentExp = 0;
                    if (category.equals("Combat")) {
                        skill.maxExp = (int) getCombatLevelExp(newLevel);
                    } else {
                        skill.maxExp = (int) getProfessionExp(newLevel); //TODO: Determine whether its a combat profession or not because it will have a +-10 difference.
                    }
                    break;
                }
            }


            return;
        }

        for (FaceSkill skill : FaceExp.skillCache) {
            if (skill.category.equalsIgnoreCase(category)) {
                //System.out.println(skill.category + " : " + skill.currentExp + " + " + amount + " = ");
                skill.currentExp += amount;
                //System.out.print(skill.currentExp + " / " + skill.maxExp + "\n");
                break;
            }
        }
    }

    @Inject(method = "onExperienceBarUpdate", at = @At("HEAD"))
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

    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void onTotemPop(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (packet.getStatus() == 35) {
            Entity entity = packet.getEntity(MinecraftClient.getInstance().world);
            if (entity != null && entity == MinecraftClient.getInstance().player) {

                FaceSkill sk = null;

                for(FaceSkill skill : FaceExp.skillCache){
                    if(Objects.equals(skill.category, "Combat")){
                        sk = skill;
                        break;
                    }
                }


                sk.currentLevel+=1;
                sk.maxExp = (int) getCombatLevelExp(sk.currentLevel);
                sk.currentExp = (int) (sk.maxExp * MinecraftClient.getInstance().player.experienceProgress);
            }
        }
    }
}
