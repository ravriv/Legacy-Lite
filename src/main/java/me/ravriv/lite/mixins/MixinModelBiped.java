package me.ravriv.lite.mixins;

import net.minecraft.client.model.ModelBiped;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ModelBiped.class)
public abstract class MixinModelBiped {
    @ModifyConstant(method = "setRotationAngles", constant = @Constant(floatValue = 0.05F), require = 3)
    private float cancelIdleArmSway(float original) {
        return 0F;
    }

    @ModifyConstant(method = "setRotationAngles", constant = @Constant(floatValue = -0.5235988F))
    private float cancelRotation(float value) {
        return 0F;
    }
}