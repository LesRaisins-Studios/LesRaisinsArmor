package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModSetupHandler {
    @SubscribeEvent
    public static void onServerResourceReload(AddReloadListenerEvent event) {
        event.addListener(ArmorDataManager.getInstance());
    }
}
