package io.github.facemod.item.util;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;

import java.util.*;

public class Unicode {
    private static final Map<Character, String> SMALL_UNICODE_MAPPING = new HashMap<>();
    private static final Map<Character, String> BIG_UNICODE_MAPPING = new HashMap<>();

    public String mappedLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ'";
    String smallUnicode = "乀乁乂乃乄久乆乇么义乊之乌乍乎乏乐乑乒乓乔乕乖乗乘乙乚"; // Ascent: 14
    String bigUnicode = "俿倀倁倂倃倄倅倆倇倈倉倊個倌倍倎倏倖倗倘候倚倛倜倝倞借"; // Ascent: 62
    public  final Set<Character> SMALL_UNICODE_SET = new HashSet<>();
    public  final Set<Character> BIG_UNICODE_SET = new HashSet<>();

    public void init() {
        if (smallUnicode.length() == mappedLetters.length()) {
            for (int i = 0; i < mappedLetters.length(); i++) {
                char unicodeChar = smallUnicode.charAt(i);
                SMALL_UNICODE_MAPPING.put(smallUnicode.charAt(i), String.valueOf(mappedLetters.charAt(i)));
                SMALL_UNICODE_SET.add(unicodeChar);
            }
        }

        if (bigUnicode.length() == mappedLetters.length()) {
            for (int i = 0; i < mappedLetters.length(); i++) {
                char unicodeChar = smallUnicode.charAt(i);
                BIG_UNICODE_MAPPING.put(bigUnicode.charAt(i), String.valueOf(mappedLetters.charAt(i)));
                BIG_UNICODE_SET.add(unicodeChar);
            }
        }
    }

    public String decode (String unicode){
        StringBuilder decoded = new StringBuilder();
        String unicodeType = "null";
        for (char c : unicode.toCharArray()) {
            if (SMALL_UNICODE_SET.contains(c)) {
                unicodeType = "small";
            } else if (BIG_UNICODE_SET.contains(c)) {
                unicodeType = "big";
            }
        }
        Map<Character, String> mapping = unicodeType.equals("big") ? BIG_UNICODE_MAPPING : SMALL_UNICODE_MAPPING;

        for (char c : unicode.toCharArray()) {
            decoded.append(mapping.getOrDefault(c, String.valueOf(c)));
        }

        return decoded.toString();
    }
}




