package me.xjqsh.lesraisinsarmor.client;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = LesRaisinsArmor.MOD_ID)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onAddResourceListener(RegisterClientReloadListenersEvent event){
        event.registerReloadListener(ArmorRenderConfigManager.getInstance());
    }
}
