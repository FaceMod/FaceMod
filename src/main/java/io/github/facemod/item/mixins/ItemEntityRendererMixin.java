package io.github.facemod.item.mixins;

import io.github.facemod.FaceModInitializer;
import io.github.facemod.config.FaceConfig;
import io.github.facemod.item.ducks.ItemStackHolder;
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
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
            skipItem = false;
            rarity = "";
            itemtype = "";

            ItemStack itemStack = ((ItemStackHolder) renderState).facemod$getCapturedStack();

            ComponentMap map = itemStack.getComponents();
            LoreComponent lore = itemStack.get(DataComponentTypes.LORE);

            if (lore == null) {
                return;
            }

            if (map == null) {
                return;
            }

            ArrayList<String> loreList = new ArrayList<>();
            lore.lines().forEach(l -> loreList.add(l.getString()));

            if (loreList.isEmpty()) {
                return;
            }

            Text customNameText = map.get(DataComponentTypes.CUSTOM_NAME);
            if (customNameText == null) {
                return;
            }

            String name = customNameText.getString().toLowerCase();

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

                if (decodedLine.toLowerCase().contains(name)) {
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

            if (itemtype == null) {
                return;
            }

            if (categorieIndex == -1 && itemtype.isEmpty()) {
                //System.out.println("No categories found for " + itemStack.getItem().getTranslationKey());
                return;
            }

            if (!skipItem) {
                String cleanedCategory = FaceModInitializer.INSTANCE.unicode.decode(loreList.get(categorieIndex))
                        .replaceAll("[^A-Z' ]", "");
                int splitIndex = cleanedCategory.indexOf("'");

                if (splitIndex == -1) {
                    //System.out.println("Invalid category format: " + cleanedCategory);
                    return;
                }

                rarity = cleanedCategory.substring(0, cleanedCategory.indexOf("'")).trim().toLowerCase();
                itemtype = cleanedCategory.substring(cleanedCategory.indexOf("'") + 1).replaceAll("'", "").trim().toLowerCase();
            }

            var gearType = getGearType(itemtype);

            if (gearType == null) {
//                    System.out.println("GearType Null");
                return;
            }

            if (!FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.enabled && !gearType.enabled) {
                //                    System.out.println("GearType Disabled: " + false);
                return;
            }

            if(!skipItem) {
                if (FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.enabled && !isGeneralRarity(rarity) && !isEmpty()) {
                    if (!isRaritySelected(gearType, rarity) && !isEmpty(gearType)) {
                        //System.out.println("Rarity Disabled: " + rarity);
                        return;
                    }
                    if (!gearType.enabled) {
                        return;
                    }
                }
            }

            if (FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.enabled && !matchesAllTags(FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.filterTags, loreList) && !(FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.filterTags.isEmpty())) {
                if (!matchesAllTags(gearType.filterTags, loreList) && !(gearType.filterTags.isEmpty())) { //TODO: Implement Conditionals, Implement Check for it ifs a main stat or not based off color.
                    //System.out.println("GearType Filter: " + gearType.filterTags); //TODO: Elements of same type should be combined, ex 50 Fire Damage MS and 10 Fire Damage SS should be considered 60 Fire Damage when doing conditionals.
                    return; //TODO: Implement check for total substats.
                }
                if (!gearType.enabled) {
                    return;
                }
            }

            if (FaceModInitializer.INSTANCE.CLIENT.player == null) {
                //System.out.println("Client Null");
                return;
            }

            Vec3d itemPos = new Vec3d(renderState.x, renderState.y, renderState.z);

            if (!seenItems.contains(name)) {
                //System.out.println("ItemEntityRenderer itemKey: " + itemStack);
                FaceModInitializer.INSTANCE.CLIENT.player.sendMessage(
                        Text.literal("財 ")
                                .append(Text.literal(capitalizeWords(rarity) + " " + capitalizeWords(itemtype))
                                        .styled(style -> style.withColor(getColor(rarity))))
                                .append(Text.literal(" with ")
                                        .styled(style -> style.withColor(Formatting.GRAY)))
                                .append(Text.literal(gearType.filterTags.toString())
                                        .styled(style -> style.withColor(Formatting.GREEN)))
                                .append(Text.literal(" dropped \n→ ")
                                        .styled(style -> style.withColor(Formatting.GRAY)))
                                .append(Text.literal("[")
                                        .append(Text.literal(itemStack.getName().getString())
                                                .styled(style -> style
                                                        .withColor(getColor(rarity))
                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                                                new HoverEvent.ItemStackContent(itemStack)))))
                                        .append(Text.literal("]"))),
                        false
                );
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
    private Formatting getColor(String rarity) {
        return switch (rarity) {
            case "common" -> Formatting.GRAY;
            case "uncommon" -> Formatting.BLUE;
            case "rare" -> Formatting.DARK_PURPLE;
            case "epic" -> Formatting.RED;
            case "unique" -> Formatting.GOLD;
            default -> Formatting.WHITE;
        };
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
    private boolean isGeneralRarity(String rarity) {
        return switch (rarity) {
            case "common" -> FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.rarity.common;
            case "uncommon" -> FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.rarity.uncommon;
            case "rare" -> FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.rarity.rare;
            case "epic" -> FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.rarity.epic;
            case "unique" -> FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general.rarity.unique;
            default -> false;
        };

    }

    @Unique
    private boolean isEmpty(FaceConfig.Inventory.GearType gearType){
        return !gearType.rarity.common && !gearType.rarity.uncommon && !gearType.rarity.rare && !gearType.rarity.epic && !gearType.rarity.unique;
    }

    @Unique
    private boolean isEmpty(){
        FaceConfig.Inventory.GearType gearType = FaceModInitializer.INSTANCE.CONFIG.inventory.dropHighlight.general;
        return !gearType.rarity.common && !gearType.rarity.uncommon && !gearType.rarity.rare && !gearType.rarity.epic && !gearType.rarity.unique;
    }


    @Unique
    private boolean matchesAllTags(List<String> filterTags, List<String> loreList) {
        return filterTags.stream().allMatch(tag -> loreList.stream().anyMatch(lore -> lore.contains(tag)));
    }

    @Unique
    private static boolean hasConditionalOperator(String tag) {
        return tag.matches(".*(>=|<=|==|>|<)\\s*-?\\d+\\s+.+");
    }
}
