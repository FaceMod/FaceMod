package io.github.facemod.item.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    private static final Map<String, String> ITEM_CATEGORIES = new HashMap<>();

    static {
        ITEM_CATEGORIES.put("Sword", "sword");
        ITEM_CATEGORIES.put("Mace", "mace");
        ITEM_CATEGORIES.put("Staff", "staff");
        ITEM_CATEGORIES.put("Dagger", "dagger");
        ITEM_CATEGORIES.put("Wand", "wand");
        ITEM_CATEGORIES.put("Pistol", "pistol");
        ITEM_CATEGORIES.put("Warhammer", "warhammer");
        ITEM_CATEGORIES.put("Longbow", "longbow");
        ITEM_CATEGORIES.put("Shortbow", "shortbow");
        ITEM_CATEGORIES.put("Battleaxe", "battleaxe");

    }

    @Inject(method = "render*", at = @At("HEAD"))
    public void onRender(ItemEntityRenderState renderState, MatrixStack matrixStack,
                         VertexConsumerProvider vertexConsumerProvider, int light,
                         CallbackInfo ci) {
        ItemStack itemStack = renderState.stack;

        ComponentMap map = itemStack.getComponents();
        NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);

        System.out.println("ItemEntityRenderer Item: " + itemStack);
        System.out.println("ItemEntityRenderer NBT: " + nbtComponent);
        System.out.println("ItemEntityRenderer Components:");

        map.forEach((component) -> {
            System.out.println( " -> " + component);
        });

    }
}
