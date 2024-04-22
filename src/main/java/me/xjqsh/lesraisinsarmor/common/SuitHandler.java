package me.xjqsh.lesraisinsarmor.common;

import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SuitHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.player.level.isClientSide()) return;
        if(event.player.tickCount % 10 != 0) return;

        Item item = event.player.getItemBySlot(EquipmentSlotType.CHEST).getItem();
        if (item instanceof LrArmorItem) {
            LrArmorItem armorItem = (LrArmorItem) item;
            if(armorItem.getSuitCount(event.player)==4){
                if("medical".equals(armorItem.getSuitIdf())){
                    if(event.player.getEffect(ModEffects.RESCUE_COOLDOWN.get())==null){
                        armorItem.applyEffect(event.player);
                    }
                }else {
                    armorItem.applyEffect(event.player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent event){
        if(event.getEntity().level.isClientSide()) return;
        if(event.getEntityLiving().hasEffect(ModEffects.HEAVY_ARMOR.get())){
            event.setAmount(event.getAmount()*0.85f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingHurtEvent event){
        if(event.getEntity().level.isClientSide() || !(event.getEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        if(player.getHealth() - event.getAmount() < player.getMaxHealth()*0.3 && player.hasEffect(ModEffects.RESCUE.get())){
            player.removeEffect(ModEffects.RESCUE.get());
            player.addEffect(new EffectInstance(ModEffects.RESCUE_COOLDOWN.get(), 600));
            player.addEffect(new EffectInstance(Effects.REGENERATION, 100,3));
        }
    }
}
