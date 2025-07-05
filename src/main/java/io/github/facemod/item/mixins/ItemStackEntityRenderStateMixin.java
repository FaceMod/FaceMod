package io.github.facemod.item.mixins;

import io.github.facemod.item.ducks.ItemStackHolder;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackEntityRenderState.class)
public class ItemStackEntityRenderStateMixin implements ItemStackHolder {
    @Unique
    private ItemStack capturedStack;

    @Override
    public ItemStack facemod$getCapturedStack() {
        return capturedStack;
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void captureStack(Entity entity, ItemStack stack, ItemModelManager itemModelManager, CallbackInfo ci) {
        this.capturedStack = stack;
    }
}
