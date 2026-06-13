package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.ravriv.lite.handlers.AfkHandler;
import static me.ravriv.lite.handlers.ZoomHandler.isZooming;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow private Minecraft mc;
    @Unique private boolean createdLightmap;
    @Unique private boolean wasZooming = false;
    @Unique private double zoomFOV = 30.0;
    @Unique private float originalSensitivity = -1F;

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private void onSetupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        GlStateManager.disableFog();
    }

    @Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
    private void renderStreamIndicator(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;viewBobbing:Z", ordinal = 0))
    private boolean setupCameraTransform(GameSettings instance) {
        return false;
    }

    @Redirect(method = "setupViewBobbing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 2))
    private void setupViewBobbing(float angle, float x, float y, float z) {}

    @ModifyConstant(method = "orientCamera", constant = @Constant(floatValue = -0.1F))
    private float orientCamera(float original) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return original;
        }
        return 0.05F;
    }

    @Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0))
    private void enablePolygonOffset(CallbackInfo ci) {
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-0.325F, -0.325F);
    }

    @Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0, shift = At.Shift.AFTER))
    private void disablePolygonOffset(CallbackInfo ci) {
        GlStateManager.disablePolygonOffset();
    }

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (AfkHandler.isAfk()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void cancelLightmapBuild(CallbackInfo ci) {
        if (mc.gameSettings.gammaSetting == 1.0F && this.createdLightmap) {
            ci.cancel();
        }
    }

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    private void setCreatedLightmap(CallbackInfo ci) {
        this.createdLightmap = true;
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void getFOVModifier(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir) {
        if (isZooming()) {
            cir.setReturnValue((float) zoomFOV);
        }
    }

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void updateCameraAndRender(float partialTicks, long nanoTime, CallbackInfo ci) {
        boolean zooming = isZooming();

        if (zooming) {
            if (!wasZooming) {
                zoomFOV = 30;
            }

            mc.gameSettings.smoothCamera = false;

            int scroll = Mouse.getDWheel();
            if (scroll > 0 && zoomFOV != 3) {
                zoomFOV -= 3;
            } else if (scroll < 0 && zoomFOV != 30) {
                zoomFOV += 3;
            }

            if (originalSensitivity == -1F) {
                originalSensitivity = mc.gameSettings.mouseSensitivity;
                mc.gameSettings.mouseSensitivity = originalSensitivity * 0.3333F;
            }
        } else {
            if (originalSensitivity != -1F) {
                mc.gameSettings.mouseSensitivity = originalSensitivity;
                originalSensitivity = -1F;
            }
        }
        wasZooming = zooming;
    }
}
