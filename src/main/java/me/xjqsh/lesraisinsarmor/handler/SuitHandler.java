package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
        if(!CommonConfig.enableArmorSetEffect.get()) return;

        if(event.player.level().isClientSide()) return;
        if(event.player.tickCount % 10 != 0) return;

        Item item = event.player.getItemBySlot(EquipmentSlot.CHEST).getItem();
        if (item instanceof LrArmorItem armorItem) {
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
        if(event.getEntity().level().isClientSide()) return;
        if(event.getEntity().hasEffect(ModEffects.HEAVY_ARMOR.get())){
            event.setAmount(event.getAmount()*0.85f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingHurtEvent event){
        if(event.getEntity().level().isClientSide() || !(event.getEntity() instanceof Player player)) return;
        if(player.getHealth() - event.getAmount() < player.getMaxHealth()*0.3 && player.hasEffect(ModEffects.RESCUE.get())){
            player.removeEffect(ModEffects.RESCUE.get());
            player.addEffect(new MobEffectInstance(ModEffects.RESCUE_COOLDOWN.get(), 600));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100,3));
        }
    }
}
