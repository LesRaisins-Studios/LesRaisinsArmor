package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.armor.LrArmorMaterial;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, LesRaisinsArmor.MOD_ID);

    static {
        registerInBatch("armored_chemical");
        registerInBatch("attacker", ModEffects.TOUGH);
        registerInBatch("chemical_protective");
        registerInBatch("defender", ModEffects.HEAVY_ARMOR);
        registerInBatch("medical", ModEffects.RESCUE);
        registerInBatch("scout", ModEffects.LIGHT_LEG);
        registerInBatch("sniper");
        registerInBatch("dea_armed");
        registerInBatch("dea");
        registerInBatch("atf", LrArmorMaterial.DEFAULT);
        registerInBatch("atf_vest", LrArmorMaterial.DEFAULT);
        registerInBatch("irs", LrArmorMaterial.DEFAULT);
        registerInBatch("fbi", LrArmorMaterial.DEFAULT);
        registerInBatch("fbi_armed", LrArmorMaterial.DEFAULT);
    }

    public static void registerInBatch(String name, LrArmorMaterial material){
        for (ArmorItem.Type slotType : ArmorItem.Type.values()){
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName,
                    ()->new LrArmorItem(name, material, slotType,
                            (new Item.Properties()),null));
        }
    }

    public static void registerInBatch(String name){
        for (ArmorItem.Type slotType : ArmorItem.Type.values()){
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName,
                    ()->new LrArmorItem(name, LrArmorMaterial.getByName(name), slotType,
                            (new Item.Properties()),null));
        }
    }

    public static void registerInBatch(String name, Supplier<MobEffect> supplier){
        for (ArmorItem.Type slotType : ArmorItem.Type.values()){
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName,
                    ()->new LrArmorItem(name,LrArmorMaterial.getByName(name), slotType,
                            (new Item.Properties()),supplier));
        }
    }

}
