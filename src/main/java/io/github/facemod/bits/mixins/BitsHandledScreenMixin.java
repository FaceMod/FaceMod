package io.github.facemod.bits.mixins;

import io.github.facemod.FaceModInitializer;
import io.github.facemod.bank.screens.BankScreen;
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
public abstract class BitsHandledScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        HandledScreen<?> handledScreen = (HandledScreen<?>) (Object) this;
        Text screenTitle = handledScreen.getTitle();

        //TODO: make bits.
        if(FaceModInitializer.INSTANCE.CONFIG.inventory.customBank) {
            if (screenTitle.getString().contains("Fish")) {

            }
        }
    }
}

