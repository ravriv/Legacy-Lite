package me.ravriv.lite.mixins;

import me.ravriv.lite.handlers.CameraHolder;
import me.ravriv.lite.handlers.ScanLimiter;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal implements CameraHolder {
    @Unique
    private ICamera camera;

    @Inject(method = "setupTerrain", at = @At("HEAD"))
    private void particleculling$setCamera(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo callback) {
        this.camera = camera;
    }

    @Override
    public ICamera getCamera() {
        return camera;
    }

    @ModifyVariable(method = "getVisibleFacings", name = "visgraph", at = @At(value = "STORE", ordinal = 0))
    private VisGraph getVisibleFacings(VisGraph visgraph) {
        ((ScanLimiter) visgraph).setLimitScan(true);
        return visgraph;
    }
}