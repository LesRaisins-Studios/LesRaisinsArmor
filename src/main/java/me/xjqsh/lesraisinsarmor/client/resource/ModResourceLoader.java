package me.xjqsh.lesraisinsarmor.client.resource;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.model.BedrockArmorModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v1.resource.RawResourceLoaders;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = LesRaisinsArmor.MOD_ID)
public class ModResourceLoader {

    private static final List<String> SUIT_IDS = List.of(
            "armored_chemical", "attacker", "chemical_protective", "defender",
            "medical", "scout", "sniper",
            "dea", "dea_armed", "atf", "atf_vest",
            "irs", "fbi", "fbi_armed", "joker", "joker_armed"
    );

    @SubscribeEvent
    public static void onModelRegister(RegisterBedrockModelEvent event) {
        for (String suitId : SUIT_IDS) {
            event.register(
                    new ResourceLocation(LesRaisinsArmor.MOD_ID, suitId + ".geo"),
                    RawResourceLoaders.COMMON_LOADER,
                    BedrockArmorModel::new
            );
        }
    }
}
