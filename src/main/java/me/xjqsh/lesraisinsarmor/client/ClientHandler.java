package me.xjqsh.lesraisinsarmor.client;

import com.google.common.collect.ImmutableList;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorPartRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientHandler {
//    @SubscribeEvent(priority = EventPriority.LOW)
//    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
//        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
//        Player player = event.getEntity();
//        resolveVisibleParts(player, EquipmentSlot.HEAD, model);
//        resolveVisibleParts(player, EquipmentSlot.CHEST, model);
//        resolveVisibleParts(player, EquipmentSlot.LEGS, model);
//        resolveVisibleParts(player, EquipmentSlot.FEET, model);
//    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre<?,?> event) {
        LivingEntity living = event.getEntity();
        EntityModel<?> model = event.getRenderer().getModel();
        if (model instanceof PlayerModel<?> playerModel){
            resolveVisibleParts(living, EquipmentSlot.HEAD, playerModel);
            resolveVisibleParts(living, EquipmentSlot.CHEST, playerModel);
            resolveVisibleParts(living, EquipmentSlot.LEGS, playerModel);
            resolveVisibleParts(living, EquipmentSlot.FEET, playerModel);
        } else if (model instanceof HumanoidModel<?> humanoidModel){
            resolveVisibleParts(living, EquipmentSlot.HEAD, humanoidModel);
            resolveVisibleParts(living, EquipmentSlot.CHEST, humanoidModel);
            resolveVisibleParts(living, EquipmentSlot.LEGS, humanoidModel);
            resolveVisibleParts(living, EquipmentSlot.FEET, humanoidModel);
        }
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Post<?,?> event) {
        EntityModel<?> model = event.getRenderer().getModel();
        if (model instanceof PlayerModel<?> playerModel){
            playerBodyParts(playerModel).forEach(part -> {
                if (part!=null) part.visible = true;
            });
        } else if (model instanceof HumanoidModel<?> humanoidModel){
            bodyParts(humanoidModel).forEach(part -> {
                if (part!=null) part.visible = true;
            });
        }
    }

    private static Iterable<ModelPart> playerBodyParts(PlayerModel<?> playerModel) {
        return ImmutableList.of(playerModel.body, playerModel.rightArm, playerModel.leftArm,
                playerModel.rightLeg, playerModel.leftLeg, playerModel.head, playerModel.hat, playerModel.jacket,
                playerModel.rightSleeve, playerModel.leftSleeve, playerModel.rightPants, playerModel.leftPants);
    }

    private static Iterable<ModelPart> bodyParts(HumanoidModel<?> humanoidModel) {
        return ImmutableList.of(humanoidModel.body, humanoidModel.rightArm, humanoidModel.leftArm,
                humanoidModel.rightLeg, humanoidModel.leftLeg, humanoidModel.head, humanoidModel.hat);
    }

    public static void resolveVisibleParts(LivingEntity living, EquipmentSlot slot, HumanoidModel<?> model) {
        Item item = living.getItemBySlot(slot).getItem();
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

    private static void setVisibility(ArmorPartRenderConfig config, HumanoidModel<?> model) {
        if (config == null) return;
        for (var part : config.hideParts) {
            switch (part){
                case HEAD -> model.head.visible = false;
                case HAT -> model.hat.visible = false;
                case BODY -> model.body.visible = false;
                case RIGHT_ARM -> model.rightArm.visible = false;
                case LEFT_ARM -> model.leftArm.visible = false;
                case RIGHT_LEG -> model.rightLeg.visible = false;
                case LEFT_LEG -> model.leftLeg.visible = false;
            }
        }
    }

    public static void resolveVisibleParts(LivingEntity player, EquipmentSlot slot, PlayerModel<?> model) {
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

    private static void setVisibility(ArmorPartRenderConfig config, PlayerModel<?> model) {
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
