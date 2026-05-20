package me.ravriv.lite.mixins;

import me.ravriv.lite.handlers.CullCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFX.class)
public class MixinEntityFX implements CullCheck {
    @Unique
    private boolean culled;

    @Override
    public void setCulled(boolean culled) {
        this.culled = culled;
    }

    @Override
    public boolean isCulled() {
        return culled;
    }

    @Redirect(method = "renderParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EntityFX;getBrightnessForRender(F)I"))
    private int renderParticle(EntityFX entityFX, float partialTicks) {
        return 15728880;
    }
}
