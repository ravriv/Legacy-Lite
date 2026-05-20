package me.ravriv.lite.handlers;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class TooltipHandler {
    private static int sY = 0;
    public static boolean s;

    public static void push() {
        GlStateManager.pushMatrix();
    }

    public static void pop() {
        GlStateManager.popMatrix();
    }

    public static void transform(int tY, int tH, int sH) {
        if (tY < 0 && !s) {
            sY = -tY + 6;
        }

        s = tY < 0;

        if (s) {
            int w = Mouse.getDWheel();
            
            sY += (w < 0 ? -10 : (w > 0 ? 10 : 0));
            
            if (sY + tY > 6) {
                sY = -tY + 6;
            } else if (sY + tY + tH + 6 < sH) {
                sY = sH - 6 - tY - tH;
            }
        }
        GlStateManager.translate(0, sY, 0);
    }
}