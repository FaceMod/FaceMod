package io.github.facemod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

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
    }

}
