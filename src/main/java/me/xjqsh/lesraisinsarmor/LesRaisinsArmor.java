package me.xjqsh.lesraisinsarmor;

import com.mojang.logging.LogUtils;
import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.init.ModCreativeTabs;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod("lrarmor")
public class LesRaisinsArmor {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "lrarmor";

    public LesRaisinsArmor(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
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
