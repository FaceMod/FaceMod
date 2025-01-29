package io.github.facemod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

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
            public selectRarity selectRarity = new selectRarity();

            @ConfigEntry.Gui.Tooltip
            public String filterTags = "";

        }

        public static class selectGear {
            @ConfigEntry.Gui.NoTooltip
            public boolean sword = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean staff = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean wand = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean pistol = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean warhammer = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean longbow = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean shortbow = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean battleaxe = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean dagger = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean mace = false;

            @ConfigEntry.Gui.NoTooltip
            public boolean hoe = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean pickaxe = false;

            @ConfigEntry.Gui.NoTooltip
            public boolean spellbook = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean quiver = false;

            @ConfigEntry.Gui.NoTooltip
            public boolean ring = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean earring = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean necklace = false;

            @ConfigEntry.Gui.NoTooltip
            public boolean helmet = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean chestplate = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean leggings = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean boots = false;

            @ConfigEntry.Gui.NoTooltip
            public boolean socketgem = false;
            @ConfigEntry.Gui.NoTooltip
            public boolean enchantmentbook = false;

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

        }
    }

}
