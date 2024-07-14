package facemod.mixins;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        HandledScreen<?> handledScreen = (HandledScreen<?>) (Object) this;
        Text screenTitle = handledScreen.getTitle();

        System.out.println("Container Name: " + screenTitle.getString());
        //TODO: Replace this hard coded unicode character with class.
        if (screenTitle.getString().contains("拴")) {
            System.out.println("Bank Opened");
        }
    }
}
