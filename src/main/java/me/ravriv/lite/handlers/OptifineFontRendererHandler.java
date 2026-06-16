package me.ravriv.lite.handlers;

import me.ravriv.lite.mixins.accessors.ConfigAccessor;
import me.ravriv.lite.mixins.accessors.CustomColorsAccessor;
import me.ravriv.lite.utils.ClassTransformer;

public class OptifineFontRendererHandler {
    private static boolean caughtError = false;
    public static int getTextColor(int index, int originalColor) {
        if (caughtError) return originalColor;
        try {
            if (ClassTransformer.optifineVersion.equals("NONE")) {
                return originalColor;
            }
            if (ConfigAccessor.invokeIsCustomColors()) {
                return CustomColorsAccessor.invokeGetTextColor(index, originalColor);
            }
            return originalColor;
        } catch (Throwable t) {
            caughtError = true;
            return originalColor;
        }
    }
}