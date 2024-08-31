package me.xjqsh.lesraisinsarmor.compat;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorPartRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.client.mesh.HumanoidMesh;

public class EpicFightCompat {
    public static void init(){
        MinecraftForge.EVENT_BUS.register(new EpicFightCompat());
    }

    @SubscribeEvent
    public void pre(PrepareModelEvent event) {
        if(event.getMesh() instanceof HumanoidMesh mesh) {
            if(event.getEntityPatch().getOriginal() instanceof Player player) {
                resolveVisibleParts(player, EquipmentSlot.HEAD, mesh);
                resolveVisibleParts(player, EquipmentSlot.CHEST, mesh);
                resolveVisibleParts(player, EquipmentSlot.LEGS, mesh);
                resolveVisibleParts(player, EquipmentSlot.FEET, mesh);
            }
        }
    }

    private static void resolveVisibleParts(Player player, EquipmentSlot slot, HumanoidMesh model) {
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

    private static void setVisibility(ArmorPartRenderConfig config, HumanoidMesh model) {
        if (config == null) return;
        for (var part : config.hideParts) {
            switch (part){
                case HEAD -> model.head.setHidden(true);
                case HAT -> model.hat.setHidden(true);
                case BODY -> model.torso.setHidden(true);
                case JACKET -> model.jacket.setHidden(true);
                case RIGHT_ARM -> model.rightArm.setHidden(true);
                case RIGHT_SLEEVE -> model.rightSleeve.setHidden(true);
                case LEFT_ARM -> model.leftArm.setHidden(true);
                case LEFT_SLEEVE -> model.leftSleeve.setHidden(true);
                case RIGHT_LEG -> model.rightLeg.setHidden(true);
                case RIGHT_PANTS -> model.rightPants.setHidden(true);
                case LEFT_LEG -> model.leftLeg.setHidden(true);
                case LEFT_PANTS -> model.leftPants.setHidden(true);
            }
        }
    }

    private static ArmorRenderConfig getConfig(LrArmorItem lrArmor) {
        return ArmorRenderConfigManager.getInstance().getConfig(ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, lrArmor.getSuitIdf()));
    }
}
