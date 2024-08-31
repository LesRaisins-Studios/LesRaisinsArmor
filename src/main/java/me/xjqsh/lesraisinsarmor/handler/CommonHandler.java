package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        event.addListener(ArmorDataManager.getInstance());
    }

    @SubscribeEvent
    public static void onServerResourceReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer s : event.getPlayerList().getPlayers()) {
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> s),
                        new ArmorDataMessage(ArmorDataManager.getInstance().getNetworkCache()));
            }
        } else {
            CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer),
                    new ArmorDataMessage(ArmorDataManager.getInstance().getNetworkCache()));
        }

    }
}
