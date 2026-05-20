package me.ravriv.lite.mixins;

import me.ravriv.lite.mixins.accessors.WorldRendererAccessor;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TexturedQuad.class)
public class MixinTexturedQuad {
    @Unique private boolean drawOnSelf;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"))
    private void beginDraw(WorldRenderer renderer, int glMode, VertexFormat format) {
        this.drawOnSelf = !((WorldRendererAccessor) renderer).isDrawing();
        if (this.drawOnSelf) {
            renderer.begin(glMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void endDraw(Tessellator tessellator) {
        if (this.drawOnSelf) {
            tessellator.draw();
        }
    }
}