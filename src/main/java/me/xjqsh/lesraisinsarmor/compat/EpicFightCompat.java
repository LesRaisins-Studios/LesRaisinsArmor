package me.xjqsh.lesraisinsarmor.compat;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorPartRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;

public class EpicFightCompat {
    public static void init(){
        if (FMLEnvironment.dist.isClient()) {
            LesRaisinsArmor.LOGGER.info("Setup EpicFight Compat");
            MinecraftForge.EVENT_BUS.register(new EpicFightCompat());
        }
    }

    @SubscribeEvent
    public void pre(PrepareModelEvent event) {
        if(event.getMesh() instanceof HumanoidMesh mesh) {
            LivingEntity living = event.getEntityPatch().getOriginal();
            if(living != null) {
                resolveVisibleParts(living, EquipmentSlot.HEAD, mesh);
                resolveVisibleParts(living, EquipmentSlot.CHEST, mesh);
                resolveVisibleParts(living, EquipmentSlot.LEGS, mesh);
                resolveVisibleParts(living, EquipmentSlot.FEET, mesh);
            }
        }
    }

    private static void resolveVisibleParts(LivingEntity player, EquipmentSlot slot, HumanoidMesh model) {
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
                case HEAD -> setHidden(model.head);
                case HAT -> setHidden(model.hat);
                case BODY -> setHidden(model.torso);
                case LEFT_ARM -> setHidden(model.leftArm);
                case RIGHT_ARM -> setHidden(model.rightArm);
                case LEFT_LEG -> setHidden(model.leftLeg);
                case RIGHT_LEG -> setHidden(model.rightLeg);
                case JACKET -> setHidden(model.jacket);
                case LEFT_SLEEVE -> setHidden(model.leftSleeve);
                case RIGHT_SLEEVE -> setHidden(model.rightSleeve);
                case LEFT_PANTS -> setHidden(model.leftPants);
                case RIGHT_PANTS -> setHidden(model.rightPants);
            }
        }
    }

    private static void setHidden(@Nullable AnimatedMesh.AnimatedModelPart part) {
        if (part != null) {
            part.setHidden(true);
        }
    }

    private static ArmorRenderConfig getConfig(LrArmorItem lrArmor) {
        return ArmorRenderConfigManager.getInstance().getConfig(ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, lrArmor.getSuitIdf()));
    }
}
