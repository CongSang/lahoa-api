package com.lahoa.lahoa_be.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Chuyển sang chữ thường
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");

        // Xử lý riêng chữ đ/Đ vì Normalizer không xử lý được
        String normalized = nowhitespace.replace("đ", "d").replace("Đ", "D");

        // Loại bỏ dấu tiếng Việt (Normalizer)
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Loại bỏ các ký tự không phải chữ cái/số và thay khoảng trắng bằng dấu gạch ngang
        String slug = NON_LATIN.matcher(normalized).replaceAll("");

        // Loại bỏ gạch ngang thừa ở đầu/cuối và các gạch ngang kép
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("-{2,}", "-")
                .replaceAll("^-", "")
                .replaceAll("-$", "");
    }
}