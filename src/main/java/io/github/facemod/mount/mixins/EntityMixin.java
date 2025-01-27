package io.github.facemod.mount.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"))
    private void startRiding(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("startRiding");
    }

    @Inject(method = "stopRiding",  at = @At("HEAD"))
    private void stopRiding(CallbackInfo ci){
        System.out.println("stopRiding");
    }
}
