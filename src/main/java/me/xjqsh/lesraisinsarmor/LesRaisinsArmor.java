package me.xjqsh.lesraisinsarmor;

import me.xjqsh.lesraisinsarmor.init.ModCreativeTabs;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("lrarmor")
public class LesRaisinsArmor {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lrarmor";

    public LesRaisinsArmor(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::onClientSetup);

        ModItems.REGISTER.register(bus);
        ModEffects.REGISTER.register(bus);
        ModCreativeTabs.TABS.register(bus);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
//            GeoArmorRenderer.registerArmorRenderer(LrArmorItem.class, BedrockArmorRenderer::new);
        });
    }
}
