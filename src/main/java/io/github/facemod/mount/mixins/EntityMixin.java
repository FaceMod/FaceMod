package io.github.facemod.mount.mixins;

import io.github.facemod.FaceModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
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
        FaceModInitializer.CLIENT.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Inject(method = "stopRiding",  at = @At("HEAD"))
    private void stopRiding(CallbackInfo ci){
        FaceModInitializer.CLIENT.options.setPerspective(Perspective.FIRST_PERSON);
    }
}
