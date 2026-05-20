package me.ravriv.lite.mixins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderItemFrame.class)
public class MixinRenderItemFrame {
    @ModifyArg(method = "renderName(Lnet/minecraft/entity/item/EntityItemFrame;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;color(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;"), index = 3)
    private float removeNametagBackground(float alpha) {
        return 0F;
    }

    @Redirect(method = "renderName(Lnet/minecraft/entity/item/EntityItemFrame;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int renderWithShadow(FontRenderer fontRenderer, String text, int x, int y, int color) {
        GL11.glDepthMask(false);
        int render = fontRenderer.drawString(text, x + 1, y + 1, color, true);
        GL11.glDepthMask(true);
        render = Math.max(render, fontRenderer.drawString(text, x, y, color, false));
        return render;
    }
}