package io.github.facemod.item.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Item.class)
public abstract class ItemMixin {

    /**
     * Override the hasGlint method to apply custom glint logic.
     * @return true to apply the glint effect, false otherwise
     * @author IAmSpade
     * @reason Faceguy Asked me.
     */

    /*@Overwrite
    public boolean hasGlint(ItemStack stack) {

        return true;
    }*/ // <- Commented out until faceguy follows up with his request.
}