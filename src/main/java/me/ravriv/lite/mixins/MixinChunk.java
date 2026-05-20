package me.ravriv.lite.mixins;

import me.ravriv.lite.mixins.accessors.ChunkAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class)
public class MixinChunk {
    @Shadow @Final private ExtendedBlockStorage[] storageArrays;

    @Inject(method = {"getLightFor", "getLightSubtracted"}, at = @At("HEAD"), cancellable = true)
    private void getLight(CallbackInfoReturnable<Integer> cir) {
        if (Minecraft.getMinecraft().gameSettings.gammaSetting == 1.0F) {
            cir.setReturnValue(15);
        }
    }

    @Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V"))
    private void skipRelight(Chunk instance, int x, int y, int z) {
        if (Minecraft.getMinecraft().gameSettings.gammaSetting != 1.0F) {
            ((ChunkAccessor) instance).callRelightBlock(x, y, z);
        }
    }

    @ModifyArg(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V", ordinal = 0), index = 1)
    private int setBlockState(int y) {
        return y - 1;
    }

    @Overwrite
    public IBlockState getBlockState(BlockPos pos) {
        final int y = pos.getY();

        if (y >= 0 && (y >> 4) < this.storageArrays.length) {
            final ExtendedBlockStorage storage = this.storageArrays[y >> 4];
            if (storage != null) {
                return storage.get(pos.getX() & 15, y & 15, pos.getZ() & 15);
            }
        }

        return Blocks.air.getDefaultState();
    }
}