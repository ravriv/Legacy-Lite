package me.ravriv.lite.mixins.accessors;

import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Chunk.class)
public interface ChunkAccessor {
    @Invoker("relightBlock")
    void callRelightBlock(int x, int y, int z);
}