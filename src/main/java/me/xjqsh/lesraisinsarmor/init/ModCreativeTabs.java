package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("all")
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LesRaisinsArmor.MOD_ID);
    public static final ResourceLocation icon = new ResourceLocation(LesRaisinsArmor.MOD_ID, "chemical_protective_chestplate");
    public static RegistryObject<CreativeModeTab> MAIN = TABS.register("other", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.lrarmor"))
            .icon(() -> ForgeRegistries.ITEMS.getValue(icon).getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ModItems.REGISTER.getEntries()) {
                    output.accept(item.get().getDefaultInstance());
                }
            }).build());
}
