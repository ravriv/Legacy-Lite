package me.ravriv.lite.mixins.accessors;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "net.optifine.CustomColors")
public interface CustomColorsAccessor {
    @Dynamic("OptiFine")
    @Invoker static int invokeGetTextColor(int index, int color) {
        throw new AssertionError("Mixin failed to inject into OptiFine");
    }
}
