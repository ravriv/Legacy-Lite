package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {
    @Shadow @Final private Minecraft mc;

    @Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
    private void drawPing(int offset, int xPosition, int yPosition, NetworkPlayerInfo info, CallbackInfo ci) {
        int ping = info.getResponseTime();
        int x = (xPosition + offset) - (mc.fontRendererObj.getStringWidth(String.valueOf(ping)) >> 1) - 2;
        int y = yPosition + 2;

        int color;

        if (ping > 500) {
            color = 11141120;
        } else if (ping > 300) {
            color = 11184640;
        } else if (ping > 200) {
            color = 11193344;
        } else if (ping > 135) {
            color = 2128640;
        } else if (ping > 70) {
            color = 39168;
        } else if (ping >= 0) {
            color = 47872;
        } else {
            color = 11141120;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        mc.fontRendererObj.drawStringWithShadow("   " + (ping == 0 ? "?" : ping), (2 * x) - 10, 2 * y, color);
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
        ci.cancel();
    }
}