package me.ravriv.lite.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class ZoomHandler {
    private static KeyBinding zoomKey;

    public static boolean isZooming() {
        try {
            if (zoomKey == null) {
                zoomKey = (KeyBinding) GameSettings.class
                        .getField("ofKeyBindZoom")
                        .get(Minecraft.getMinecraft().gameSettings);
            }
            return GameSettings.isKeyDown(zoomKey);
        } catch (Exception e) {
            return false;
        }
    }
}