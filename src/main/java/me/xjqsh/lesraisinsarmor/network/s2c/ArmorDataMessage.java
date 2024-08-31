package me.xjqsh.lesraisinsarmor.network.s2c;

import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ArmorDataMessage {
    private final Map<ResourceLocation, String> networkCache;
    public ArmorDataMessage(Map<ResourceLocation, String> networkCache) {
        this.networkCache = networkCache;
    }
    public static void encode(ArmorDataMessage message, FriendlyByteBuf buf) {
        buf.writeMap(message.networkCache, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeUtf);
    }

    public static ArmorDataMessage decode(FriendlyByteBuf buf) {
        return new ArmorDataMessage(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readUtf));
    }

    public static void handle(ArmorDataMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> onReceive(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onReceive(ArmorDataMessage message) {
        ArmorDataManager.fromNetwork(message.networkCache);
    }
}
