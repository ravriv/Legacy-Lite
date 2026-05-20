package me.ravriv.lite.mixins;

import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Profiler.Result;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collections;
import java.util.List;

@Mixin(Profiler.class)
public class MixinProfiler {
    @Overwrite
    public void startSection(String name) {}

    @Overwrite
    public void endSection() {}

    @Overwrite
    public void endStartSection(String name) {}

    @Overwrite
    public void clearProfiling() {}

    @Overwrite
    public String getNameOfLastSection() {
        return "root";
    }

    @Overwrite
    public List<Result> getProfilingData(String unused) {
        return Collections.emptyList();
    }
}