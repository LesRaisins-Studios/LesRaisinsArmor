package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
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

        Player player = event.player;
        Item item = player.getItemBySlot(EquipmentSlot.CHEST).getItem();

        if (item instanceof LrArmorItem armorItem) {
            if(armorItem.getSuitCount(player)==4){
                // 套装完整：应用效果
                if("medical".equals(armorItem.getSuitIdf())){
                    if(player.getEffect(ModEffects.RESCUE_COOLDOWN.get())==null){
                        armorItem.applyEffect(player);
                    }
                }else {
                    armorItem.applyEffect(player);
                }
            } else {
                // 套装不完整：移除该套装的效果
                armorItem.removeEffect(player);
            }
        } else {
            // 胸甲部位不是模组盔甲：移除所有套装效果
            for (MobEffect effect : SuitEffect.getAllSuitEffects()) {
                if (player.hasEffect(effect)) {
                    player.removeEffect(effect);
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
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3));
        }
    }
}
