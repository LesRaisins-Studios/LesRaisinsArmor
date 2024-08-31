package me.xjqsh.lesraisinsarmor.client;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorPartRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelPart.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        Player player = event.getEntity();
        resolveVisibleParts(player, EquipmentSlot.HEAD, model);
        resolveVisibleParts(player, EquipmentSlot.CHEST, model);
        resolveVisibleParts(player, EquipmentSlot.LEGS, model);
        resolveVisibleParts(player, EquipmentSlot.FEET, model);
    }

    private static void resolveVisibleParts(Player player, EquipmentSlot slot, PlayerModel<AbstractClientPlayer> model) {
        Item item = player.getItemBySlot(slot).getItem();
        if(item instanceof LrArmorItem lrArmor){
            ArmorRenderConfig config = getConfig(lrArmor);
            if (config == null) return;
            switch (slot) {
                case HEAD -> setVisibility(config.helmet, model);
                case CHEST -> setVisibility(config.chestplate, model);
                case LEGS -> setVisibility(config.leggings, model);
                case FEET -> setVisibility(config.boots, model);
            }
        }
    }

    private static void setVisibility(ArmorPartRenderConfig config, PlayerModel<AbstractClientPlayer> model) {
        if (config == null) return;
        for (var part : config.hideParts) {
            switch (part){
                case HEAD -> model.head.visible = false;
                case HAT -> model.hat.visible = false;
                case BODY -> model.body.visible = false;
                case JACKET -> model.jacket.visible = false;
                case RIGHT_ARM -> model.rightArm.visible = false;
                case RIGHT_SLEEVE -> model.rightSleeve.visible = false;
                case LEFT_ARM -> model.leftArm.visible = false;
                case LEFT_SLEEVE -> model.leftSleeve.visible = false;
                case RIGHT_LEG -> model.rightLeg.visible = false;
                case RIGHT_PANTS -> model.rightPants.visible = false;
                case LEFT_LEG -> model.leftLeg.visible = false;
                case LEFT_PANTS -> model.leftPants.visible = false;
            }
        }
    }

    private static ArmorRenderConfig getConfig(LrArmorItem lrArmor) {
        return ArmorRenderConfigManager.getInstance().getConfig(ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, lrArmor.getSuitIdf()));
    }
}
