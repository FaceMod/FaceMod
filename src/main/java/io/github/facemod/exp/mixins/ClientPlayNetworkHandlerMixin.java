package io.github.facemod.exp.mixins;

import io.github.facemod.exp.utils.ExpGain;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.facemod.exp.utils.ExpGain.getExpPerHour;
import static io.github.facemod.exp.utils.ExpGain.trimOldEntries;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Unique
    private static final List<ExpGain> xpHistory = new ArrayList<>();

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!Thread.currentThread().getName().equals("Render thread")) {
            return;
        }
        String txt = packet.content().getString();
        Pattern pattern = Pattern.compile("Gained (\\w+) XP! \\(\\+(\\d+)XP\\)");
        Matcher matcher = pattern.matcher(txt);

        if (!matcher.find()) return;

        String category = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));

        xpHistory.add(new ExpGain(category, amount));

        trimOldEntries(xpHistory);
        double currentRate = getExpPerHour(category, xpHistory);
        System.out.printf("%s EXP/hr: %.2f%n", category, currentRate);
    }
}
