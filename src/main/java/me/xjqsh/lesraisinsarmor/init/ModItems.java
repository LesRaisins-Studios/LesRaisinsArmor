package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.armor.LrArmorMaterial;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ModItems {
    public static final ItemGroup LR_ARMOR = new ItemGroup(LesRaisinsArmor.MOD_ID) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            //noinspection DataFlowIssue
            return new ItemStack(ForgeRegistries.ITEMS.getValue(icon));
        }
    };
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, LesRaisinsArmor.MOD_ID);
    public static final ResourceLocation icon = new ResourceLocation(LesRaisinsArmor.MOD_ID, "chemical_protective_chest");
    public static final EquipmentSlotType[] armorSlots = new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};



    static {
        registerInBatch("armored_chemical");
        registerInBatch("attacker", ModEffects.TOUGH);
        registerInBatch("chemical_protective");
        registerInBatch("defender", ModEffects.HEAVY_ARMOR);
        registerInBatch("medical", ModEffects.RESCUE);
        registerInBatch("scout", ModEffects.LIGHT_LEG);
        registerInBatch("sniper");
    }

    public static void registerInBatch(String name){
        for (EquipmentSlotType slotType : armorSlots){
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName,
                    ()->new LrArmorItem(name, LrArmorMaterial.getByName(name), slotType,
                            (new Item.Properties()).tab(LR_ARMOR),null));
        }
    }

    public static void registerInBatch(String name, Supplier<Effect> supplier){
        for (EquipmentSlotType slotType : armorSlots){
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName,
                    ()->new LrArmorItem(name,LrArmorMaterial.getByName(name), slotType,
                            (new Item.Properties()).tab(LR_ARMOR),supplier));
        }
    }

}
