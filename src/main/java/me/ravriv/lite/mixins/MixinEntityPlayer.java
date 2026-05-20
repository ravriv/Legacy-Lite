package me.ravriv.lite.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {
    @Unique private float aBH = 1.62F;
    @Unique private long bCT = System.currentTimeMillis();
    @Unique private static final float sH = 1.54F;
    @Unique private static final float nH = 1.62F;
    @Unique private static final int fDY = 1000 / 60;

    @Overwrite
    public float getEyeHeight() {
        EntityPlayer ePL = (EntityPlayer) (Object) this;

        long t = System.currentTimeMillis();

        if (ePL.isPlayerSleeping()) {
            aBH = 0.2F;
        } else if (ePL.isSneaking()) {
            if (aBH > sH && t - bCT > fDY) {
                aBH -= 0.012F;
                bCT = t;
            }
        } else {
            if (aBH < nH && aBH > 0.2F && t - bCT > fDY) {
                aBH += 0.012F;
                bCT = t;
            } else if (aBH < 0.2F || aBH >= nH) {
                aBH = nH;
            }
        }

        return aBH;
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    private void cachePlayerDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        super.cacheDisplayName(cir);
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void returnCachedPlayerDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        super.returnCachedDisplayName(cir);
    }
}