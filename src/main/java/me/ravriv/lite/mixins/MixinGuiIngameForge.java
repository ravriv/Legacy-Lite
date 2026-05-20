package me.ravriv.lite.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void enableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.enableAlpha();
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void disableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.disableAlpha();
    }

    @Redirect(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_int(III)I"))
    private int forceFullAlpha(int value, int min, int max) {
        return max;
    }

    @Redirect(method = "renderCrosshairs", slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=775", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;tryBlendFuncSeparate(IIII)V", ordinal = 0))
    private void renderCrosshairs(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {}
}