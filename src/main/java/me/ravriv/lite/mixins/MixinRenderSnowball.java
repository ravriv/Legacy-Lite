package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSnowball.class)
public class MixinRenderSnowball<T extends Entity> {
    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entity.ticksExisted < 2) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 1), index = 0)
    private float doRender(float angle) {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -angle : angle;
    }
}