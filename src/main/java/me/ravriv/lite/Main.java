package me.ravriv.lite;

import me.ravriv.lite.handlers.ParticleCullingHandler;
import me.ravriv.lite.utils.ClassTransformer;
import me.ravriv.lite.utils.ReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "lglt", name = "Legacy Lite", version = "1.0")
public class Main {
    public static ParticleCullingHandler cullThread;

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        resourceManager.registerReloadListener(new ReloadListener());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClassTransformer.initOptiFine();
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        cullThread = new ParticleCullingHandler();
        cullThread.start();
    }
}
