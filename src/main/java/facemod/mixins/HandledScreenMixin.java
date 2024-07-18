package facemod.mixins;

import facemod.screens.BankScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Unique
    private boolean hideOriginalGui = false;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        HandledScreen<?> handledScreen = (HandledScreen<?>) (Object) this;
        Text screenTitle = handledScreen.getTitle();

        //System.out.println("Container Name: " + screenTitle.getString());
        //TODO: Replace this hard coded unicode character with class.
        if (screenTitle.getString().contains("拴") || screenTitle.getString().contains("拽") || screenTitle.getString().contains("抭")) {

            hideOriginalGui = true;

            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            minecraftClient.setScreen(new BankScreen(handledScreen.getScreenHandler(), Objects.requireNonNull(MinecraftClient.getInstance().player).getInventory(),screenTitle));

        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (hideOriginalGui) {
            ci.cancel();
        }
    }
}
