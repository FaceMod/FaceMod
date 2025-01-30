package io.github.facemod.item.mixins;

import io.github.facemod.FaceModInitializer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

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

        if(FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.enabled) {

            ItemStack itemStack = renderState.stack;

            ComponentMap map = itemStack.getComponents();
            NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            LoreComponent lore = itemStack.get(DataComponentTypes.LORE);

            if (lore == null) {
                return;
            }

            System.out.println("ItemEntityRenderer Item: " + Objects.requireNonNull(map.get(DataComponentTypes.CUSTOM_NAME)).getString().toLowerCase());

            ArrayList<String> loreList = new ArrayList<>();
            lore.lines().forEach(l -> loreList.add(l.getString()));

            System.out.println("ItemEntityRenderer Lore: " + loreList);

            int categorieIndex = -1;
            for (int i = 0; i < loreList.size(); i++) {
                String l = loreList.get(i);
                String decodedLine = FaceModInitializer.INSTANCE.unicode.decode(l);
                String customName = Objects.requireNonNull(map.get(DataComponentTypes.CUSTOM_NAME)).getString().toLowerCase();

                if (decodedLine.toLowerCase().contains(customName)) {
                    continue;
                }

                for (char f : l.toCharArray()) {
                    if (FaceModInitializer.INSTANCE.unicode.SMALL_UNICODE_SET.contains(f) ||
                            FaceModInitializer.INSTANCE.unicode.BIG_UNICODE_SET.contains(f)) {

                        categorieIndex = i;
                        break;
                    }
                }
            }

            if (categorieIndex == -1) {
                return;
            }

            String category = FaceModInitializer.INSTANCE.unicode.decode(loreList.get(categorieIndex));

            System.out.println("ItemEntityRenderer Category: " + category);

            String cleanedCategory = FaceModInitializer.INSTANCE.unicode.decode(loreList.get(categorieIndex))
                    .replaceAll("[^A-Z' ]", "");

            System.out.println("ItemEntityRenderer Category (Cleaned): " + cleanedCategory);
        }
    }
}
