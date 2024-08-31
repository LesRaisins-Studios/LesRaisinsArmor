package me.xjqsh.lesraisinsarmor.network;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkHandler {
    private static final String VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(LesRaisinsArmor.MOD_ID, "network"),
            () -> VERSION, it -> it.equals(VERSION), it -> it.equals(VERSION));

    private static final AtomicInteger ID_COUNT = new AtomicInteger(1);

    public static void init() {
        CHANNEL.registerMessage(PacketId.S2C_ARMOR_DATA.value(),
                ArmorDataMessage.class,
                ArmorDataMessage::encode,
                ArmorDataMessage::decode,
                ArmorDataMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }


    public static void sendToClientPlayer(Object message, Player player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }

    public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
        if (world instanceof ServerLevel ws) {
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 192 * 192)
                    .forEach(p -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    public static void sendToNearby(Entity entity, Object toSend, int distance) {
        if (entity.level() instanceof ServerLevel ws) {
            BlockPos pos = entity.blockPosition();
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < distance * distance)
                    .forEach(p -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    public static void sendToNearby(Entity entity, Object toSend) {
        if (entity.level() instanceof ServerLevel ws) {
            BlockPos pos = entity.blockPosition();
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 192 * 192)
                    .forEach(p -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    enum PacketId {
        S2C_ARMOR_DATA(100),
        ;

        private final int id;

        PacketId(int id) {
            this.id = id;
        }

        int value() {
            return id;
        }

    }

}
