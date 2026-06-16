package me.ravriv.lite.mixins.accessors;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "Config")
public interface ConfigAccessor {
    @Dynamic("OptiFine")
    @Invoker
    static boolean invokeIsCustomColors() {
        throw new AssertionError("Mixin did not inject");
    }
}
