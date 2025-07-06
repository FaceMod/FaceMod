package io.github.facemod.exp.mixins;

import io.github.facemod.config.FaceConfig;
import io.github.facemod.exp.utils.FaceSkill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.facemod.exp.utils.FaceExp.skillCache;

@Mixin(HandledScreen.class)
public abstract class ExpHandledScreenMixin {
    @Unique
    private boolean init = true;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        HandledScreen<?> handledScreen = (HandledScreen<?>) (Object) this;
        Text screenTitle = handledScreen.getTitle();

        if (FaceConfig.General.onFaceLand) {
            if (init && screenTitle.getString().contains("朮")) {
                init = false;
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MinecraftClient.getInstance().execute(() -> {
                        cacheSkillsFromContainer(handledScreen.getScreenHandler());
                    });
                }).start();
            }
        }
    }

    @Unique
    private void cacheSkillsFromContainer(ScreenHandler handler) {
        for (Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            Text displayName = stack.getName();
            List<Text> lore = stack.getTooltip(Item.TooltipContext.DEFAULT,
                    MinecraftClient.getInstance().player,
                    MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC
            );

            String name = displayName.getString(); // "<Category> Skill [<Current>/99]"
            System.out.println(displayName.getString());
            Matcher nameMatch = Pattern.compile("^(\\w+) (?:\\w+ )?\\[(\\d+)/\\d+]").matcher(name);
            if (!nameMatch.find()) continue;

            String skill = nameMatch.group(1);
            int level = Integer.parseInt(nameMatch.group(2));

            if (lore.isEmpty()) continue;

            String xpLine = lore.get(1).getString(); // "XP: 1234/4500"
            Matcher xpMatch = Pattern.compile("XP:\\s*([\\d,]+)\\s*/\\s*([\\d,]+)").matcher(xpLine);
            if (!xpMatch.find()) continue;

            String xpStr = xpMatch.group(1).replace(",", "");
            String maxStr = xpMatch.group(2).replace(",", "");

            int xp = Integer.parseInt(xpStr);
            int max = Integer.parseInt(maxStr);

            System.out.println("Skill: " + skill + " , Level: " + level + " Exp: " + xp + "/" + max);
            skillCache.add(new FaceSkill(skill, level, xp, max));
        }

        init = false;
        MinecraftClient.getInstance().player.closeHandledScreen();
    }
}

