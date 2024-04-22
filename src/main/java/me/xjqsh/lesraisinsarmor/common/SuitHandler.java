package me.xjqsh.lesraisinsarmor.common;

import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
                armorItem.applyEffect(event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent event){
        if(event.getEntity().level.isClientSide()) return;
        if(event.getEntityLiving().hasEffect(ModEffects.HEAVY_ARMOR.get())){
            event.setAmount(event.getAmount()*0.85f);
        }
    }
}
