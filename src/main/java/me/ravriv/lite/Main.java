package me.ravriv.lite;

import me.ravriv.lite.handlers.CullThread;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(modid = "lglt", name = "Legacy Lite", version = "1.0")
public class Main {
    public static CullThread cullThread;

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        cullThread = new CullThread();
        cullThread.start();
    }
}