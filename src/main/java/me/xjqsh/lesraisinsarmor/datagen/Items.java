package me.xjqsh.lesraisinsarmor.datagen;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Items {
    @SubscribeEvent
    public static void dataGen(GatherDataEvent event) {
        event.getGenerator().addProvider(true, new ItemModels(event.getGenerator(), event.getExistingFileHelper()));
    }

    public static class ItemModels extends ItemModelProvider {
        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator.getPackOutput(), LesRaisinsArmor.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            for(Item item : ForgeRegistries.ITEMS.getValues()){
                if(item instanceof LrArmorItem){
                    getBuilder(item.toString())
                            .parent(getExistingFile(mcLoc("item/generated")))
                            .texture("layer0", "item/"+ item);
                }
            }

        }
    }
}
