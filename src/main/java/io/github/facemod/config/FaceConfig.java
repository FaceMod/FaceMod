package io.github.facemod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Config(name = "FaceMod")
public class FaceConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Excluded
    public static ConfigHolder<FaceConfig> holder;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    public static class General {

        @ConfigEntry.Gui.Excluded
        public static boolean onFaceLand = false;

        @ConfigEntry.Gui.Excluded
        public static boolean isMounted = false;

        @ConfigEntry.Gui.Excluded
        public static boolean inCombat = false;

        @ConfigEntry.Gui.Excluded
        public static float hurtTime = 0;

        @ConfigEntry.Gui.Excluded
        public static long lastHurtTime = 0;

        @ConfigEntry.Gui.Excluded
        public int curseStacks = 0;

        @ConfigEntry.Gui.Tooltip
        public boolean mountThirdPerson = true;

        @ConfigEntry.Gui.Tooltip
        public boolean instantBowZoom = true;

        @ConfigEntry.Gui.Tooltip
        public int playerListHeightOffset = 25;

    }

    @ConfigEntry.Category("inventory")
    @ConfigEntry.Gui.TransitiveObject
    public Inventory inventory = new Inventory();

    public static class Inventory {
        @ConfigEntry.Gui.Tooltip
        public boolean customBank = false;

        @ConfigEntry.Gui.CollapsibleObject()
        public AutoTool autoTool = new AutoTool();

        public static class AutoTool {

            @ConfigEntry.Gui.Tooltip
            public boolean enabled = false;

            @ConfigEntry.Gui.Excluded
            public int PICKAXE = 15;
            @ConfigEntry.Gui.Excluded
            public int WOODCUTTINGAXE = 16;
            @ConfigEntry.Gui.Excluded
            public int HOE = 17;
        }

        @ConfigEntry.Gui.CollapsibleObject()
        public DropHighlight dropHighlight = new DropHighlight();
        public static class DropHighlight {

            @ConfigEntry.Gui.NoTooltip
            public boolean enabled = false;

            @ConfigEntry.Gui.CollapsibleObject()
            public selectGear selectGear = new selectGear();
        }

        public static class selectGear {
            @ConfigEntry.Gui.CollapsibleObject
            public GearType sword = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType staff = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType wand = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType pistol = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType warhammer = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType longbow = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType shortbow = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType battleaxe = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType dagger = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType mace = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType hoe = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType pickaxe = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType rod = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType spellbook = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType quiver = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType ammunition = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType ring = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType earring = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType necklace = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType helmet = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType coif = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType hat = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType platebody = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType robe = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType tunic = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType leggings = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType platelegs = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType skirt = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType boots = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType shoes = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType greaves = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType socketgem = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType enchantmentbook = new GearType();
            @ConfigEntry.Gui.CollapsibleObject
            public GearType potion = new GearType();

            public List<String> getSelectedGear() {
                List<String> selectedGear = new ArrayList<>();

                if (sword.enabled) selectedGear.add("sword");
                if (staff.enabled) selectedGear.add("staff");
                if (wand.enabled) selectedGear.add("wand");
                if (pistol.enabled) selectedGear.add("pistol");
                if (warhammer.enabled) selectedGear.add("warhammer");
                if (longbow.enabled) selectedGear.add("longbow");
                if (shortbow.enabled) selectedGear.add("shortbow");
                if (battleaxe.enabled) selectedGear.add("battleaxe");
                if (dagger.enabled) selectedGear.add("dagger");
                if (mace.enabled) selectedGear.add("mace");
                if (hoe.enabled) selectedGear.add("hoe");
                if (pickaxe.enabled) selectedGear.add("pickaxe");
                if (rod.enabled) selectedGear.add("rod");
                if (spellbook.enabled) selectedGear.add("spellbook");
                if (quiver.enabled) selectedGear.add("quiver");
                if (ammunition.enabled) selectedGear.add("ammunition");
                if (ring.enabled) selectedGear.add("ring");
                if (earring.enabled) selectedGear.add("earring");
                if (necklace.enabled) selectedGear.add("necklace");
                if (helmet.enabled) selectedGear.add("helmet");
                if (coif.enabled) selectedGear.add("coif");
                if (hat.enabled) selectedGear.add("hat");
                if (platebody.enabled) selectedGear.add("platebody");
                if (robe.enabled) selectedGear.add("robe");
                if (tunic.enabled) selectedGear.add("tunic");
                if (leggings.enabled) selectedGear.add("leggings");
                if (platelegs.enabled) selectedGear.add("platelegs");
                if (skirt.enabled) selectedGear.add("skirt");
                if (boots.enabled) selectedGear.add("boots");
                if (shoes.enabled) selectedGear.add("shoes");
                if (greaves.enabled) selectedGear.add("greaves");
                if (socketgem.enabled) selectedGear.add("socketgem");
                if (enchantmentbook.enabled) selectedGear.add("enchantmentbook");
                if (potion.enabled) selectedGear.add("potion");

                return selectedGear;
            }
        }

        public static class selectRarity {
            @ConfigEntry.Gui.NoTooltip
            public boolean common = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean uncommon = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean rare = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean epic = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean unique = false;

            public List<String> getSelectedRarities(String gearName) {
                List<String> selectedRarities = new ArrayList<>();
                try {
                    java.lang.reflect.Field field = this.getClass().getDeclaredField(gearName);
                    GearType gear = (GearType) field.get(this);
                    if (gear.enabled) {
                        if (gear.rarity.common) selectedRarities.add("common");
                        if (gear.rarity.uncommon) selectedRarities.add("uncommon");
                        if (gear.rarity.rare) selectedRarities.add("rare");
                        if (gear.rarity.epic) selectedRarities.add("epic");
                        if (gear.rarity.unique) selectedRarities.add("unique");
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("Invalid gear type: " + gearName);
                }
                return selectedRarities;
            }
        }

        public static class GearType {
            @ConfigEntry.Gui.NoTooltip
            public boolean enabled = false;

            @ConfigEntry.Gui.CollapsibleObject
            public selectRarity rarity = new selectRarity();

            @ConfigEntry.Gui.NoTooltip
            public List<String> filterTags = new ArrayList<>();
        }
    }

}
