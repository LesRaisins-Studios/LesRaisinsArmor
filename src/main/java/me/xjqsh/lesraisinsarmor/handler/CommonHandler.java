package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static me.xjqsh.lesraisinsarmor.network.NetworkHandler.CHANNEL;

@Mod.EventBusSubscriber
public class CommonHandler {
    @SubscribeEvent
    public static void onServerResourceReload(AddReloadListenerEvent event) {
        event.addListener(ArmorSuitManager.getInstance());
    }

    @SubscribeEvent
    public static void onServerResourceSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer s : event.getPlayerList().getPlayers()) {
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> s),
                        new ArmorDataMessage(ArmorSuitManager.getInstance().getNetworkCache()));
            }
        } else {
            CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer),
                    new ArmorDataMessage(ArmorSuitManager.getInstance().getNetworkCache()));
        }

    }
}
