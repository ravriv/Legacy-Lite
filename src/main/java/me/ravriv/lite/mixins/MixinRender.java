package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Render.class)
public abstract class MixinRender {
    @ModifyArg(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 1), index = 0)
    private float renderLivingLabel(float angle) {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -angle : angle;
    }
}