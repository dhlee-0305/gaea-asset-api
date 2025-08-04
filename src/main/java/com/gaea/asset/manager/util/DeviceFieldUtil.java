package com.gaea.asset.manager.util;

public class DeviceFieldUtil {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public static void appendIfPresent(StringBuilder sb, String label, Object value) {
        if (value != null && !value.toString().isBlank()) {
            if (!sb.isEmpty()) sb.append(" || ");
            sb.append(label).append(": ").append(value);
        }
    }
}
