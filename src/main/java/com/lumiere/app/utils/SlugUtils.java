package com.lumiere.app.utils;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {
  public static String toSlug(String input) {
    if (input == null || input.isBlank()) return "";
    String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "");                 // bỏ dấu
    return noAccent.toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9\\s-]", "")           // bỏ ký tự lạ
        .replaceAll("\\s+", "-")                   // space -> -
        .replaceAll("-{2,}", "-")                  // gộp ---
        .replaceAll("^-|-$", "");                  // trim -
  }
}
