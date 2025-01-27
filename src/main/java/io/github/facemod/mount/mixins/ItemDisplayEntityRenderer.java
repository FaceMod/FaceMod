package io.github.facemod.mount.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayEntityRenderer.ItemDisplayEntityRenderer.class)
public class ItemDisplayEntityRenderer {
    /*@Inject(method = "render(Lnet/minecraft/entity/decoration/DisplayEntity$ItemDisplayEntity;Lnet/minecraft/entity/decoration/DisplayEntity$ItemDisplayEntity$Data;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V", at = @At("HEAD"), cancellable = true)
    public void onRender(DisplayEntity.ItemDisplayEntity itemDisplayEntity, DisplayEntity.ItemDisplayEntity.Data data, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo callbackInfo) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerEntity playerEntity = minecraftClient.player;
        if (playerEntity == null) {
            return;
        }
        if (!playerEntity.hasVehicle()) {
            return;
        }
        if (!minecraftClient.options.getPerspective().isFirstPerson()) {
            return;
        }
        if (itemDisplayEntity.distanceTo(playerEntity) > 2.0F) {
            return;
        }
        callbackInfo.cancel();
    }*/
}