package io.github.facemod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.HashMap;
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
            public selectRarity rarity = new selectRarity();

            @ConfigEntry.Gui.CollapsibleObject()
            public selectGear selectGear = new selectGear();

            @ConfigEntry.Gui.Tooltip
            public String filterTags = "";

        }

        public static class selectGear {
            @ConfigEntry.Gui.NoTooltip public boolean sword = false;
            @ConfigEntry.Gui.NoTooltip public boolean staff = false;
            @ConfigEntry.Gui.NoTooltip public boolean wand = false;
            @ConfigEntry.Gui.NoTooltip public boolean pistol = false;
            @ConfigEntry.Gui.NoTooltip public boolean warhammer = false;
            @ConfigEntry.Gui.NoTooltip public boolean longbow = false;
            @ConfigEntry.Gui.NoTooltip public boolean shortbow = false;
            @ConfigEntry.Gui.NoTooltip public boolean battleaxe = false;
            @ConfigEntry.Gui.NoTooltip public boolean dagger = false;
            @ConfigEntry.Gui.NoTooltip public boolean mace = false;

            @ConfigEntry.Gui.NoTooltip public boolean hoe = false;
            @ConfigEntry.Gui.NoTooltip public boolean pickaxe = false;
            @ConfigEntry.Gui.NoTooltip public boolean rod = false;
            
            @ConfigEntry.Gui.NoTooltip public boolean spellbook = false;
            @ConfigEntry.Gui.NoTooltip public boolean quiver = false;
            @ConfigEntry.Gui.NoTooltip public boolean ammunition = false;

            @ConfigEntry.Gui.NoTooltip public boolean ring = false;
            @ConfigEntry.Gui.NoTooltip public boolean earring = false;
            @ConfigEntry.Gui.NoTooltip public boolean necklace = false;

            @ConfigEntry.Gui.NoTooltip public boolean helmet = false;
            @ConfigEntry.Gui.NoTooltip public boolean coif = false;
            @ConfigEntry.Gui.NoTooltip public boolean hat = false;

            @ConfigEntry.Gui.NoTooltip public boolean platebody = false;
            @ConfigEntry.Gui.NoTooltip public boolean robe = false;
            @ConfigEntry.Gui.NoTooltip public boolean tunic = false;

            @ConfigEntry.Gui.NoTooltip public boolean leggings = false;
            @ConfigEntry.Gui.NoTooltip public boolean platelegs = false;
            @ConfigEntry.Gui.NoTooltip public boolean skirt = false;


            @ConfigEntry.Gui.NoTooltip public boolean boots = false;
            @ConfigEntry.Gui.NoTooltip public boolean shoes = false;
            @ConfigEntry.Gui.NoTooltip public boolean greaves = false;


            @ConfigEntry.Gui.NoTooltip public boolean socketgem = false;
            @ConfigEntry.Gui.NoTooltip public boolean enchantmentbook = false;
            @ConfigEntry.Gui.NoTooltip public boolean potion = false;

            public List<String> getSelectedGear() {
                List<String> selectedGear = new ArrayList<>();

                if (sword) selectedGear.add("sword");
                if (staff) selectedGear.add("staff");
                if (wand) selectedGear.add("wand");
                if (pistol) selectedGear.add("pistol");
                if (warhammer) selectedGear.add("warhammer");
                if (longbow) selectedGear.add("longbow");
                if (shortbow) selectedGear.add("shortbow");
                if (battleaxe) selectedGear.add("battleaxe");
                if (dagger) selectedGear.add("dagger");
                if (mace) selectedGear.add("mace");

                if (hoe) selectedGear.add("hoe");
                if (pickaxe) selectedGear.add("pickaxe");

                if (spellbook) selectedGear.add("spellbook");
                if (quiver) selectedGear.add("quiver");

                if (ring) selectedGear.add("ring");
                if (earring) selectedGear.add("earring");
                if (necklace) selectedGear.add("necklace");

                if (helmet) selectedGear.add("helmet");
                if (chestplate) selectedGear.add("chestplate");
                if (leggings) selectedGear.add("leggings");
                if (boots) selectedGear.add("boots");

                if (socketgem) selectedGear.add("socketgem");
                if (enchantmentbook) selectedGear.add("enchantmentbook");
                if (potion) selectedGear.add("potion");

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

            public List<String> getSelectedRarities() {
                List<String> selectedRarities = new ArrayList<>();

                if (common) selectedRarities.add("common");
                if (uncommon) selectedRarities.add("uncommon");
                if (rare) selectedRarities.add("rare");
                if (epic) selectedRarities.add("epic");
                if (unique) selectedRarities.add("unique");

                return selectedRarities;
            }
        }


    }

}
