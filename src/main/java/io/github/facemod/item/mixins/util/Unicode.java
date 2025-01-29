package io.github.facemod.item.mixins.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Unicode {
    private static final Map<Character, String> SMALL_UNICODE_MAPPING = new HashMap<>();
    private static final Map<Character, String> BIG_UNICODE_MAPPING = new HashMap<>();

    String mappedLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ'";
    String smallUnicode = "俐俑俒俓俔俕俖俗俘俙俚俛俜保俞俟俠信俢俣俤俥俦俧俨俩俏"; // ascent: 19
    String bigUnicode = "加务劢劣劤劥劦劧动助努劫劬劭劮劯劰励劲劳労劵劶劷劸効婱"; // ascent: 28
    private static final Set<Character> SMALL_UNICODE_SET = new HashSet<>();
    private static final Set<Character> BIG_UNICODE_SET = new HashSet<>();

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

    public String decode(String unicode) {
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




