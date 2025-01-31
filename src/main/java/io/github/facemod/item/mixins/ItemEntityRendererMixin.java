package io.github.facemod.item.mixins;

import io.github.facemod.FaceModInitializer;
import io.github.facemod.config.FaceConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static io.github.facemod.item.util.Unicode.capitalizeWords;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @Unique
    private static final Set<String> seenItems = new HashSet<>();
    @Unique
    String rarity = "";
    @Unique
    String itemtype = "";
    @Unique
    Boolean skipItem = false;

    @Inject(method = "render*", at = @At("HEAD"))
    public void onRender(ItemEntityRenderState renderState, MatrixStack matrixStack,
                         VertexConsumerProvider vertexConsumerProvider, int light,
                         CallbackInfo ci) {

        if(FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.enabled) {

            ItemStack itemStack = renderState.stack;

            ComponentMap map = itemStack.getComponents();
            LoreComponent lore = itemStack.get(DataComponentTypes.LORE);

            if (lore == null) {
                return;
            }

            ArrayList<String> loreList = new ArrayList<>();
            lore.lines().forEach(l -> loreList.add(l.getString()));

            if (loreList.isEmpty()) {
                return;
            }

            int categorieIndex = -1;
            for (int i = 0; i < loreList.size(); i++) {
                String l = loreList.get(i);

                if (loreList.get(i).contains(FaceModInitializer.INSTANCE.unicode.potionUnicode)){
                    itemtype = "potion";
                    skipItem = true;
                    break;
                }

                if (loreList.get(i).contains(FaceModInitializer.INSTANCE.unicode.bookUnicode)){
                    itemtype = "enchantmentbook";
                    skipItem = true;
                    break;
                }

                if (loreList.get(i).contains(FaceModInitializer.INSTANCE.unicode.gemUnicode)){
                    itemtype = "socketgem";
                    skipItem = true;
                    break;
                }

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

            if (categorieIndex == -1 && itemtype.isEmpty()) {
                System.out.println("No categories found for " + itemStack.getItem().getTranslationKey());
                return;
            }

            if (!skipItem) {
                String cleanedCategory = FaceModInitializer.INSTANCE.unicode.decode(loreList.get(categorieIndex))
                        .replaceAll("[^A-Z' ]", "");
                int splitIndex = cleanedCategory.indexOf("'");

                if (splitIndex == -1) {
                    System.out.println("Invalid category format: " + cleanedCategory);
                    return;
                }

                rarity = cleanedCategory.substring(0, cleanedCategory.indexOf("'")).trim().toLowerCase();
                itemtype = cleanedCategory.substring(cleanedCategory.indexOf("'") + 1).trim().toLowerCase();
            }

            var gearType = getGearType(itemtype);

            System.out.println("ItemEntityRenderer Rarity: " + rarity);
            System.out.println("ItemEntityRenderer Itemtype: " + itemtype);

            if (gearType == null || !gearType.enabled) {
                if (gearType == null) {
                    System.out.println("GearType Null");
                } else {
                    System.out.println("GearType Disabled: " + gearType.enabled);
                }
                return;
            }

            if (!isRaritySelected(gearType, rarity) && !isEmpty(gearType)) {
                System.out.println("Rarity Disabled: " + rarity);
                return;
            }

            if (!matchesAllTags(gearType.filterTags, loreList) && !(gearType.filterTags.isEmpty())) {
                System.out.println("GearType Filter: " + gearType.filterTags);
                return;
            }

            if (FaceModInitializer.INSTANCE.CLIENT.player == null) {
                System.out.println("Client Null");
                return;
            }

            Vec3d itemPos = new Vec3d(renderState.x, renderState.y, renderState.z);
            String name = map.get(DataComponentTypes.CUSTOM_NAME).getString().toLowerCase();

            if (!seenItems.contains(name)) {
                System.out.println("ItemEntityRenderer itemKey: " + itemStack);
                FaceModInitializer.INSTANCE.CLIENT.player.sendMessage(Text.of("[FaceMod] >> " + capitalizeWords(rarity) + " " + capitalizeWords(itemtype)
                        + " with " + gearType.filterTags.toString() + " dropped!"), false);
                seenItems.add(name);
            }

            ClientWorld world = MinecraftClient.getInstance().world;

            if (world == null){
                return;
            }

            for (int i = 0; i < 20; i++) {
                world.addParticle(ParticleTypes.END_ROD,
                        itemPos.x,
                        itemPos.y + 2 + i * 0.5,
                        itemPos.z,
                        0, 0.05, 0);
            }

        }
    }

    @Unique
    private FaceConfig.Inventory.GearType getGearType(String itemType) {
        var gearConfig = FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.selectGear;

        try {
            java.lang.reflect.Field field = gearConfig.getClass().getDeclaredField(itemType.toLowerCase());
            return (FaceConfig.Inventory.GearType) field.get(gearConfig);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Unique
    private boolean isRaritySelected(FaceConfig.Inventory.GearType gearType, String rarity) {
        return switch (rarity) {
            case "common" -> gearType.rarity.common;
            case "uncommon" -> gearType.rarity.uncommon;
            case "rare" -> gearType.rarity.rare;
            case "epic" -> gearType.rarity.epic;
            case "unique" -> gearType.rarity.unique;
            default -> false;
        };
    }

    @Unique
    private boolean isEmpty(FaceConfig.Inventory.GearType gearType){
        return !gearType.rarity.common && !gearType.rarity.uncommon && !gearType.rarity.rare && !gearType.rarity.epic && !gearType.rarity.unique;
    }

    @Unique
    private boolean matchesAllTags(List<String> filterTags, List<String> loreList) {
        return filterTags.stream().allMatch(tag -> loreList.stream().anyMatch(lore -> lore.contains(tag)));
    }
}
