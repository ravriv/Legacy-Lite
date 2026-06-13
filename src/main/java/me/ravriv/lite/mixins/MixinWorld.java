package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(World.class)
public class MixinWorld {
    @Shadow @Final public boolean isRemote;

    @ModifyVariable(method = "updateEntityWithOptionalForce", at = @At("STORE"), ordinal = 1)
    private boolean updateEntityWithOptionalForce(boolean isForced) {
        return isForced && !this.isRemote;
    }

    @Inject(method = "getCollidingBoundingBoxes", at = @At("HEAD"), cancellable = true)
    private void getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        if (entityIn instanceof EntityTNTPrimed || entityIn instanceof EntityFallingBlock || entityIn instanceof EntityItem || entityIn instanceof EntityFX) {
            cir.setReturnValue(Collections.emptyList());
        }
    }

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void checkLightFor(CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getMinecraft().gameSettings.gammaSetting == 1.0F) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"getLightFromNeighborsFor", "getLightFromNeighbors", "getRawLight", "getLight(Lnet/minecraft/util/BlockPos;)I", "getLight(Lnet/minecraft/util/BlockPos;Z)I"}, at = @At("HEAD"), cancellable = true)
    private void getLight(CallbackInfoReturnable<Integer> cir) {
        if (Minecraft.getMinecraft().gameSettings.gammaSetting == 1.0F) {
            cir.setReturnValue(15);
        }
    }
}
