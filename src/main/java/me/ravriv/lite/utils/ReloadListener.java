package me.ravriv.lite.utils;

import me.ravriv.lite.handlers.FontRendererHandler;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances()) {
            enhancedFontRenderer.invalidateAll();
        }

        FontRendererHandler.forceRefresh = true;
    }
}
