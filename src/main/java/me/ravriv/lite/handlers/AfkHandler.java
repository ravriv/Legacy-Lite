package me.ravriv.lite.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;

public class AfkHandler {
    private static boolean afk = false;
    private static float[] originalVolumes = null;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isAfk() {
        return afk;
    }

    public static void setAfk(boolean value) {
        afk = value;
    }

    public static void volumeState(boolean mute) {
        if (mute) {
            if (originalVolumes == null) {
                originalVolumes = new float[SoundCategory.values().length];
                for (SoundCategory cat : SoundCategory.values()) {
                    originalVolumes[cat.getCategoryId()] = mc.gameSettings.getSoundLevel(cat);
                }
            }
            for (SoundCategory cat : SoundCategory.values()) {
                mc.gameSettings.setSoundLevel(cat, 0.0F);
            }
        } else {
            if (originalVolumes != null) {
                for (SoundCategory cat : SoundCategory.values()) {
                    mc.gameSettings.setSoundLevel(cat, originalVolumes[cat.getCategoryId()]);
                }
                originalVolumes = null;
            }
        }
    }
}