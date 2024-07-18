package me.xjqsh.lesraisinsarmor.client;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre e) {
        Item item = e.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem();
        if(item instanceof LrArmorItem && ((LrArmorItem) item).isHideArm()){
            e.getRenderer().getModel().leftArm.visible = false;
            e.getRenderer().getModel().rightArm.visible = false;
        }
    }
}
