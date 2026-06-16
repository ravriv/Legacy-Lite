package me.ravriv.lite.utils;

import net.minecraft.client.gui.FontRenderer;

public class OptifineHook {

    public float getCharWidth(FontRenderer renderer, char c) {
        return renderer.getCharWidth(c);
    }

    public float getOptifineBoldOffset(FontRenderer renderer) {
        return 1;
    }
}
