package io.github.facemod.exp.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        String txt = packet.body().content();
        Pattern pattern = Pattern.compile("Gained (\\w+) XP! \\(\\+(\\d+)XP\\)");

        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            String category = matcher.group(1);
            int amount = Integer.parseInt(matcher.group(2));
            System.out.println("Category: " + category + ", Amount: " + amount);
        }
    }
}
