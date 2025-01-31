package io.github.facemod.keybinds.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FaceBinds {

    public static KeyBinding MOUNT = add("facemod.key.mount", GLFW.GLFW_KEY_V);
    public static KeyBinding ESCAPE = add("facemod.key.escape", GLFW.GLFW_KEY_0);
    public static KeyBinding SPELL_ONE = add("facemod.key.spellOne", GLFW.GLFW_KEY_1);
    public static KeyBinding SPELL_TWO = add("facemod.key.spellTwo", GLFW.GLFW_KEY_2);
    public static KeyBinding SPELL_THREE = add("facemod.key.spellThree", GLFW.GLFW_KEY_3);
    public static KeyBinding SPELL_FOUR = add("facemod.key.spellFour", GLFW.GLFW_KEY_4);
    public static KeyBinding POTION_HEALING = add("facemod.key.potionHeal", GLFW.GLFW_KEY_R);
    public static KeyBinding POTION_ENERGY = add("facemod.key.potionEnergy", GLFW.GLFW_KEY_G);

    public static KeyBinding add(String key, int defaultKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(key, InputUtil.Type.KEYSYM, defaultKey, "facemod.title"));
    }
}
